package com.jy.rock.bean.cable;

import com.jy.rock.domain.CablePoint;
import lombok.Data;

import java.util.List;

/**
 * @author hzhou
 */
@Data
public class CableVO {

    private String id;

    private String name;

    private String inEquipmentId;

    private String inEquipmentName;

    private String outEquipmentId;

    private String outEquipmentName;

    private String typeId;

    private List<CablePoint> points;
}
