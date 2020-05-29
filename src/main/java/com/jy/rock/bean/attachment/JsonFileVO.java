package com.jy.rock.bean.attachment;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Strings;
import lombok.Data;

import java.util.Base64;

/**
 * @author hzhou
 */
@Data
public class JsonFileVO {

    private String id;

    private String name;

    private String contentType;

    private byte[] content;

    @JsonSetter("content")
    public void setContent(String content) {
        if (!Strings.isNullOrEmpty(content)) {
            this.content = Base64.getDecoder().decode(content);
        }
    }
}
