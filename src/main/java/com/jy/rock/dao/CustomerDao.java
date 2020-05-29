package com.jy.rock.dao;

import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.bean.customer.CustomerVO;
import com.jy.rock.domain.Customer;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import com.xmgsd.lan.roadhog.mybatis.mappers.PaginationWithoutDuplicateMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author hzhou
 */
@Repository
public interface CustomerDao extends CurdMapper<Customer, String>, PaginationWithoutDuplicateMapper<CustomerVO, String, CustomerQueryVO> {
    @Override
    List<String> paginationIds(@Param("query") CustomerQueryVO query);

    @Override
    List<CustomerVO> findPaginationResultByIds(@Param("ids") Collection<String> ids, @Param("query") CustomerQueryVO query);

    @Override
    default Function<CustomerVO, String> idGetter() {
        return CustomerVO::getId;
    }

    List<IdNameEntry> listOptions();
}
