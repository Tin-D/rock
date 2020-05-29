package com.jy.rock.bean.equipment;

import com.xmgsd.lan.roadhog.bean.BasePaginationQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EquipmentQueryVO extends BasePaginationQuery {

    private String name;

    private String modelId;

    private String serialNumber;
}
