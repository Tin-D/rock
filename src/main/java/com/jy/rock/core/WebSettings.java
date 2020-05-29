package com.jy.rock.core;

import com.xmgsd.lan.gwf.core.SystemConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 网站配置
 *
 * @author hzhou
 */
@Component
@Data
public class WebSettings {

    @Value("${web.title}")
    private String title;

    @Value("${web.url}")
    private String url;

    @Value("${web.tel}")
    private String tel;

    @Value("${web.copyright}")
    private String copyright;

    private String version;

    private boolean enableValidCode;

    @Autowired
    public WebSettings(SystemConfig systemConfig) {
        this.enableValidCode = systemConfig.isEnableValidCode();
    }

    public String getVersion() {
        return System.getProperty("version");
    }
}
