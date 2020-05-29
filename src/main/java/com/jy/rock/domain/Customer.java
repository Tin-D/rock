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
@Table(name = "customer")
public class Customer extends BaseDomainWithGuidKey {

    /**
     * 名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 负责人姓名
     */
    @Column(name = "charge_user_full_name")
    private String chargeUserFullName;

    /**
     * 负责人邮箱
     */
    @Column(name = "charge_user_email")
    private String chargeUserEmail;

    /**
     * 负责人电话1
     */
    @Column(name = "charge_user_phone1")
    private String chargeUserPhone1;

    /**
     * 负责人电话2
     */
    @Column(name = "charge_user_phone2")
    private String chargeUserPhone2;

    /**
     * 负责人电话3
     */
    @Column(name = "charge_user_phone3")
    private String chargeUserPhone3;
}
