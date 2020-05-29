package com.jy.rock.domain;

import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author lin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorty")
public class Authorty extends BaseDomainWithGuidKey {

    /**
     * 角色ID
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 权限
     */
    private String authority;
    
}
