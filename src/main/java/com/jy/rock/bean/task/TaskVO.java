package com.jy.rock.bean.task;

import com.jy.rock.domain.Task;
import com.jy.rock.enums.TaskStatus;
import com.jy.rock.enums.TaskType;
import com.xmgsd.lan.roadhog.bean.BaseFormData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LinGuoHua
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskVO extends BaseFormData<Task>  {
    private String id;

    @NotBlank(message = "编号不能为空")
    private String code;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String parentId;

    @NotBlank(message = "必须关联机房")
    private String computerRoomId;

    private String equipmentId;

    private String remark;

    private LocalDateTime createTime;

    private String createUserId;

    private String createUserName;

    private String createUserFullName;

    private String finishUserId;

    private String finishUserName;

    private String finishUserFullName;

    private Boolean finish;

    private LocalDateTime finishTime;

    private TaskType taskType;

    private TaskStatus taskStatus;

    private List<TaskVO> children;
}
