package com.jy.rock.dao;

import com.jy.rock.domain.CablePoint;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.springframework.stereotype.Repository;

/**
 * @author hzhou
 */
@Repository
public interface CablePointDao extends CurdMapper<CablePoint, String> {
}
