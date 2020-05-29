package com.jy.rock;

import com.xmgsd.lan.roadhog.mybatis.ConfigBuildHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * @author hzhou
 */
@SpringBootApplication
public class RockApplication {

    public static void main(String[] args) throws IOException {
        ConfigBuildHelper.loadPropertiesFile("env.properties");
        SpringApplication.run(RockApplication.class, args);
    }

}

