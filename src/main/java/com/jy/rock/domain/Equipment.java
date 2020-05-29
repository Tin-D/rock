package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import com.xmgsd.lan.roadhog.utils.LanUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "equipment")
public class Equipment extends BaseDomainWithGuidKey {

    /**
     * 名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 类别
     */
    @Column(name = "category_id")
    private String categoryId;

    /**
     * 类型
     */
    @Column(name = "type_id")
    private String typeId;

    /**
     * 品牌型号
     */
    @Column(name = "model_id")
    private String modelId;

    /**
     * 所有权归属
     */
    @Column(name = "`owner`")
    private String owner;

    /**
     * 序列号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 生产日期
     */
    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    /**
     * 所属机房
     */
    private String computerRoomId;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    public Equipment() {
        this.createTime = LanUtils.now();
    }
}
