package risk.engine.dto.dto.block;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 链上转账
 * @Author: X
 * @Date: 2025/3/14 13:48
 * @Version: 1.0
 */
@Data
public class ChainTransferDTO {

    /**
     * 转出地址
     */
    private String fromAddress;

    /**
     * 收款地址
     */
    private String toAddress;

    /**
     * 数量
     */
    private BigDecimal amount;

    /**
     * 折u价格
     */
    private BigDecimal uAmount;

    /**
     * 交易哈希
     */
    private String hash;

    /**
     * 区块高度
     */
    private Integer height;

    /**
     * 链
     */
    private String chain;

    /**
     * 代币
     */
    private String coin;

    /**
     * 手续费
     */
    private BigDecimal fee;

}
