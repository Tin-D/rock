package com.jy.rock.service;

import com.google.common.base.Preconditions;
import com.jy.rock.bean.attachment.JsonFileVO;
import com.jy.rock.bean.em.EquipmentModelVO;
import com.jy.rock.dao.AttachmentDao;
import com.jy.rock.dao.EquipmentModelDao;
import com.jy.rock.domain.Attachment;
import com.jy.rock.domain.EquipmentModel;
import com.jy.rock.enums.AttachmentRecorderType;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.gwf.utils.SecurityUtil;
import com.xmgsd.lan.roadhog.exception.NoEntityWithIdException;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class EquipmentModelServiceImpl extends BaseService<EquipmentModelDao>
        implements SimpleCurdViewService<String, EquipmentModelVO> {

    private AttachmentDao attachmentDao;

    @Autowired
    public EquipmentModelServiceImpl(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    private static List<Attachment> generateAttachments(@NotNull String recorderId, @Nullable List<JsonFileVO> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }

        return files.stream().map(fvo -> {
            Attachment attachment = new Attachment(fvo, AttachmentRecorderType.EquipmentModel, Objects.requireNonNull(SecurityUtil.getLoginUser()));
            attachment.setId(fvo.getId());
            attachment.setType(AttachmentType.instructions);
            attachment.setRecorderType(EquipmentModel.class.getSimpleName());
            attachment.setRecorderId(recorderId);
            return attachment;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EquipmentModelVO add(@NotNull EquipmentModelVO item) throws Exception {
        EquipmentModel em = item.toDbInsertItem();
        this.getMapper().insert(em);
        List<Attachment> attachments = generateAttachments(em.getId(), item.getAttachments());
        this.attachmentDao.insert(attachments);
        return this.getOrError(em.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EquipmentModelVO update(@NotNull String id, @NotNull EquipmentModelVO item) throws Exception {
        EquipmentModel em = item.toDbUpdateItem();

        EquipmentModel oldItem = this.getMapper().selectByPrimaryKey(id);
        oldItem.update(em);
        this.getMapper().updateByPrimaryKey(oldItem);

        List<Attachment> attachments = generateAttachments(id, item.getAttachments());
        this.attachmentDao.updateRecorderAttachments(id, null, attachments);

        return this.getOrError(id);
    }

    @Nullable
    @Override
    public EquipmentModelVO get(@NotNull String id) {
        EquipmentModel em = this.getMapper().selectByPrimaryKey(id);
        EquipmentModelVO vo = new EquipmentModelVO();
        BeanUtils.copyProperties(em, vo);
        return vo;
    }

    @NotNull
    @Override
    public EquipmentModelVO getOrError(@NotNull String id) throws IllegalArgumentException {
        return Preconditions.checkNotNull(this.get(id), new NoEntityWithIdException(id).getMessage());
    }

    @Override
    public List<EquipmentModelVO> list() {
        List<EquipmentModel> list = this.getMapper().selectAll();
        List<EquipmentModelVO> result = new ArrayList<>(list.size());
        for (EquipmentModel equipmentModel : list) {
            EquipmentModelVO vo = new EquipmentModelVO();
            BeanUtils.copyProperties(equipmentModel, vo);
            result.add(vo);
        }
        return result;
    }
}
