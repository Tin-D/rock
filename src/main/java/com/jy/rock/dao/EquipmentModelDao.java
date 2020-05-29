package com.jy.rock.dao;

import com.jy.rock.domain.EquipmentModel;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.springframework.stereotype.Repository;

/**
 * @author hzhou
 */
@Repository
public interface EquipmentModelDao extends CurdMapper<EquipmentModel, String> {
}
