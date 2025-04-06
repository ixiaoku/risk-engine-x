package risk.engine.service.service;

import risk.engine.db.entity.BlockchainBlockPO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/13 21:00
 * @Version: 1.0
 */
public interface IBlockchainService {

    boolean deleteByPrimaryKey(Long id);

    boolean insert(BlockchainBlockPO record);

    List<BlockchainBlockPO> selectByExample(BlockchainBlockPO example);

    BlockchainBlockPO selectByPrimaryKey(Long id);

    boolean updateByPrimaryKey(BlockchainBlockPO record);

}
