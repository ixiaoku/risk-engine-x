package risk.engine.db.dao;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.RulePO;
import risk.engine.db.entity.example.RuleExample;

import java.util.List;

/**
 * 规则
 * @Author: X
 * @Date: 2025/3/12 19:35
 * @Version: 1.0
 */
@Mapper
public interface RuleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RulePO record);

    List<RulePO> selectByExample(RuleExample example);

    RulePO selectByRuleCode(String incidentCode);

    List<RulePO> selectByIncidentCode(String incidentCode);

    RulePO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(RulePO record);
}