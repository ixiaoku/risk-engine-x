package risk.engine.service.service;

import risk.engine.db.entity.BlockchainBlock;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/13 21:00
 * @Version: 1.0
 */
public interface IBlockchainBlockService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(BlockchainBlock record);

    List<BlockchainBlock> selectByExample(BlockchainBlock example);

    BlockchainBlock selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(BlockchainBlock record);

}
