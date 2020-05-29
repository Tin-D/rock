package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 线缆连接点
 *
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CablePoint extends BaseDomainWithGuidKey {

    /**
     * 线缆Id
     */
    private String cableId;

    /**
     * 名称
     */
    private String name;

    /**
     * 顺序
     */
    private Integer orderNumber;
}
