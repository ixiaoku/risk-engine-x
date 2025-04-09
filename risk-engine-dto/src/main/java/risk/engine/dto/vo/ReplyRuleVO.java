package risk.engine.dto.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/9 20:40
 * @Version: 1.0
 */
@Data
public class ReplyRuleVO {

    private String ruleName;

    private List<LinkedHashMap<String, Object>> conditions;

    private String logicScript;

    private Boolean resultFlag;

}
