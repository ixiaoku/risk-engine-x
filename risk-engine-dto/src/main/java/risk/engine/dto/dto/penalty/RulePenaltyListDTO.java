package risk.engine.dto.dto.penalty;

import lombok.Data;
import risk.engine.dto.dto.ListDataDTO;

import java.util.List;

/**
 * 规则的处罚相关的名单
 * @Author: X
 * @Date: 2025/3/16 23:16
 * @Version: 1.0
 */
@Data
public class RulePenaltyListDTO {

    private String penaltyCode;

    private List<ListDataDTO> penaltyJson;

}
