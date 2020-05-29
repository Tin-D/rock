package com.jy.rock.bean.cr;

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
public class ComputerRoomQueryVO extends BasePaginationQuery {

    private String name;
}
