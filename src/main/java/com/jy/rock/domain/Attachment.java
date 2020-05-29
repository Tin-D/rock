package com.jy.rock.domain;

import com.jy.rock.bean.attachment.JsonFileVO;
import com.jy.rock.enums.AttachmentRecorderType;
import com.jy.rock.enums.AttachmentType;
import com.xmgsd.lan.gwf.domain.User;
import com.xmgsd.lan.roadhog.mybatis.BaseDomainWithGuidKey;
import com.xmgsd.lan.roadhog.utils.LanUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 附件表
 *
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Attachment extends BaseDomainWithGuidKey {

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件类别
     */
    private AttachmentType type;

    /**
     * 附件内容
     */
    private byte[] content;

    private Integer size;

    private String contentType;

    private String recorderType;

    private String recorderId;

    @Column(updatable = false)
    private String createUserId;

    @Column(updatable = false)
    private String createUsername;

    @Column(updatable = false)
    private String createUserFullName;

    @Column(updatable = false)
    private LocalDateTime createTime;

    public Attachment() {
        this.createTime = LanUtils.now();
    }

    public Attachment(String name, byte[] content, User user) {
        this();
        this.name = name;
        this.content = content;
        if (this.content.length > 0) {
            this.size = this.content.length;
        }
        this.createUserId = user.getId();
        this.createUsername = user.getUsername();
        this.createUserFullName = user.getFullName();
    }

    public Attachment(@NotNull MultipartFile file, User user) throws IOException {
        this();
        this.name = file.getOriginalFilename();
        this.content = file.getBytes();
        if (this.content.length > 0) {
            this.size = this.content.length;
        }
        this.contentType = file.getContentType();
        this.createUserId = user.getId();
        this.createUsername = user.getUsername();
        this.createUserFullName = user.getFullName();
    }

    public Attachment(@NotNull JsonFileVO file, User user) {
        this();
        this.setId(file.getId());
        this.name = file.getName();
        this.content = file.getContent();
        if (this.content.length > 0) {
            this.size = this.content.length;
        }
        this.contentType = file.getContentType();
        this.createUserId = user.getId();
        this.createUsername = user.getUsername();
        this.createUserFullName = user.getFullName();
    }

    public Attachment(@NotNull JsonFileVO file, @NotNull AttachmentRecorderType recorderType, User user) {
        this();
        this.setId(file.getId());
        this.name = file.getName();
        this.content = file.getContent();
        if (this.content.length > 0) {
            this.size = this.content.length;
        }
        this.contentType = file.getContentType();
        this.recorderType = recorderType.name();
        this.createUserId = user.getId();
        this.createUsername = user.getUsername();
        this.createUserFullName = user.getFullName();
    }

    public Attachment(@NotNull String recorderId, @NotNull JsonFileVO file, @NotNull AttachmentRecorderType recorderType, User user) {
        this(file, recorderType, user);
        this.recorderId = recorderId;
    }
}
