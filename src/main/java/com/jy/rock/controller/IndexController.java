package com.jy.rock.controller;

import com.jy.rock.core.WebSettings;
import com.xmgsd.lan.gwf.bean.CodeImageVO;
import com.xmgsd.lan.gwf.bean.LoginUser;
import com.xmgsd.lan.gwf.utils.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static com.xmgsd.lan.gwf.core.SystemConfig.VALID_CODE_ATTRIBUTE;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("")
public class IndexController {

    @Autowired
    public HttpSession session;

    @Autowired
    public WebSettings webSettings;

    @GetMapping("/web_settings")
    public WebSettings webSettings() {
        return this.webSettings;
    }

    @GetMapping("/current_user")
    public LoginUser getCurrentUser(@AuthenticationPrincipal LoginUser user) {
        return user;
    }

    @GetMapping(value = "/code_image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] codeImage() throws IOException {
        CodeImageVO codeImageVO = CodeUtil.generateCodeAndPic();
        session.setAttribute(VALID_CODE_ATTRIBUTE, codeImageVO.getCode());
        BufferedImage img = codeImageVO.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    @GetMapping("/unique_id")
    public String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
