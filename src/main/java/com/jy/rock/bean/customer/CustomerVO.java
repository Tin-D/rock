package com.jy.rock.bean.customer;

import com.jy.rock.domain.Customer;
import com.xmgsd.lan.roadhog.bean.BaseFormData;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
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
public class CustomerVO extends BaseFormData<Customer> {

    private String id;

    private String name;

    private String chargeUserFullName;

    private String chargeUserEmail;

    private String chargeUserPhone1;

    private String chargeUserPhone2;

    private String chargeUserPhone3;

    private List<IdNameEntry> computerRooms;
}
