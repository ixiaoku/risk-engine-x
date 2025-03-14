package risk.engine.dto.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 区块信息
 * @Author: X
 * @Date: 2025/3/13 18:02
 * @Version: 1.0
 */
@Data
public class BlockchainBlockDTO {

    /**
     * 区块哈希
     */
    private String id;

    /**
     * 区块高度
     */
    private int height;

    /**
     * 版本
     */
    private int version;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 交易数量
     */
    private int tx_count;

    /**
     * 区块大小
     */
    private int size;

    /**
     * 区块重量
     */
    private int weight;

    /**
     * merkle root hash
     */
    private String merkle_root;

    /**
     * 父区块哈希
     */
    private String previousblockhash;

    /**
     * 中位时间
     */
    private long mediantime;

    /**
     * 随机数
     */
    private long nonce;

    /**
     * 难度目标
     */
    private int bits;

    /**
     * 区块难度
     */
    private BigDecimal difficulty;

}
