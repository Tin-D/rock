package com.jy.rock.service;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.bean.customer.CustomerVO;
import com.xmgsd.lan.roadhog.bean.PaginationResultVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(rollbackFor = Exception.class)
public class CustomerServiceTest extends TestClassBase {

    @Autowired
    private CustomerServiceImpl customerService;

    /**
     * 根据ID查询客户
     */
    @Test
    public void testFindById() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("1");
        customerVO.setName("张三");
        this.customerService.add(customerVO);
        CustomerVO customerVO1 = customerService.get(customerVO.getId());

        assertThat(customerVO).isNotNull().isEqualToComparingFieldByField(customerVO);
        assertThat(customerVO1).isNotNull().isEqualToComparingFieldByField(customerVO1);
    }

    /**
     * 根据ID更新客户
     */
    @Test
    public void testUpdateById() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setName("王五");
        customerVO.setChargeUserEmail("abc@a.com");
        customerVO.setChargeUserFullName("aaa");
        customerVO.setChargeUserPhone1("123");
        customerVO.setChargeUserPhone2("455");
        customerVO.setChargeUserPhone3("789");

        CustomerVO customerVO2 = new CustomerVO();
        customerVO2.setId("6");
        customerVO2.setName("赵六");
        this.customerService.add(customerVO2);

        this.customerService.update(customerVO2.getId(), customerVO);
        CustomerVO customerVO1 = this.customerService.get(customerVO2.getId());

        assertThat(customerVO).isNotNull().isEqualToComparingFieldByField(customerVO);
        assertThat(customerVO2).isNotNull().isEqualToComparingFieldByField(customerVO2);
        assertThat(customerVO1).isNotNull().isEqualToComparingFieldByField(customerVO1);
    }

    /**
     * 根据ID删除客户
     */
    @Test
    public void testDeleteById() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("6");
        customerVO.setName("赵六");
        this.customerService.add(customerVO);

        this.customerService.remove(customerVO.getId());

        assertThat(customerVO).isNotNull().isEqualToComparingFieldByField(customerVO);
    }

    @Test
    public void testPage() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("5");
        customerVO.setName("客户A");
        this.customerService.add(customerVO);

        CustomerQueryVO customerQueryVO = new CustomerQueryVO();
        customerQueryVO.setName("客户A");
        customerQueryVO.setSortField("Name");
        customerQueryVO.setSortOrder("asc");
        PaginationResultVO<CustomerVO> pagination = this.customerService.pagination(customerQueryVO);
        assertThat(pagination.toString()).contains("5");
    }

}
