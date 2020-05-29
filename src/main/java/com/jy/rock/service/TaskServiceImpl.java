package com.jy.rock.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.jy.rock.bean.task.TaskQueryVO;
import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.dao.EquipmentDao;
import com.jy.rock.dao.TaskDao;
import com.jy.rock.domain.Equipment;
import com.jy.rock.domain.Task;
import com.jy.rock.enums.TaskStatus;
import com.jy.rock.enums.TaskType;
import com.xmgsd.lan.gwf.domain.User;
import com.xmgsd.lan.roadhog.exception.NoEntityWithIdException;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class TaskServiceImpl extends BaseService<TaskDao>
        implements SimpleCurdViewService<String, TaskVO>, PaginationService<TaskQueryVO, TaskVO> {

    private EquipmentDao equipmentDao;

    private TaskCodeServiceImpl taskCodeService;

    @Autowired
    public TaskServiceImpl(EquipmentDao equipmentDao, TaskCodeServiceImpl taskCodeService) {
        this.equipmentDao = equipmentDao;
        this.taskCodeService = taskCodeService;
    }

    @Override
    public BasePaginationMapper<TaskVO> getPaginationMapper() {
        return this.getMapper();
    }

    /**
     * 创建任务
     *
     * @param item 任务
     * @return 插入的任务
     * @throws IllegalArgumentException 机房参数有误
     * @throws Exception                异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskVO add(@NotNull TaskVO item) throws Exception {
        //如果任务的机房跟设备的机房不同 则抛出异常
        if (!Strings.isNullOrEmpty(item.getEquipmentId())) {
            Equipment equipment = equipmentDao.selectByPrimaryKey(item.getEquipmentId());
            if (!equipment.getComputerRoomId().equals(item.getComputerRoomId())) {
                throw new IllegalArgumentException("设备所在机房有误");
            }
        }

        Task task = item.toDbInsertItem();
        this.getMapper().insert(task);

        //如果该任务有子任务，父子任务机房必须相同且父任务设备必须为空
        List<TaskVO> children = item.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            if (Strings.isNullOrEmpty(item.getEquipmentId())) {
                for (TaskVO child : children) {
                    if (!item.getComputerRoomId().equals(child.getComputerRoomId())) {
                        throw new IllegalArgumentException("子任务" + child.getName() + "和父任务机房不同");
                    } else {
                        Task childTask = child.toDbInsertItem();
                        this.getMapper().insert(childTask);
                    }
                }
            } else {
                throw new Exception("父任务设备应为空");
            }
        }

        return this.getOrError(task.getId());
    }

    /**
     * 根据id获取任务
     *
     * @param id 任务id
     * @return 任务
     */
    @Nullable
    @Override
    public TaskVO get(@NotNull String id) {
        Task task = Preconditions.checkNotNull(this.getMapper().selectByPrimaryKey(id), new NoEntityWithIdException(id).getMessage());
        TaskVO taskVO = new TaskVO();
        List<Task> childrenTask = this.getMapper().selectByExample(new Example.Builder(Task.class)
                .where(WeekendSqls.<Task>custom().andEqualTo(Task::getParentId, id))
                .build());
        List<TaskVO> childrenTaskVO = new ArrayList<>();
        if (!CollectionUtils.isEmpty(childrenTask)) {
            for (Task childTask : childrenTask) {
                TaskVO childTaskVO = new TaskVO();
                BeanUtils.copyProperties(childTask, childTaskVO);
                childrenTaskVO.add(childTaskVO);
            }
            taskVO.setChildren(childrenTaskVO);
        }

        BeanUtils.copyProperties(task, taskVO);
        return taskVO;
    }

    /**
     * 更新任务状态为完成
     * 1. 当item是父任务，子任务也会被更新
     * 2. 当item是子任务，会检查它的父任务，如果父任务的子任务都完成了，则会更新父任务的完成状态
     *
     * @param taskId 任务id
     * @throws NullPointerException 任务不存在 传入id有误
     */
    @Transactional(rollbackFor = Exception.class)
    public void finishTask(@NotNull String taskId, @NotNull User user) {
        Task task = Preconditions.checkNotNull(this.getMapper().selectByPrimaryKey(taskId), new NoEntityWithIdException(taskId).getMessage());
        task.setFinish(true);
        task.setTaskStatus(TaskStatus.Finish);
        LocalDateTime finishTime = LocalDateTime.now();
        task.setFinishTime(finishTime);
        task.setFinishUserId(user.getId());
        task.setFinishUserName(user.getUsername());
        task.setFinishUserFullName(user.getFullName());
        this.getMapper().updateByPrimaryKey(task);

        //检查该任务是否有子任务
        List<Task> children = this.getMapper().selectByParentId(task.getId());
        //如果有子任务则自动标记子任务为完成
        if (!CollectionUtils.isEmpty(children)) {
            this.getMapper().finishTaskByParentId(task.getId(), user);
        }

        //完成子任务的时候要检查父任务，如果父任务的所有子任务都完成了，父任务也标记为完成
        if (!Strings.isNullOrEmpty(task.getParentId())) {
            //根据父任务id 查询出父任务,如果父任务不为空，则查询父任务下的子任务的状态
            Task taskParent = Preconditions.checkNotNull(this.getMapper().selectByPrimaryKey(task.getParentId()), new NoEntityWithIdException(taskId).getMessage());
            // 这里的children2是指该任务父任务下的子任务
            List<Task> children2 = this.getMapper().selectByParentId(taskParent.getId());
            //判断每一个子任务的状态
            boolean childrenFlag = children2.stream().anyMatch(Task::getFinish);
            //如果每个子任务的状态都完成，则自动标记父任务状态为完成
            if (childrenFlag) {
                taskParent.setFinish(true);
                taskParent.setTaskStatus(TaskStatus.Finish);
                taskParent.setFinishTime(finishTime);
                taskParent.setFinishUserId(user.getId());
                taskParent.setFinishUserName(user.getUsername());
                taskParent.setFinishUserFullName(user.getFullName());
                this.getMapper().updateByPrimaryKey(taskParent);
            }
        }
    }

    /**
     * 更新任务，会更新包含的子任务（比如子任务增加了，减少了，子任务的信息改变了）
     *
     * @param id   需要更新的任务id
     * @param item 更新后的任务
     * @return 更新后的任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskVO update(@NotNull String id, @NotNull TaskVO item) throws Exception {
        Task oldTask = this.getMapper().selectByPrimaryKey(id);
        oldTask.update(item.toDbUpdateItem());
        this.getMapper().updateByPrimaryKey(oldTask);

        // 处理子任务
        List<TaskVO> newChildTasks = item.getChildren() == null ? Collections.emptyList() : item.getChildren();
        List<Task> oldChildTasks = this.getMapper().selectByParentId(item.getId()) == null ? Collections.emptyList() : this.getMapper().selectByParentId(item.getId());

        // 处理更新的和删除的任务
        Set<String> newChildTaskIds = newChildTasks.stream().map(TaskVO::getId).collect(Collectors.toSet());
        Set<String> oldChildTaskIds = oldChildTasks.stream().map(BaseDomainWithGuidKey::getId).collect(Collectors.toSet());

        // newChildTaskIds 和 oldChildTaskIds 的差集就是要新增的
        Sets.SetView<String> idsToAdd = Sets.difference(newChildTaskIds, oldChildTaskIds);
        if (!idsToAdd.isEmpty()) {
            List<Task> tasksToAdd = newChildTasks.stream().filter(t -> idsToAdd.contains(t.getId()))
                    .map(t -> {
                        try {
                            return t.toDbInsertItem();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            this.getMapper().insert(tasksToAdd);
        }

        // oldChildTaskIds 和 newChildTaskIds 的差集就是要删除的
        Sets.SetView<String> idsToDelete = Sets.difference(oldChildTaskIds, newChildTaskIds);
        idsToDelete.forEach(i -> this.getMapper().deleteByPrimaryKey(i));

        // oldChildTaskIds 和 newChildTaskIds 的交集就是要修改的
        Sets.SetView<String> idsToUpdate = Sets.intersection(oldChildTaskIds, newChildTaskIds);
        for (String taskId : idsToUpdate) {
            Task newChildTask = newChildTasks.stream().filter(t -> taskId.equals(t.getId()))
                    .map(t -> {
                        try {
                            return t.toDbUpdateItem();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .findFirst()
                    .orElse(null);
            assert newChildTask != null;

            Task oldChildTask = oldChildTasks.stream().filter(t -> taskId.equals(t.getId())).findFirst().orElse(null);
            assert oldChildTask != null;

            if (oldChildTask.equals(newChildTask)) {
                this.getMapper().updateByPrimaryKey(newChildTask);
            }
        }

        return this.getOrError(item.getId());
    }

    /**
     * 根据任务代号查询任务
     *
     * @param code 任务代号
     * @return 任务
     */
    public TaskVO selectByCode(@NotNull String code) {
        TaskVO taskVO = Preconditions.checkNotNull(this.getMapper().selectByCode(code), new NoEntityWithIdException(code).getMessage());

        //如果这个任务有子任务，则将子任务一起返回
        List<Task> children = this.getMapper().selectByParentId(taskVO.getId());
        if (!CollectionUtils.isEmpty(children)) {
            List<TaskVO> childrenTaskVO = new ArrayList<>(children.size());
            TaskVO childTaskVO = new TaskVO();
            for (Task child : children) {
                BeanUtils.copyProperties(child, childTaskVO);
                childrenTaskVO.add(childTaskVO);
            }
            taskVO.setChildren(childrenTaskVO);
        }

        return taskVO;
    }

    /**
     * 根据id删除任务，如果这个任务有子任务，将子任务一起删除
     *
     * @param id 任务id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(@NotNull String id) {
        Task task = Preconditions.checkNotNull(this.getMapper().selectByPrimaryKey(id), new NoEntityWithIdException(id).getMessage());

        //如果这个任务有子任务，就将子任务一起删除
        List<Task> childTask = this.getMapper().selectByParentId(task.getId());
        if (!CollectionUtils.isEmpty(childTask)) {
            this.getMapper().deleteByParentId(task.getId());
        }
        this.getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void tryCode(@NotNull TaskType taskType, boolean throwException) {
        String s = this.taskCodeService.generateCode(taskType, false);
        System.out.println(s);
        if (throwException) {
            throw new IllegalArgumentException("force exception");
        }
    }
}
