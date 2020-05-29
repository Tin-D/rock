package com.jy.rock.controller;

import com.jy.rock.service.ComputerRoomServiceImpl;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/computer_room")
public class ComputerRoomController extends AbstractAuditCurdController<ComputerRoomServiceImpl> {
}
