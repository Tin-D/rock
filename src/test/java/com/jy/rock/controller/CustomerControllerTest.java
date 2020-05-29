package com.jy.rock.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jy.rock.TestClassBase;
import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.bean.customer.CustomerVO;
import com.jy.rock.service.CustomerServiceImpl;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.bean.PaginationResultVO;
import com.xmgsd.lan.roadhog.utils.JSON;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author hzhou
 */
public class CustomerControllerTest extends TestClassBase {

    private MockMvc mockMvc;

    @Mock
    private CustomerServiceImpl customerService;

    @InjectMocks
    private CustomerController customerController;

    @Before
    public void setUp() {
        // mock初始化
        MockitoAnnotations.initMocks(this);

        // 初始化controller测试客户端
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.customerController).build();
    }

    /**
     * 测试 list_options 方法 （list_options的功能，请看方法的注释）
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin") // 这个测试模拟一个叫做 “admin”  的用户登录
//    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void listOptions() throws Exception {
        // mock service层listOptions返回的数据
        when(this.customerService.listOptions()).thenReturn(new ArrayList<IdNameEntry>() {{
            add(new IdNameEntry("customer1", "客户1"));
            add(new IdNameEntry("customer2", "客户2"));
            add(new IdNameEntry("customer3", "客户3"));
        }});

        // 要测试的url地址
        final String url = "/customer/list_options";

        // mockMvc的用法，请自行百度，比如：https://blog.csdn.net/kqZhu/article/details/78836275
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        // 因为 customerService 是mock出来，我们这里断言这个方法在访问当前url的时候，是被调用过一次的，这是验证代码逻辑正确的断言
        verify(this.customerService, times(1)).listOptions();

        String json = mvcResult.getResponse().getContentAsString();
        // 断言，json字符串里一定包含字符：customer2
        assertThat(json).contains("customer2");

        List<IdNameEntry> options = JSON.deserializeList(json, IdNameEntry.class);
        // 断言，一定有3项
        assertThat(options).hasSize(3);
    }

    /**
     * 测试list()方法，查询所有客户
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin")
    public void list() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("1");
        customerVO.setName("tom");
        List<CustomerVO> customerVOS = new ArrayList<>();
        customerVOS.add(customerVO);
        when(this.customerService.list()).thenReturn(customerVOS);

        final String url = "/customer/list";

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.customerService, times(1)).list();

        String json = mvcResult.getResponse().getContentAsString();
        assertThat(json).contains("tom");

        List<CustomerVO> customerVOS1 = JSON.deserializeList(json, CustomerVO.class);

        assertThat(customerVOS1).hasSize(1);
    }

    /**
     * 测试get()，根据ID查找客户
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin")
    public void findOne() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("1");
        customerVO.setName("tom");

        //返回customerVO
        when(this.customerService.get("1")).thenReturn(customerVO);
        final String url = "/customer/findOne/" + customerVO.getId();
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("tom"))
                .andReturn();

        verify(this.customerService, times(1)).get(any());
        String json = mvcResult.getResponse().getContentAsString();
        assertThat(json).contains("tom");
    }

    /**
     * 更新客户
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin")
    public void update() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("1");
        customerVO.setName("tom");

        CustomerVO customerVO2 = new CustomerVO();
        customerVO.setName("jack");

        when(this.customerService.update(customerVO.getId(), customerVO2)).thenReturn(customerVO2);
        final String url = "/customer/update/" + customerVO.getId();
        mockMvc.perform(put(url))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.customerService, times(1)).update(eq("1"), any());

        assertThat(customerVO2).isNotNull();
    }

    /**
     * 插入客户
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin")
    public void add() throws Exception {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setId("1");
        customerVO.setName("tom");

        when(this.customerService.add(customerVO)).thenReturn(customerVO);
        final String url = "/customer/add";
        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.customerService, times(1)).add(any());
        assertThat(customerVO).isNotNull();
    }

    @WithMockUser(username = "admin")
    @Test
    public void pagination() throws Exception {
        // 查询条件
        final String url = "/customer/pagination";
        CustomerQueryVO queryVO = new CustomerQueryVO();

        // mock分页查询的结果
        queryVO.setPage(1);
        PaginationResultVO<CustomerVO> paginationResultVO = new PaginationResultVO<CustomerVO>();
        paginationResultVO.setPage(1);
        paginationResultVO.setPageSize(15);
        paginationResultVO.setTotal(200L);

        List<CustomerVO> customerVOS = new ArrayList<>(paginationResultVO.getPageSize());
        for (int i = 0; i < paginationResultVO.getPageSize(); i++) {
            CustomerVO customerVO = new CustomerVO();
            customerVO.setId(i + "");
            customerVOS.add(customerVO);
        }
        paginationResultVO.setItems(customerVOS);

        when(this.customerService.pagination(any())).thenReturn(paginationResultVO);
        MvcResult result = this.mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(JSON.serialize(queryVO)))
                .andExpect(status().isOk())
                .andReturn();

        // 反序列化controller返回的JSON结果
        PaginationResultVO<CustomerVO> p = JSON.deserialize(result.getResponse().getContentAsString(), new TypeReference<PaginationResultVO<CustomerVO>>() {
        });

        // 比较结果是否和controller中返回的一致，items这个字段因为是List类型的，不能简单的进行比较
        assertThat(p).isNotNull().isEqualToIgnoringGivenFields(paginationResultVO, "items");

        // 比较items这个字段
        for (int i = 0; i < paginationResultVO.getItems().size(); i++) {
            assertThat(paginationResultVO.getItems().get(i)).isEqualToComparingFieldByField(p.getItems().get(i));
        }
    }

}
