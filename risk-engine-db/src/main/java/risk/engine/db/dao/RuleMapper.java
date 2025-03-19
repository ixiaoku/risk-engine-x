package risk.engine.db.dao;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.Rule;
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

    int insert(Rule record);

    List<Rule> selectByExample(RuleExample example);

    Rule selectByRuleCode(String incidentCode);

    List<Rule> selectByIncidentCode(String incidentCode);

    Rule selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Rule record);
}