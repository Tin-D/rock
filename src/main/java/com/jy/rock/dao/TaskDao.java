package com.jy.rock.dao;

import com.jy.rock.bean.task.TaskQueryVO;
import com.jy.rock.bean.task.TaskVO;
import com.jy.rock.domain.Task;
import com.xmgsd.lan.gwf.domain.User;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import com.xmgsd.lan.roadhog.mybatis.mappers.PaginationWithoutDuplicateMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author hzhou
 */
@Repository
public interface TaskDao extends CurdMapper<Task, String>, PaginationWithoutDuplicateMapper<TaskVO,String, TaskQueryVO> {
    @Override
    List<String> paginationIds(@Param("query") TaskQueryVO taskQueryVO);

    @Override
    List<TaskVO> findPaginationResultByIds(@Param("ids") Collection<String> ids, @Param("query") TaskQueryVO taskQueryVO);

    @Override
    default Function<TaskVO, String> idGetter(){
        return TaskVO::getId;
    };

    List<Task> selectByParentId(@Param("parentId") String parentId);

    int finishTaskByParentId(@Param("parentId") String parentId, @Param("user")User user);

    TaskVO selectByCode(@Param("code") String code);

    int deleteByParentId(@Param("parentId") String parentId);
}
