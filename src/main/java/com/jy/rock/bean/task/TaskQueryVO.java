package com.jy.rock.bean.task;

import com.xmgsd.lan.roadhog.bean.BasePaginationQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskQueryVO extends BasePaginationQuery {

    private String code;

    private String name;

    private String parentId;

    private String computerRoomId;

    private String equipmentId;

    private String remark;

    private LocalDateTime createTime;

    private String createUserName;

    private String createUserFullName;

    private String finishUserName;

    private String finishUserFullName;

    private Boolean finish;

    private LocalDateTime finishTime;
}
