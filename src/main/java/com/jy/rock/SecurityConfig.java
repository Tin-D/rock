package com.jy.rock;

import com.xmgsd.lan.gwf.security.AbstractAuthConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author hzhou
 */
@Configuration
public class SecurityConfig extends AbstractAuthConfiguration {
    /**
     * 允许匿名访问的URL
     *
     * @return 允许匿名访问的URL
     */
    @Override
    protected String[] getAnonymousUrls() {
        return new String[]{
                "/",
                "/v2/api-docs",
                "/configuration/**",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/login",
                "/web_settings",
                "/dictionary_code",
                "/group_manager",
                "/code_image",
                "/reset_my_password",
                "/public/*",
                "/group",
                "/attachment/**"
        };
    }
}
