package com.jy.rock.dao;

import com.jy.rock.domain.TaskCode;
import com.jy.rock.enums.TaskType;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;

/**
 * @author hzhou
 */
public interface TaskCodeDao extends CurdMapper<TaskCode, TaskType> {
}
