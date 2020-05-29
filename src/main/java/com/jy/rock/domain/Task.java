package com.jy.rock.domain;

import com.jy.rock.enums.TaskStatus;
import com.jy.rock.enums.TaskType;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import com.xmgsd.lan.roadhog.utils.LanUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 任务
 *
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "task")
public class Task extends BaseDomainWithGuidKey {
    /**
     * 任务编号，按规律生成，具有可读性
     */
    private String code;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 父任务编号，如果为null代表没有父任务
     */
    private String parentId;

    /**
     * 关联机房的编号
     */
    private String computerRoomId;

    /**
     * 关联设备的编号
     */
    private String equipmentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 任务创建时间
     */
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 创建用户id，如果为null，代表系统创建的
     */
    @Column(updatable = false)
    private String createUserId;

    /**
     * 创建用户帐号，如果为null，代表系统创建的
     */
    @Column(updatable = false)
    private String createUserName;

    /**
     * 创建用户名称，如果为null，代表系统创建的
     */
    @Column(updatable = false)
    private String createUserFullName;

    /**
     * 执行用户id，如果为null，代表系统处理
     */
    private String finishUserId;

    /**
     * 执行用户帐号，如果为null，代表系统处理
     */
    private String finishUserName;

    /**
     * 执行用户名称，如果为null，代表系统处理
     */
    private String finishUserFullName;

    /**
     * 是否已经完成
     */
    private Boolean finish;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 任务类别
     */
    private TaskType taskType;

    /**
     * 任务状态
     */
    private TaskStatus taskStatus;

    public Task() {
        this.finish = false;
        this.createTime = LanUtils.now();
    }

    public Task(@NotNull String code, @NotNull String name) {
        this();
        this.code = code;
        this.name = name;
    }


}
