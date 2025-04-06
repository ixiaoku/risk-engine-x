package risk.engine.dto.dto.block;

import lombok.Data;
import risk.engine.dto.dto.penalty.AnnouncementDTO;

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
     * 发送地址
     */
    private String sendAddress;

    /**
     * 接收地址
     */
    private String receiveAddress;

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
    private String token;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 交易转账时间
     */
    private long transferTime;

    /**
     * 推送公告
     */
    private AnnouncementDTO announcement;

}
