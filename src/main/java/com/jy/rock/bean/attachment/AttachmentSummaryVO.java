package com.jy.rock.bean.attachment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jy.rock.domain.Attachment;
import com.jy.rock.enums.AttachmentType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hzhou
 */
@Data
@NoArgsConstructor
public class AttachmentSummaryVO {
    private String id;

    private String name;

    private AttachmentType type;

    private Integer size;

    private String token;

    public AttachmentSummaryVO(Attachment attachment) {
        this.id = attachment.getId();
        this.name = attachment.getName();
        this.type = attachment.getType();
        this.size = attachment.getSize();
    }

    public AttachmentSummaryVO(Attachment attachment, Integer validateSeconds, boolean generateToken) throws JsonProcessingException {
        this(attachment);
        if (generateToken) {
            AttachmentTokenVO tokenVO = new AttachmentTokenVO(attachment.getRecorderId(), attachment.getId(), validateSeconds);
            this.token = tokenVO.encrypt();
        }
    }
}
