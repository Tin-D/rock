package com.jy.rock.service;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.dao.EquipmentDao;
import com.jy.rock.dao.TaskDao;
import com.jy.rock.domain.Equipment;
import com.jy.rock.domain.Task;
import com.jy.rock.enums.TaskStatus;
import com.xmgsd.lan.gwf.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TaskServiceTest extends TestClassBase {

    @Mock
    private TaskDao taskDao;

    @Mock
    private EquipmentDao equipmentDao;

    private TaskServiceImpl taskService;

    @Mock
    private TaskCodeServiceImpl taskCodeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // 创建taskService，这里不能用spring的自动装配获得service
        // 因为@InjectMocks只对service里标记为@Autowired的字段有用
        // 这里直接new service，传入mock的dao
        // 另外，由于service的部分方法需要被代理掉，所以要包装成spy类型
        this.taskService = spy(new TaskServiceImpl(this.equipmentDao, taskCodeService));

        // mock getMapper 方法，这样getMapper 得到的 dao 就是我们mock的对象
        doReturn(this.taskDao).when(this.taskService).getMapper();
        assertThat(this.taskService.getMapper()).isEqualTo(this.taskDao);
    }

    /**
     * 测试根据编号查询任务 没有子任务也没有父任务
     */
    @Test
    public void selectByCodeTest() {
        TaskVO taskVO = new TaskVO();
        taskVO.setId("1");
        taskVO.setName("abc");
        taskVO.setCode("abc");
        taskVO.setComputerRoomId("computerRoomID");

        doReturn(taskVO).when(taskDao).selectByCode(taskVO.getCode());

        TaskVO taskVO1 = taskService.selectByCode(taskVO.getCode());
        assertThat(taskVO1).isNotNull().isEqualToComparingFieldByField(taskVO);
    }

    /**
     * 测试根据编号查询的任务有子任务
     */
    @Test
    public void testSelectByCodeWithChildren() throws Exception {
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
        childTaskVO.setParentId(parentTaskVO.getId());

        List<Task> childTask = new ArrayList<>();
        childTask.add(childTaskVO.toDbUpdateItem());

        doReturn(parentTaskVO).when(taskDao).selectByCode(parentTaskVO.getCode());
        doReturn(childTask).when(taskDao).selectByParentId(parentTaskVO.getId());
        TaskVO taskVO = taskService.selectByCode(parentTaskVO.getCode());
        assertThat(taskVO.toString()).contains("childTaskVOName");
    }

    /**
     * 测试根据编号查询不到任务
     */
    @Test
    public void testSelectByCodeIsNull() {
        assertThatThrownBy(() -> this.taskService.selectByCode("123"))
                .hasMessageContaining("no entity with id");
    }

    @Test
    public void testAddWillCheckEquipmentId() {
        TaskVO taskVO = new TaskVO();
        taskVO.setEquipmentId("equipmentId1");
        taskVO.setComputerRoomId("room1");

        Equipment equipment = new Equipment();
        equipment.setComputerRoomId("room2");
        when(this.equipmentDao.selectByPrimaryKey(taskVO.getEquipmentId())).thenReturn(equipment);

        assertThatThrownBy(() -> this.taskService.add(taskVO)).hasMessageContaining("设备所在机房有误");
        verify(this.equipmentDao, times(1)).selectByPrimaryKey(taskVO.getEquipmentId());
        verify(this.taskDao, never()).insert(any(Task.class));
    }

    @Test
    public void testAddWithoutChildren() throws Exception {
        final String taskId = "taskId1";

        TaskVO taskVO = new TaskVO();
        // 必须给taskVO一个id，因为id是在taskDao.insert方法里生成的，
        // 而现在taskDao是个mock对象，不会有实际执行的方法，所以事先给定一个id
        taskVO.setId(taskId);

        // mock taskService.getOrError 方法
        // 下面这种写法，和 when(this.taskService.getOrError(taskId)).thenReturn(taskVO) 的区别如下：
        // 下面这种写法不会真正的调用 getOrError 方法一次，而另外一种写法，在mock的时候会闲调用真实的getOrError方法一次，这样会导致错误出现
        doReturn(taskVO).when(this.taskService).getOrError(taskId);


        TaskVO add = this.taskService.add(taskVO);
        verify(this.taskDao, times(1)).insert(any(Task.class));
        verify(this.taskService, times(1)).getOrError(anyString());

        assertThat(add).isEqualTo(taskVO);
    }

    /**
     * 测试完成一个单独的任务，没有父任务也没有子任务
     */
    @Test
    public void testUpdateTaskFinish() throws Exception {
        // 构建一个独立的任务
        TaskVO taskVO = new TaskVO();
        taskVO.setId("task1");
        taskVO.setName("任务");
        taskVO.setComputerRoomId("computerRoom1");

        User user = new User();
        user.setId("userId");
        user.setUsername("tom");
        user.setFullName("jack");

        doReturn(taskVO.toDbUpdateItem()).when(taskDao).selectByPrimaryKey(taskVO.getId());
        taskService.finishTask(taskVO.getId(), user);
        verify(this.taskDao, times(1)).updateByPrimaryKey(any());
    }

    /**
     * 测试完成方法传入的任务id从数据库中无法查询到任务
     */
    @Test
    public void testUpdateTaskIsNull() {
        String taskId = "taskId";
        //mock根据这个id查询返回null 但是不用mock好像也是null
        doReturn(null).when(taskDao).selectByPrimaryKey(taskId);
        assertThatThrownBy(() -> this.taskService.finishTask(taskId, new User())).hasMessageContaining("no entity with id: taskId");
        verify(this.taskDao, never()).updateByPrimaryKey(any());
    }

    /**
     * 测试完成父任务中有子任务时更新子任务
     */
    @Test
    public void testUpdateTaskWithChildren() throws Exception {
        User user = new User();
        user.setId("userId");
        user.setUsername("tom");
        user.setFullName("jack");

        // 构建一个包含3个子任务的父任务
        TaskVO parentTask = new TaskVO();
        parentTask.setId("parentTask1");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId("computerRoom1");

        List<TaskVO> children = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            TaskVO child = new TaskVO();
            child.setId("childTask" + i);
            child.setName("子任务" + i);
            child.setParentId(parentTask.getId());
            child.setComputerRoomId(parentTask.getComputerRoomId());
            children.add(child);
        }
        parentTask.setChildren(children);

        List<Task> dbChildren = new ArrayList<>(children.size());
        for (TaskVO child : children) {
            Task dbChild = child.toDbUpdateItem();
            dbChildren.add(dbChild);
        }

        doReturn(parentTask.toDbUpdateItem()).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(dbChildren).when(taskDao).selectByParentId(any());
        taskService.finishTask(parentTask.getId(), user);
        verify(this.taskDao, times(1)).updateByPrimaryKey(any());
        verify(this.taskDao, times(1)).finishTaskByParentId(parentTask.getId(), user);
    }

    /**
     * 测试完成子任务时检查父任务，父任务下的子任务全部完成，自动标记父任务完成
     */
    @Test
    public void testUpdateTaskWithParentId() throws Exception {
        User user = new User();
        user.setId("userId");
        user.setUsername("tom");
        user.setFullName("jack");

        // 构建一个包含2个子任务的父任务
        TaskVO parentTask = new TaskVO();
        parentTask.setId("parentTask1");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId("computerRoom1");

        List<TaskVO> children = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            TaskVO child = new TaskVO();
            child.setId("childTask" + i);
            child.setName("子任务" + i);
            child.setComputerRoomId(parentTask.getComputerRoomId());
            child.setParentId(parentTask.getId());
            children.add(child);
        }
        parentTask.setChildren(children);

        //将其中一个子任务状态标记为完成
        TaskVO child1 = children.get(0);
        child1.setFinish(true);
        child1.setTaskStatus(TaskStatus.Finish);

        //把子任务从TaskVO转换成Task
        List<Task> dbChildren = new ArrayList<>();
        for (TaskVO child : children) {
            dbChildren.add(child.toDbUpdateItem());
        }

        Task child2 = dbChildren.get(1);

        doReturn(child2).when(taskDao).selectByPrimaryKey(child2.getId());
        doReturn(parentTask.toDbUpdateItem()).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(dbChildren).when(taskDao).selectByParentId(parentTask.getId());
        taskService.finishTask(child2.getId(), user);
        verify(taskDao, times(2)).updateByPrimaryKey(any());
    }

    /**
     * 测试完成子任务时检查父任务，父任务下的子任务未全部完成
     */
    @Test
    public void testUpdateTaskWithParentId2() throws Exception {
        User user = new User();
        user.setId("userId");
        user.setUsername("tom");
        user.setFullName("jack");

        // 构建一个包含2个子任务的父任务
        TaskVO parentTask = new TaskVO();
        parentTask.setId("parentTask1");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId("computerRoom1");

        List<TaskVO> children = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            TaskVO child = new TaskVO();
            child.setId("childTask" + i);
            child.setName("子任务" + i);
            child.setComputerRoomId(parentTask.getComputerRoomId());
            child.setParentId(parentTask.getId());
            child.setFinish(false);
            children.add(child);
        }
        parentTask.setChildren(children);

        //把子任务从TaskVO转换成Task
        List<Task> dbChildren = new ArrayList<>();
        for (TaskVO child : children) {
            dbChildren.add(child.toDbUpdateItem());
        }

        Task child = dbChildren.get(0);

        doReturn(child).when(taskDao).selectByPrimaryKey(child.getId());
        doReturn(parentTask.toDbUpdateItem()).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(dbChildren).when(taskDao).selectByParentId(parentTask.getId());
        taskService.finishTask(child.getId(), user);
        verify(taskDao, times(2)).updateByPrimaryKey(any());
    }

    /**
     * 测试根据id删除任务时这个任务有子任务
     */
    @Test
    public void testRemoveTaskWithChildren() {
        Task parentTask = new Task();
        parentTask.setId("1");
        parentTask.setName("parentTaskVOName");
        parentTask.setCode("parentTaskVOCode");
        parentTask.setComputerRoomId("computerRoomID");

        Task childTask = new Task();
        childTask.setId("2");
        childTask.setName("childTaskVOName");
        childTask.setCode("childTaskVOCode");
        childTask.setComputerRoomId("computerRoomID");
        childTask.setParentId(parentTask.getId());
        List<Task> childTaskList = new ArrayList<>();
        childTaskList.add(childTask);

        doReturn(parentTask).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(childTaskList).when(taskDao).selectByParentId(parentTask.getId());
        taskService.remove(parentTask.getId());
        verify(taskDao, times(1)).deleteByParentId(parentTask.getId());
        verify(taskDao, times(1)).deleteByPrimaryKey(parentTask.getId());
    }

    /**
     * 测试根据id删除任务时这个任务没有子任务
     */
    @Test
    public void testRemoveTaskWithoutChildren() {
        Task task = new Task();
        task.setId("1");
        task.setName("TaskName");
        task.setCode("TaskCode");
        task.setComputerRoomId("computerRoomID");

        doReturn(task).when(taskDao).selectByPrimaryKey(task.getId());
        taskService.remove(task.getId());
        verify(taskDao, times(1)).deleteByPrimaryKey(task.getId());
        verify(taskDao, never()).deleteByParentId(anyString());
    }

    /**
     * 测试根据id查询不到任务
     */
    @Test
    public void testRemoveTaskIsNull() {
        String taskId = "taskId";
        doReturn(null).when(taskDao).selectByPrimaryKey(taskId);
        assertThatThrownBy(() -> this.taskService.remove(taskId)).hasMessageContaining("no entity with id: taskId");
        verify(this.taskDao, never()).deleteByPrimaryKey(any());
    }

    /**
     * 测试更新任务时没子任务
     */
    @Test
    public void testUpdate() throws Exception {
        Task task = new Task();
        task.setId("1");
        task.setName("TaskName");
        task.setCode("TaskCode");
        task.setComputerRoomId("computerRoomID");

        TaskVO taskVO = new TaskVO();
        taskVO.setId("1");
        taskVO.setName("TaskVOName");
        taskVO.setCode("TaskVOCode");
        taskVO.setComputerRoomId("computerRoomID");

        doReturn(task).when(taskDao).selectByPrimaryKey(task.getId());
        doReturn(taskVO).when(taskService).getOrError(task.getId());
        taskService.update(task.getId(), taskVO);
        verify(taskDao, times(1)).updateByPrimaryKey(task);
        verify(taskDao, never()).deleteByParentId(anyString());
        verify(taskDao, never()).insert(any(Task.class));
    }

    /**
     * 测试更新时更新了子任务
     */
    @Test
    public void testUpdateWithChildrenForAll() throws Exception {
        //数据库中的任务带有子任务
        Task parentTask = new Task();
        parentTask.setId("1");
        parentTask.setName("parentTaskName");
        parentTask.setCode("parentTaskCode");
        parentTask.setComputerRoomId("computerRoomID");
        Task childTaskVO1 = new Task();
        childTaskVO1.setId("2");
        childTaskVO1.setName("childTaskVOName");
        childTaskVO1.setCode("childTaskVOCode");
        childTaskVO1.setComputerRoomId("computerRoomID");
        childTaskVO1.setParentId(parentTask.getId());
        Task childTaskVO2 = new Task();
        childTaskVO2.setId("3");
        childTaskVO2.setName("childTaskVOName");
        childTaskVO2.setCode("childTaskVOCode");
        childTaskVO2.setComputerRoomId("computerRoomID");
        childTaskVO2.setParentId(parentTask.getId());
        Task childTaskVO3 = new Task();
        childTaskVO3.setId("4");
        childTaskVO3.setName("childTaskVOName");
        childTaskVO3.setCode("childTaskVOCode");
        childTaskVO3.setComputerRoomId("computerRoomID");
        childTaskVO3.setParentId(parentTask.getId());
        Task childTaskVO4 = new Task();
        childTaskVO4.setId("5");
        childTaskVO4.setName("childTaskVOName");
        childTaskVO4.setCode("childTaskVOCode");
        childTaskVO4.setComputerRoomId("computerRoomID");
        childTaskVO4.setParentId(parentTask.getId());
        List<Task> tasks = new ArrayList<>();
        tasks.add(childTaskVO1);
        tasks.add(childTaskVO2);
        tasks.add(childTaskVO3);
        tasks.add(childTaskVO4);


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
        TaskVO childTaskV5 = new TaskVO();
        childTaskV5.setId("3");
        childTaskV5.setName("childTask");
        childTaskV5.setCode("childTaskVOCode");
        childTaskV5.setComputerRoomId("computerRoomID");
        childTaskV5.setParentId(parentTask.getId());
        TaskVO childTaskV6 = new TaskVO();
        childTaskV6.setName("childTaskVOName");
        childTaskV6.setCode("childTaskVOCode");
        childTaskV6.setComputerRoomId("computerRoomID");
        childTaskV6.setParentId(parentTask.getId());

        List<TaskVO> childTaskVOList = new ArrayList<>();
        childTaskVOList.add(childTaskVO);
        childTaskVOList.add(childTaskV5);
        childTaskVOList.add(childTaskV6);
        parentTaskVO.setChildren(childTaskVOList);

        doReturn(parentTask).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(tasks).when(taskDao).selectByParentId(parentTask.getId());
        doReturn(parentTaskVO).when(taskService).getOrError(parentTask.getId());
        taskService.update(parentTask.getId(), parentTaskVO);
        verify(taskDao, times(1)).updateByPrimaryKey(parentTask);
        verify(taskDao, times(1)).insert(anyList());
        verify(taskDao, times(2)).deleteByPrimaryKey(anyString());
    }

    /**
     * 测试更新时更新了子任务
     */
    @Test
    public void testUpdateWithChildrenForNotChange() throws Exception {
        //数据库中的任务带有子任务
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
        TaskVO childTaskV5 = new TaskVO();
        childTaskV5.setId("3");
        childTaskV5.setName("childTaskVOName");
        childTaskV5.setCode("childTaskVOCode");
        childTaskV5.setComputerRoomId("computerRoomID");
        childTaskV5.setParentId(parentTask.getId());


        List<TaskVO> childTaskVOList = new ArrayList<>();
        childTaskVOList.add(childTaskVO);
        childTaskVOList.add(childTaskV5);
        parentTaskVO.setChildren(childTaskVOList);

        doReturn(parentTask).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(null).when(taskDao).selectByParentId(parentTask.getId());
        doReturn(parentTaskVO).when(taskService).getOrError(parentTask.getId());
        taskService.update(parentTask.getId(), parentTaskVO);
        verify(taskDao, times(1)).updateByPrimaryKey(parentTask);

    }

    @Test
    public void testUpdateWithChildrenForNoChild() throws Exception {
        //数据库中的任务带有子任务
        Task parentTask = new Task();
        parentTask.setId("1");
        parentTask.setName("parentTaskName");
        parentTask.setCode("parentTaskCode");
        parentTask.setComputerRoomId("computerRoomID");
        Task childTaskVO1 = new Task();
        childTaskVO1.setId("2");
        childTaskVO1.setName("childTaskVOName");
        childTaskVO1.setCode("childTaskVOCode");
        childTaskVO1.setComputerRoomId("computerRoomID");
        childTaskVO1.setParentId(parentTask.getId());
        Task childTaskVO2 = new Task();
        childTaskVO2.setId("3");
        childTaskVO2.setName("childTaskVOName");
        childTaskVO2.setCode("childTaskVOCode");
        childTaskVO2.setComputerRoomId("computerRoomID");
        childTaskVO2.setParentId(parentTask.getId());
        List<Task> tasks = new ArrayList<>();
        tasks.add(childTaskVO1);
        tasks.add(childTaskVO2);

        //更新后的任务 带有子任务
        TaskVO parentTaskVO = new TaskVO();
        parentTaskVO.setId("1");
        parentTaskVO.setName("parentTaskVOName");
        parentTaskVO.setCode("parentTaskVOCode");
        parentTaskVO.setComputerRoomId("computerRoomID");

        doReturn(parentTask).when(taskDao).selectByPrimaryKey(parentTask.getId());
        doReturn(tasks).when(taskDao).selectByParentId(parentTask.getId());
        doReturn(parentTaskVO).when(taskService).getOrError(parentTask.getId());
        taskService.update(parentTask.getId(), parentTaskVO);


    }
}
