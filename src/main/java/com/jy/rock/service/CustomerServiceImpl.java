package com.jy.rock.service;

import com.google.common.collect.ImmutableList;
import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.bean.customer.CustomerVO;
import com.jy.rock.dao.CustomerDao;
import com.xmgsd.lan.roadhog.bean.IdNameEntry;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class CustomerServiceImpl extends BaseService<CustomerDao>
        implements SimpleCurdViewService<String, CustomerVO>, PaginationService<CustomerQueryVO, CustomerVO> {
    @Nullable
    @Override
    public CustomerVO get(@NotNull String id) {
        List<CustomerVO> items = this.getMapper().findPaginationResultByIds(ImmutableList.of(id), null);
        return items.isEmpty() ? null : items.get(0);
    }

    @Override
    public BasePaginationMapper<CustomerVO> getPaginationMapper() {
        return this.getMapper();
    }

    public List<IdNameEntry> listOptions() {
        return this.getMapper().listOptions();
    }
}
