package com.jy.rock.bean.equipment;

import com.jy.rock.bean.attachment.FormDataWithAttachments;
import com.jy.rock.bean.attachment.JsonFileVO;
import com.jy.rock.domain.Equipment;
import com.xmgsd.lan.roadhog.bean.BaseFormData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import tk.mybatis.mapper.weekend.Fn;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EquipmentVO extends BaseFormData<Equipment> implements FormDataWithAttachments {
    private String id;

    private String name;

    private String typeId;

    private String modelName;

    private String brandId;

    private String modelId;

    private String owner;

    private String customerName;

    private String serialNumber;

    private Date manufactureDate;

    private List<JsonFileVO> attachments;

    /**
     * 不同类型设备的扩展信息JSON
     */
    private String extendInfo;

    @Override
    protected Equipment toDbItem(@Nullable Collection<Fn<Equipment, Object>> ignoreFields) throws Exception {
        Equipment equipment = super.toDbItem(ignoreFields);
        if (this.manufactureDate != null) {
            equipment.setManufactureDate(LocalDateTime.ofInstant(manufactureDate.toInstant(), ZoneId.systemDefault()));
        }
        return equipment;
    }
}
