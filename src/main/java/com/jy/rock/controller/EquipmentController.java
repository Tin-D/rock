package com.jy.rock.controller;

import com.jy.rock.bean.equipment.EquipmentVO;
import com.jy.rock.domain.Equipment;
import com.jy.rock.service.EquipmentServiceImpl;
import com.xmgsd.lan.gwf.bean.LoginUser;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import com.xmgsd.lan.gwf.domain.AuditLog;
import com.xmgsd.lan.gwf.utils.SecurityUtil;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.utils.JSON;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController extends AbstractAuditCurdController<EquipmentServiceImpl> {

    @PostMapping("/parse_equipment_ids")
    public List<IdNameEntry> parseEquipmentIds(@RequestBody List<String> equipmentIds) {
        List<IdNameEntry> result = new ArrayList<>(equipmentIds.size());
        for (String equipmentId : equipmentIds) {
            Equipment equipmentVO = this.getService().getMapper().selectByPrimaryKey(equipmentId);
            if (equipmentVO != null) {
                result.add(new IdNameEntry(equipmentVO.getId(), equipmentVO.getName()));
            }
        }
        return result;
    }

    @Override
    protected Object invokeAdd(@NotNull String payload, @NotNull AuditLog al, @NotNull LoginUser loginUser, Class clazz) throws Exception {
        EquipmentVO equipmentVO = JSON.deserialize(payload, EquipmentVO.class);
        return this.getService().add(equipmentVO, Objects.requireNonNull(SecurityUtil.getLoginUser()));
    }

    @Override
    protected Object invokeUpdate(@NotNull String id, @NotNull String payload, @NotNull AuditLog al, @NotNull LoginUser loginUser, Class clazz) throws Exception {
        EquipmentVO equipmentVO = JSON.deserialize(payload, EquipmentVO.class);
        return this.getService().update(id, equipmentVO, Objects.requireNonNull(SecurityUtil.getLoginUser()));
    }
}
