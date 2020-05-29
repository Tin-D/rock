package com.jy.rock.dao;

import com.jy.rock.bean.equipment.EquipmentQueryVO;
import com.jy.rock.bean.equipment.EquipmentVO;
import com.jy.rock.domain.Equipment;
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
public interface EquipmentDao extends CurdMapper<Equipment, String>, PaginationWithoutDuplicateMapper<EquipmentVO, String, EquipmentQueryVO> {

    @Override
    List<String> paginationIds(@Param("query") EquipmentQueryVO query);

    @Override
    List<EquipmentVO> findPaginationResultByIds(@Param("ids") Collection<String> ids, @Param("query") EquipmentQueryVO query);

    @Override
    default Function<EquipmentVO, String> idGetter() {
        return EquipmentVO::getId;
    }
}
