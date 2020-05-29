package com.jy.rock.dao;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.bean.customer.CustomerVO;
import com.jy.rock.domain.ComputerRoom;
import com.jy.rock.domain.Customer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author hzhou
 */
public class CustomerDaoTest extends TestClassBase {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ComputerRoomDao computerRoomDao;

    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAdd() {
        Customer customer = new Customer();
        customer.setName("客户1");
        customer.setChargeUserEmail("kh1@abc.com");
        this.customerDao.insert(customer);

        Customer c = this.customerDao.selectByPrimaryKey(customer.getId());
        assertThat(c).isNotNull().isEqualToComparingFieldByField(customer);
    }

    /**
     * 测试不允许插入重名的客户
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAddWillCheckDuplicateName() {
        Customer customer = new Customer();
        customer.setName("客户1");
        customer.setChargeUserEmail("kh1@abc.com");
        this.customerDao.insert(customer);

        Customer customer1 = new Customer();
        customer1.setName("客户1");
        assertThatThrownBy(() -> this.customerDao.insert(customer1)).hasMessageContaining("Duplicate entry");
    }

    /**
     * 测试修改客户
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testUpdate() {
        Customer customer = new Customer();
        customer.setId("3");
        customer.setName("客户A");
        customer.setChargeUserEmail("aaa@abc.com");
        this.customerDao.insert(customer);

        Customer customer1 = customerDao.selectByPrimaryKey(customer.getId());
        customer1.setName("客户B");
        customer1.setChargeUserEmail("bbb@abc.com");
        this.customerDao.updateByPrimaryKey(customer1);

        assertThat(customer1).isNotNull();
    }

    /**
     * 测试删除客户
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testDelete() {
        Customer customer = new Customer();
        customer.setId("3");
        customer.setName("客户A");
        customer.setChargeUserEmail("aaa@abc.com");
        this.customerDao.insert(customer);

        Customer customer1 = customerDao.selectByPrimaryKey(customer.getId());
        customerDao.delete(customer1);
        assertThat(customer1).isNotNull().isEqualToComparingFieldByField(customer);
    }

    /**
     * 测试为客户添加主机
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testAddComputerRoomtoCustomer() {
        Customer customer = new Customer();
        customer.setId("3");
        customer.setName("客户A");
        customer.setChargeUserEmail("aaa@abc.com");
        this.customerDao.insert(customer);

        Customer customer1 = customerDao.selectByPrimaryKey(customer.getId());

        ComputerRoom computerRoom = new ComputerRoom();
        computerRoom.setName("A");
        computerRoom.setCustomerId(customer1.getId());
        this.computerRoomDao.insert(computerRoom);

        assertThat(customer1).isNotNull().isEqualToComparingFieldByField(customer1);
        assertThat(computerRoom).isNotNull().isEqualToComparingFieldByField(computerRoom);
    }

    /**
     * 根据ID查询客户分页显示
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testCustomerPage() {
        Customer customer = new Customer();
        customer.setId("3");
        customer.setName("客户A");
        customer.setChargeUserEmail("aaa@abc.com");
        this.customerDao.insert(customer);

        CustomerQueryVO customerQueryVO = new CustomerQueryVO();
        customerQueryVO.setName("客户A");
        customerQueryVO.setSortField("Name");
        customerQueryVO.setSortOrder("asc");

        List<String> ids = this.customerDao.paginationIds(customerQueryVO);
        List<CustomerVO> paginationResultByIds = this.customerDao.findPaginationResultByIds(ids, customerQueryVO);

        assertThat(paginationResultByIds).hasSize(1);
        assertThat(customer).isNotNull().isEqualToComparingFieldByField(customer);
        assertThat(customerQueryVO).isNotNull().isEqualToComparingFieldByField(customerQueryVO);
    }

    /**
     * 自定义条件查询
     */
    @Transactional(rollbackFor = Exception.class)
    @Test
    public void testExample() {
        Customer customer = new Customer();
        customer.setId("3");
        customer.setName("张三");
        customer.setChargeUserEmail("aaa@abc.com");
        this.customerDao.insert(customer);

        Example example = new Example(Customer.class);
        Example.Criteria criteria = example.createCriteria();
        if(customer.getName() != null){
            criteria.andLike("name", customer.getName() + "%");
        }
        if(customer.getId() != null){
            criteria.andEqualTo("id", customer.getId());
        }
        example.orderBy("id").desc();
        List<Customer> customers = this.customerDao.selectByExample(example);
        assertThat(customer).isNotNull().isEqualToComparingFieldByField(customer);
        assertThat(example).isNotNull().isEqualToComparingFieldByField(example);
    }


}
