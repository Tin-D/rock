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
@Table(name = "user_info")
public class UserInfo extends BaseDomainWithGuidKey {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * 是否启用
     */
    private String enable;

    /**
     * 是否锁住
     */
    private String locked;

    /**
     * 更改密码
     */
    @Column(name = "need_change_password_when_login")
    private String needChangePasswordWhenLogin;
}
