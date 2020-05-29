package com.jy.rock.service;

import com.google.common.base.Preconditions;
import com.jy.rock.bean.cable.CableQueryVO;
import com.jy.rock.bean.cable.CableVO;
import com.jy.rock.dao.CableDao;
import com.jy.rock.domain.Cable;
import com.xmgsd.lan.roadhog.exception.NoEntityWithIdException;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class CableServiceImpl extends BaseService<CableDao> implements SimpleCurdService<String, Cable>, PaginationService<CableQueryVO, CableVO> {
    @Override
    public BasePaginationMapper<CableVO> getPaginationMapper() {
        return this.getMapper();
    }

    @Nullable
    @Override
    public Cable get(@NotNull String id) {
        return this.getMapper().selectByPrimaryKey(id);
    }

    @NotNull
    @Override
    public Cable getOrError(@NotNull String id) throws IllegalArgumentException {
        return Preconditions.checkNotNull(this.get(id), new NoEntityWithIdException(id));
    }
}
