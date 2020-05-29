package com.jy.rock.controller;

import com.jy.rock.service.EquipmentModelServiceImpl;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/equipment_model")
public class EquipmentModelController extends AbstractAuditCurdController<EquipmentModelServiceImpl> {
}
