package com.jy.rock.controller;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.task.TaskQueryVO;
import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.domain.Task;
import com.jy.rock.service.TaskServiceImpl;
import com.xmgsd.lan.roadhog.bean.PaginationResultVO;
import com.xmgsd.lan.roadhog.utils.JSON;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest extends TestClassBase {
    private MockMvc mockMvc;

    @Mock
    private TaskServiceImpl taskService;

    @InjectMocks
    private TaskController taskController;

    @Before
    public void setUp() {
        // mock初始化
        MockitoAnnotations.initMocks(this);

        // 初始化controller测试客户端
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.taskController).build();
    }

    /**
     * 测试 分页
     *
     * @throws Exception 异常
     */
    @Test
    @WithMockUser(username = "admin")
    public void pagination() throws Exception {
        TaskQueryVO taskQueryVO = new TaskQueryVO();
        taskQueryVO.setCode("aaa");
        taskQueryVO.setName("bbb");
        taskQueryVO.setSortField("code");
        taskQueryVO.setSortOrder("asc");

        List<TaskVO> taskVOList = new ArrayList<>();
        TaskVO taskVO = new TaskVO();
        taskVO.setId("1");
        taskVO.setCode("aaa");
        taskVO.setName("bbb");
        taskVOList.add(taskVO);

        PaginationResultVO<TaskVO> pagination = new PaginationResultVO<>();
        pagination.setItems(taskVOList);

        final String url = "/task/pagination";

        when(this.taskService.pagination(taskQueryVO)).thenReturn(pagination);
        MvcResult mvcResult = this.mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(JSON.serialize(taskQueryVO)))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.taskService, times(1)).pagination(any());

        String json = mvcResult.getResponse().getContentAsString();
        assertThat(json).contains("aaa");
    }

    /**
     * 插入任务
     *
     * @throws Exception 异常
     */
    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void add() throws Exception {
        TaskVO taskVO = new TaskVO();
        taskVO.setId("taskId");
        taskVO.setName("taskName");
        taskVO.setCode("taskCode");
        taskVO.setComputerRoomId("computerRoomId");

        when(this.taskService.add(any(TaskVO.class))).thenReturn(taskVO);
        final String url = "/task";
        MvcResult mvcResult = mockMvc.perform(post(url).
                content(JSON.serialize(taskVO)).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        assertThat(json).contains("taskId");
    }

    /**
     * 插入任务时编号跟机房未填写
     */
    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void addParameterChecking() {
        TaskVO taskVO = new TaskVO();
        taskVO.setId("taskId");
        taskVO.setName("taskName");

        final String url = "/task";
        assertThatThrownBy(() -> mockMvc.perform(post(url).
                content(JSON.serialize(taskVO)).
                contentType(MediaType.APPLICATION_JSON))).hasMessageContaining("编号不能为空");
    }

    /**
     * 删除任务
     */
    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void deleteTask() throws Exception {
        String taskId = "taskId";

        final String url = "/task/" + taskId;
        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andReturn();
        verify(taskService, times(1)).getOrError(taskId);
        verify(taskService, times(1)).remove(taskId);
    }

    /**
     * 更新任务
     */
    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void UpdateTask() throws Exception {
        //数据库中的任务
        Task parentTask = new Task();
        parentTask.setId("1");
        parentTask.setName("parentTaskName");
        parentTask.setCode("parentTaskCode");
        parentTask.setComputerRoomId("computerRoomID");

        //更新后的任务 带有子任务
        TaskVO parentTaskVO = new TaskVO();
        parentTaskVO.setId("1");
        parentTaskVO.setName("parentTaskVOName");
        parentTaskVO.setCode("parentTaskVOCode");
        parentTaskVO.setComputerRoomId("computerRoomID");

        TaskVO childTaskVO = new TaskVO();
        childTaskVO.setId("2");
        childTaskVO.setName("childTaskVOName");
        childTaskVO.setCode("childTaskVOCode");
        childTaskVO.setComputerRoomId("computerRoomID");
        childTaskVO.setParentId(parentTask.getId());
        List<TaskVO> childTaskVOList = new ArrayList<>();
        childTaskVOList.add(childTaskVO);
        parentTaskVO.setChildren(childTaskVOList);

        when(this.taskService.update(anyString(), any(TaskVO.class))).thenReturn(parentTaskVO);
        final String url = "/task/" + parentTask.getId();
        mockMvc.perform(put(url).
                content(JSON.serialize(parentTaskVO)).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(this.taskService, times(1)).update(eq("1"), any());
    }

    /**
     * 更新任务时编号跟机房未填写未填写
     */
    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "myUserDetailsServiceImpl")
    public void updateParameterChecking() {
        TaskVO taskVO = new TaskVO();
        taskVO.setId("taskId");
        taskVO.setName("taskName");

        final String url = "/task/" + taskVO.getId();
        assertThatThrownBy(() -> mockMvc.perform(put(url).
                content(JSON.serialize(taskVO)).
                contentType(MediaType.APPLICATION_JSON))).hasMessageContaining("必须关联机房");

    }
}
