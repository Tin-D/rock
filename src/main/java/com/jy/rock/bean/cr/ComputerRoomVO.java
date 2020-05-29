package com.jy.rock.bean.cr;

import lombok.Data;
import lombok.ToString;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
public class ComputerRoomVO {

    private String id;

    private String name;

    private String customerId;

    private String remark;

    private String customerName;
}
