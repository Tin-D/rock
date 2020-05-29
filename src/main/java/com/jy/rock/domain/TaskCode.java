package com.jy.rock.domain;

import com.jy.rock.enums.TaskType;
import com.xmgsd.lan.roadhog.mybatis.DbItem;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
public class TaskCode implements DbItem {

    @Id
    private TaskType taskType;

    private String nextNumber;
}
