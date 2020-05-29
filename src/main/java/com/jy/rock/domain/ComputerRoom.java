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
@Table(name = "computer_room")
public class ComputerRoom extends BaseDomainWithGuidKey {

    /**
     * 名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 所属客户（允许空值）
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;
}
