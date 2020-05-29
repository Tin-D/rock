package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "equipment_model")
public class EquipmentModel extends BaseDomainWithGuidKey {

    /**
     * 品牌
     */
    @Column(name = "brand_id")
    private String brandId;

    /**
     * 型号名称
     */
    @Column(name = "`name`")
    private String name;
}
