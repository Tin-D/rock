package com.jy.rock.bean.cable;

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
public class CableQueryVO extends BasePaginationQuery {

    private String name;
}
