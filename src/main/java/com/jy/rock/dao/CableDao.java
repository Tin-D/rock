package com.jy.rock.dao;

import com.jy.rock.bean.cable.CableQueryVO;
import com.jy.rock.bean.cable.CableVO;
import com.jy.rock.domain.Cable;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import com.xmgsd.lan.roadhog.mybatis.mappers.PaginationMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hzhou
 */
@Repository
public interface CableDao extends CurdMapper<Cable, String>, PaginationMapper<CableVO, CableQueryVO> {

    @Override
    List<CableVO> pagination(@Param("query")CableQueryVO query);
}
