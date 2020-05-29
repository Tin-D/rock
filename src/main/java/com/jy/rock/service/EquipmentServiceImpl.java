package com.jy.rock.service;

import com.google.common.collect.ImmutableList;
import com.jy.rock.bean.equipment.EquipmentQueryVO;
import com.jy.rock.bean.equipment.EquipmentVO;
import com.jy.rock.dao.AttachmentDao;
import com.jy.rock.dao.EquipmentDao;
import com.jy.rock.domain.Attachment;
import com.jy.rock.domain.Equipment;
import com.jy.rock.enums.AttachmentRecorderType;
import com.jy.rock.enums.AttachmentType;
import com.jy.rock.enums.EquipmentType;
import com.xmgsd.lan.gwf.domain.User;
import com.xmgsd.lan.gwf.service.DictionaryCodeServiceImpl;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class EquipmentServiceImpl extends BaseService<EquipmentDao>
        implements SimpleCurdViewService<String, EquipmentVO>, PaginationService<EquipmentQueryVO, EquipmentVO> {
    @Autowired
    private DictionaryCodeServiceImpl dictionaryCodeService;

    @Autowired
    private AttachmentDao attachmentDao;

    @Override
    public BasePaginationMapper<EquipmentVO> getPaginationMapper() {
        return this.getMapper();
    }

    @Nullable
    @Override
    public EquipmentVO get(@NotNull String id) {
        List<EquipmentVO> items = this.getMapper().findPaginationResultByIds(ImmutableList.of(id), null);
        return items.isEmpty() ? null : items.get(0);
    }

    @NotNull
    private List<Attachment> parseAttachments(@NotNull String equipmentId, @NotNull EquipmentVO item, @NotNull User user) {
        List<Attachment> attachments = Collections.emptyList();
        if (!CollectionUtils.isEmpty(item.getAttachments())) {
            attachments = item.getAttachments().stream().map(fvo -> {
                Attachment attachment = new Attachment(fvo, AttachmentRecorderType.Equipment, user);
                attachment.setRecorderId(equipmentId);
                attachment.setRecorderType(Equipment.class.getSimpleName());
                attachment.setType(AttachmentType.equipmentFile);
                return attachment;
            }).collect(Collectors.toList());
        }

        return attachments;
    }

    @Transactional(rollbackFor = Exception.class)
    public EquipmentVO add(@NotNull EquipmentVO item, @NotNull User user) throws Exception {
        Equipment equipment = item.toDbInsertItem();
        this.getMapper().insert(equipment);

        List<Attachment> attachments = this.parseAttachments(equipment.getId(), item, user);
        if (!attachments.isEmpty()) {
            this.attachmentDao.insert(attachments);
        }

        EquipmentType equipmentType = EquipmentType.valueOf(dictionaryCodeService.get(equipment.getTypeId()).getCode());
        switch (equipmentType) {
            case ups:
                break;
            case Battery:
                break;
            default:
                throw new IllegalArgumentException("not support equipmentType: " + equipmentType);
        }

        return this.getOrError(equipment.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public EquipmentVO update(@NotNull String id, @NotNull EquipmentVO item, @NotNull User user) throws Exception {
        Equipment oldItem = this.getMapper().selectByPrimaryKey(id);
        if (oldItem == null) {
            throw new IllegalArgumentException("no entry with id: " + id);
        }
        Equipment newItem = item.toDbUpdateItem();

        oldItem.update(newItem);
        this.getMapper().updateByPrimaryKey(oldItem);

        List<Attachment> attachments = this.parseAttachments(id, item, user);
        this.attachmentDao.updateRecorderAttachments(id, AttachmentType.equipmentFile, attachments);

        return this.getOrError(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(@NotNull String id) {
        this.attachmentDao.deleteByRecorderId(id);
        this.getMapper().deleteByPrimaryKey(id);
    }
}
