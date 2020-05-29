package com.jy.rock.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jy.rock.bean.attachment.FormDataWithAttachments;
import com.jy.rock.bean.attachment.JsonFileVO;
import com.xmgsd.lan.gwf.enums.WorkflowTaskAction;
import com.xmgsd.lan.roadhog.bean.BaseFormData;
import com.xmgsd.lan.roadhog.mybatis.DbItem;
import com.xmgsd.lan.roadhog.utils.JSON;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程表单基类
 *
 * @author hzhou
 */
@Getter
@Setter
public abstract class AbstractWorkflowForm<T extends DbItem> extends BaseFormData<T> implements FormDataWithAttachments {

    /**
     * 表单数据id
     */
    private String id;

    /**
     * 实例标题
     */
    private String name;

    /**
     * 流程实例id
     */
    private String instanceId;

    /**
     * 流程当前步骤代码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String stepCode;

    /**
     * 流程当前步骤名称
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String stepName;

    /**
     * 执行的动作
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private WorkflowTaskAction action;

    /**
     * 意见
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String remark;

    /**
     * 附件
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<JsonFileVO> attachments;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createUserId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createUsername;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createUserFullName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createTime;

    /**
     * 获取流程的审计日志
     *
     * @return 审计日志
     * @throws JsonProcessingException json序列化出错
     */
    @JsonIgnore
    public String getLogDetails() throws JsonProcessingException {
        Map<String, Object> params = new HashMap<>(6);
        params.put("id", this.getId());
        params.put("instanceId", this.getInstanceId());
        params.put("stepCode", this.getStepCode());
        params.put("stepName", this.getStepName());
        params.put("action", this.getAction());
        params.put("remark", this.getRemark());
        if (!CollectionUtils.isEmpty(this.attachments)) {
            params.put("附件数量", this.attachments.size());
        }
        return JSON.serialize(params);
    }
}
