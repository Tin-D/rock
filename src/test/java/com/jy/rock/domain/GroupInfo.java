package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Table;

/**
 * @author lin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "group_info")
public class GroupInfo extends BaseDomainWithGuidKey {

    /**
     * 代号
     */
    private String code;

    /**
     * 角色名
     */
    private String name;

    /**
     * 是否编辑
     */
    private String editable;

    /**
     * 是否唯一
     */
    private String only;

}
