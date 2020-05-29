package com.jy.rock.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.jy.rock.domain.Attachment;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
public interface AttachmentDao extends CurdMapper<Attachment, String> {

    /**
     * 根据附件关联的记录的Id和附件类别查询属于该记录的所有附件（不包含附件内容）
     *
     * @param recorderId     记录ID
     * @param attachmentType 附件类别
     * @return 属于该记录的所有附件的ID编号
     */
    default List<Attachment> getAttachmentsByRecorderIdAndAttachmentType(@NotNull String recorderId, @Nullable AttachmentType attachmentType) {
        Weekend<Attachment> weekend = Weekend.of(Attachment.class);
        WeekendCriteria<Attachment, Object> criteria = weekend.weekendCriteria().andEqualTo(Attachment::getRecorderId, recorderId);
        if (attachmentType != null) {
            criteria.andEqualTo(Attachment::getType, attachmentType);
        }
        weekend.selectProperties("id", "name", "type", "contentType", "recorderType", "recorderId", "size");
        return this.selectByExample(weekend);
    }

    @Nullable
    default Attachment getWithoutContent(@NotNull String id) {
        Weekend<Attachment> weekend = Weekend.of(Attachment.class);
        weekend.weekendCriteria().andEqualTo(BaseDomainWithGuidKey::getId, id);
        weekend.selectProperties("id", "name", "type", "contentType", "recorderType", "recorderId", "size");
        return this.selectOneByExample(weekend);
    }

    /**
     * 插入之前会检查content，对于附件内容是空的记录不会插入
     *
     * @param recordList 要插入的记录
     * @return 插入成功的记录
     */
    @Override
    default int insert(@NotNull List<Attachment> recordList) {
        int result = 0;
        for (Attachment attachment : recordList) {
            if (attachment.getContent() != null && attachment.getContent().length > 0) {
                this.insert(attachment);
                result++;
            }
        }
        return result;
    }

    /**
     * 插入，对于有id的记录，会检查是否这个附件已经存在于数据库中
     *
     * @param recorderList            要插入的记录
     * @param existsAttachmentHandler 对于已经存在于数据库中的记录的处理函数，第一个参数：新记录，第二个参数：数据库中的记录，返回值：要插入的记录，如果为null，表示不插入
     * @return 插入成功的记录数
     */
    default int insert(@NotNull List<Attachment> recorderList, @NotNull BiFunction<Attachment, Attachment, Attachment> existsAttachmentHandler) {
        List<Attachment> existsAttachments = recorderList.stream().filter(n -> !Strings.isNullOrEmpty(n.getId())).collect(Collectors.toList());

        List<Attachment> insertItems = recorderList.stream().filter(n -> Strings.isNullOrEmpty(n.getId())).collect(Collectors.toList());

        for (Attachment newAttachment : existsAttachments) {
            Attachment oldAttachment = this.selectByPrimaryKey(newAttachment.getId());
            Attachment recorderToInsert = existsAttachmentHandler.apply(newAttachment, oldAttachment);
            if (recorderToInsert != null) {
                insertItems.add(recorderToInsert);
            }
        }

        return this.insert(insertItems);
    }

    /**
     * 更新指定记录的附件，会自动比对出新增的附件和删除的附件
     *
     * @param recorderId     记录ID
     * @param attachmentType 附件类别
     * @param newAttachments 该记录本次传入的附件
     */
    default void updateRecorderAttachments(@NotNull String recorderId, @Nullable AttachmentType attachmentType, @NotNull List<Attachment> newAttachments) {
        this.updateRecorderAttachments(recorderId, attachmentType, newAttachments, null);
    }

    default void updateRecorderAttachments(@NotNull String recorderId, @Nullable AttachmentType attachmentType, @NotNull List<Attachment> newAttachments, @Nullable BiFunction<Attachment, Attachment, Attachment> existsAttachmentHandler) {
        // 查询出该记录的附件
        Set<String> oldAttachmentIds = this.getAttachmentsByRecorderIdAndAttachmentType(recorderId, attachmentType)
                .stream().map(BaseDomainWithGuidKey::getId).collect(Collectors.toSet());

        // 如果附件的ID不为空，代表这个附件是存在在数据库中的
        Set<String> newAttachmentIds = newAttachments.stream().filter(i -> !Strings.isNullOrEmpty(i.getId()))
                .map(Attachment::getId).collect(Collectors.toSet());

        // 比对出删除掉的附件
        Sets.SetView<String> deleteAttachments = Sets.difference(oldAttachmentIds, newAttachmentIds);
        for (String attachment : deleteAttachments) {
            this.deleteByPrimaryKey(attachment);
        }

        if (existsAttachmentHandler != null) {
            // 比对出新增的附件，但是这些附件不是上传上来的，而是本来就存在于数据库中的
            Sets.SetView<String> attachmentNotUploadToAdd = Sets.difference(newAttachmentIds, oldAttachmentIds);
            List<Attachment> insertItems = new ArrayList<>(attachmentNotUploadToAdd.size());
            for (String id : attachmentNotUploadToAdd) {
                Attachment newAttachment = newAttachments.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst().orElse(null);
                assert newAttachment != null;
                Attachment oldAttachment = this.selectByPrimaryKey(id);
                Attachment recorderToInsert = existsAttachmentHandler.apply(newAttachment, oldAttachment);
                insertItems.add(recorderToInsert);
            }
            if (!insertItems.isEmpty()) {
                this.insert(insertItems);
            }
        }

        // 对于ID为空的附件，插入这个附件
        List<Attachment> attachmentsToAdd = newAttachments.stream().filter(i -> Strings.isNullOrEmpty(i.getId())).collect(Collectors.toList());
        attachmentsToAdd.forEach(i -> i.setRecorderId(recorderId));
        this.insert(attachmentsToAdd);
    }

    /**
     * 根据附件所属记录ID删除所有属于该记录的附件
     *
     * @param recorderId 记录ID
     */
    default void deleteByRecorderId(@NotNull String recorderId) {
        Weekend<Attachment> weekend = Weekend.of(Attachment.class);
        weekend.weekendCriteria().andEqualTo(Attachment::getRecorderId, recorderId);
        this.deleteByExample(weekend);
    }
}
