package risk.engine.dto.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/4 20:31
 * @Version: 1.0
 */
@Data
public class PenaltyActionVO {

    String penaltyCode;

    List<PenaltyFieldVO> penaltyFields;


}
