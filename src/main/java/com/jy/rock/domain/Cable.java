package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 线缆
 *
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Cable extends BaseDomainWithGuidKey {

    private String name;

    /**
     * 接入点的设备Id
     */
    private String inEquipmentId;

    /**
     * 输出点的设备Id
     */
    private String outEquipmentId;

    /**
     * 线缆类别编号
     */
    private String typeId;
}
