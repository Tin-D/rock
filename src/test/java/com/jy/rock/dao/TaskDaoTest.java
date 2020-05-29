package com.jy.rock.dao;

import com.jy.rock.TestClassBase;
import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.domain.ComputerRoom;
import com.jy.rock.domain.Task;
import com.xmgsd.lan.gwf.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author LinGuoHua
 */
public class TaskDaoTest extends TestClassBase {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ComputerRoomDao computerRoomDao;

    /**
     * 插入一条ComputerRoom数据
     */
    @Transactional(rollbackFor = Exception.class)
    public ComputerRoom addComputerRoom() {
        ComputerRoom computerRoom = new ComputerRoom();
        computerRoom.setId("computerRoomId886");
        computerRoom.setName("computerRoomName");
        computerRoomDao.insert(computerRoom);
        return computerRoomDao.selectByPrimaryKey(computerRoom.getId());
    }

    /**
     * 测试selectByParentId，根据ParentId查询
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testSelectByParentId() {
        ComputerRoom computerRoom = addComputerRoom();

        //父任务
        Task parentTask = new Task();
        parentTask.setId("parentTask995");
        parentTask.setCode("586");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId(computerRoom.getId());
        taskDao.insert(parentTask);

        //子任务
        Task child = new Task();
        child.setId("childTask99");
        child.setName("子任务");
        child.setCode("685");
        child.setParentId(parentTask.getId());
        child.setComputerRoomId(parentTask.getComputerRoomId());
        taskDao.insert(child);

        List<Task> childTaskList = taskDao.selectByParentId(parentTask.getId());
        assertThat(childTaskList.get(0)).isNotNull().isEqualToComparingFieldByField(child);
    }

    /**
     * 测试selectByCode，根据代号查询
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testSelectByCode() throws Exception {
        ComputerRoom computerRoom = addComputerRoom();

        Task task = new Task();
        task.setId("Task");
        task.setCode("586");
        task.setName("任务");
        task.setComputerRoomId(computerRoom.getId());
        taskDao.insert(task);

        TaskVO taskVO = taskDao.selectByCode(task.getCode());
        assertThat(taskVO.toDbUpdateItem()).isNotNull().isEqualToComparingFieldByField(task);
    }

    /**
     * 测试FinishTaskByParentId方法，根据parentId更新任务状态
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testFinishTaskByParentId() {
        User user = new User();
        user.setId("userId");
        user.setUsername("tom");
        user.setFullName("jack");

        ComputerRoom computerRoom = addComputerRoom();

        //父任务
        Task parentTask = new Task();
        parentTask.setId("parentTask995");
        parentTask.setCode("586");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId(computerRoom.getId());
        taskDao.insert(parentTask);

        //子任务
        Task child = new Task();
        child.setId("childTask99");
        child.setName("子任务");
        child.setCode("685");
        child.setParentId(parentTask.getId());
        child.setComputerRoomId(parentTask.getComputerRoomId());
        taskDao.insert(child);

        this.taskDao.finishTaskByParentId(parentTask.getId(), user);
        List<Task> childTaskList = this.taskDao.selectByParentId(parentTask.getId());
        assertThat(childTaskList.get(0).toString()).contains("Finish").contains("jack");
    }

    /**
     * 测试deleteByParentId，根据父类parentId删除
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testDeleteByParentId() {
        ComputerRoom computerRoom = addComputerRoom();

        //父任务
        Task parentTask = new Task();
        parentTask.setId("parentTask995");
        parentTask.setCode("586");
        parentTask.setName("父任务");
        parentTask.setComputerRoomId(computerRoom.getId());
        taskDao.insert(parentTask);

        //子任务
        Task child = new Task();
        child.setId("childTask99");
        child.setName("子任务");
        child.setCode("685");
        child.setParentId(parentTask.getId());
        child.setComputerRoomId(parentTask.getComputerRoomId());
        taskDao.insert(child);

        int i = taskDao.deleteByParentId(parentTask.getId());
        assertThat(i).isEqualTo(1);
    }
}
