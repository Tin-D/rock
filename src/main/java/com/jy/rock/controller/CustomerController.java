package com.jy.rock.controller;

import com.jy.rock.service.CustomerServiceImpl;
import com.xmgsd.lan.gwf.core.audit.AbstractAuditCurdController;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/customer")
@Api(value = "客户管理")
public class CustomerController extends AbstractAuditCurdController<CustomerServiceImpl> {

    /**
     * 列出所有客户的信息，键值对形式表示
     *
     * @return 所有客户信息
     */
    @ApiOperation(value = "列出所有客户的信息")
    @GetMapping("/list_options")
    public List<IdNameEntry> listOptions() {
        return this.getService().listOptions();
    }
}
