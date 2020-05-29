package com.jy.rock.dao;

import com.jy.rock.bean.cr.ComputerRoomQueryVO;
import com.jy.rock.bean.cr.ComputerRoomVO;
import com.jy.rock.domain.ComputerRoom;
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
public interface ComputerRoomDao extends CurdMapper<ComputerRoom, String>, PaginationWithoutDuplicateMapper<ComputerRoomVO, String, ComputerRoomQueryVO> {
    @Override
    List<String> paginationIds(@Param("query") ComputerRoomQueryVO query);

    @Override
    List<ComputerRoomVO> findPaginationResultByIds(@Param("ids") Collection<String> ids, @Param("query") ComputerRoomQueryVO query);

    @Override
    default Function<ComputerRoomVO, String> idGetter() {
        return ComputerRoomVO::getId;
    }
}
