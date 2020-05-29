package com.jy.rock.bean.attachment;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 包含附件的表单数据
 *
 * @author hzhou
 */
public interface FormDataWithAttachments {

    /**
     * 表单id
     *
     * @return 表单id
     */
    String getId();

    /**
     * 表单附件
     *
     * @return 表单附件
     */
    @Nullable
    List<JsonFileVO> getAttachments();

    /**
     * 获取该记录对应的附件记录的令牌
     *
     * @return 令牌
     * @throws JsonProcessingException 异常
     */
    default String getAttachmentListToken() throws JsonProcessingException {
        return new AttachmentTokenVO(this.getId(), null).encrypt();
    }
}
