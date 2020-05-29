package com.jy.rock.controller;

import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.service.TaskServiceImpl;
import com.xmgsd.lan.gwf.bean.LoginUser;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import com.xmgsd.lan.gwf.domain.AuditLog;
import com.xmgsd.lan.gwf.utils.ValidatorUtil;
import com.xmgsd.lan.roadhog.utils.JSON;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LinGuoHua
 */
@RestController
@RequestMapping("/task")
public class TaskController extends AbstractAuditCurdController<TaskServiceImpl> {

    @Override
    protected Object invokeAdd(@NotNull String payload, @NotNull AuditLog al, @NotNull LoginUser loginUser, Class clazz) throws Exception {
        TaskVO taskVO = JSON.deserialize(payload, TaskVO.class);
        Map<String, String> errors = ValidatorUtil.validate(taskVO);
        if (!CollectionUtils.isEmpty(errors)) {
            throw new IllegalArgumentException(errors.toString());
        }
        return this.getService().add(taskVO);
    }

    @Override
    protected Object invokeUpdate(@NotNull String id, @NotNull String payload, @NotNull AuditLog al, @NotNull LoginUser loginUser, Class clazz) throws Exception {
        TaskVO taskVO = JSON.deserialize(payload, TaskVO.class);
        Map<String, String> errors = ValidatorUtil.validate(taskVO);
        if (!CollectionUtils.isEmpty(errors)) {
            throw new IllegalArgumentException(errors.toString());
        }
        return this.getService().update(id, taskVO);
    }
}
