package risk.engine.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.Incident;

/**
 * 事件
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Mapper
public interface IncidentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Incident record);

    List<Incident> selectByExample(Incident incident);

    Incident selectByIncidentCode(String incidentCode);

    Incident selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Incident record);
}