package com.jy.rock.bean.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import com.xmgsd.lan.gwf.bean.LoginUser;
import com.xmgsd.lan.gwf.core.encrypt.EncryptStringUtil;
import com.xmgsd.lan.roadhog.utils.JSON;
import com.xmgsd.lan.roadhog.utils.LanUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 附件令牌
 *
 * @author hzhou
 */
@Data
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class AttachmentTokenVO {

    private String recorderId;

    private String attachmentId;

    private String userId;

    private LocalDateTime createTime;

    private Integer validateTime;

    @JsonIgnore
    private String sessionId;

    public AttachmentTokenVO(String recorderId) {
        this(recorderId, null, null);
    }

    public AttachmentTokenVO(String recorderId, String attachmentId) {
        this(recorderId, attachmentId, null);
    }

    public AttachmentTokenVO(String recorderId, String attachmentId, Integer validateTime) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser user = (LoginUser) authentication.getPrincipal();

        this.recorderId = recorderId;
        this.userId = user.getId();
        this.createTime = LanUtils.now();
        this.sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        this.validateTime = validateTime;
        this.attachmentId = attachmentId;
    }

    /**
     * 校验是否可以解密成功，成功就返回该对象，否则返回null
     *
     * @param input 令牌
     * @return null: 令牌校验失败
     */
    @Nullable
    public static AttachmentTokenVO check(@NotNull String input) {
        try {
            input = URLDecoder.decode(input, Charsets.UTF_8.name());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LoginUser user = (LoginUser) authentication.getPrincipal();

            String s = EncryptStringUtil.decrypt(input, user.getId(), RequestContextHolder.currentRequestAttributes().getSessionId());
            AttachmentTokenVO tokenVO = JSON.deserialize(s, AttachmentTokenVO.class);
            boolean checkSuccess = true;
            if (!Objects.equals(tokenVO.getUserId(), user.getId())) {
                // 校验错误：这个令牌不是分发给当前访问的用户的
                checkSuccess = false;
                log.debug("attachment token check error: currentUser check failed");
            } else if (tokenVO.getValidateTime() != null && tokenVO.getValidateTime() > 0) {
                // 校验错误：令牌有效期超时
                checkSuccess = tokenVO.getCreateTime().plusSeconds(tokenVO.getValidateTime()).isAfter(LanUtils.now());
                if (!checkSuccess) {
                    log.debug("attachment token check error: validateTime check failed");
                }
            }

            return checkSuccess ? tokenVO : null;
        } catch (Exception e) {
            log.error("attachment token invalid", e);
            return null;
        }
    }

    public String encrypt() throws JsonProcessingException {
        return EncryptStringUtil.encrypt(JSON.serialize(this), this.userId, this.sessionId);
    }
}
