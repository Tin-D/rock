package com.jy.rock.service;

import com.jy.rock.bean.cr.ComputerRoomQueryVO;
import com.jy.rock.bean.cr.ComputerRoomVO;
import com.jy.rock.dao.ComputerRoomDao;
import com.jy.rock.domain.ComputerRoom;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hzhou
 */
@Slf4j
@Service
public class ComputerRoomServiceImpl extends BaseService<ComputerRoomDao>
        implements SimpleCurdViewService<String, ComputerRoom>, PaginationService<ComputerRoomQueryVO, ComputerRoomVO> {

    @Override
    public BasePaginationMapper<ComputerRoomVO> getPaginationMapper() {
        return this.getMapper();
    }

    @Override
    public void processQuery(ComputerRoomQueryVO query) {
        if ("name".equals(query.getSortField())) {
            query.setSortField("cr.name");
        } else if ("customer_name".equals(query.getSortField())) {
            query.setSortField("c.name");
        }
    }
}
