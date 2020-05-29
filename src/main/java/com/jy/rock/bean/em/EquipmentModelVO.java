package com.jy.rock.bean.em;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jy.rock.bean.attachment.FormDataWithAttachments;
import com.jy.rock.bean.attachment.JsonFileVO;
import com.jy.rock.domain.EquipmentModel;
import com.xmgsd.lan.roadhog.bean.BaseFormData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EquipmentModelVO extends BaseFormData<EquipmentModel> implements FormDataWithAttachments {

    private String id;

    private String brandId;

    private String name;

    /**
     * 附件
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "instructions")
    private List<JsonFileVO> attachments;
}
