package com.jy.rock.controller;

import com.jy.rock.service.CableServiceImpl;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/cable")
public class CableController extends AbstractAuditCurdController<CableServiceImpl> {
}
