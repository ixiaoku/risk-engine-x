package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.BlockchainBlockMapper;
import risk.engine.db.entity.BlockchainBlockPO;
import risk.engine.service.service.IBlockchainService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/13 21:01
 * @Version: 1.0
 */
@Service
public class BlockchainServiceImpl implements IBlockchainService {

    @Resource
    private BlockchainBlockMapper blockchainBlockMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return blockchainBlockMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(BlockchainBlockPO record) {
        return  blockchainBlockMapper.insert(record) > 0;
    }

    @Override
    public List<BlockchainBlockPO> selectByExample(BlockchainBlockPO example) {
        return blockchainBlockMapper.selectByExample(example);
    }

    @Override
    public BlockchainBlockPO selectByPrimaryKey(Long id) {
        return blockchainBlockMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(BlockchainBlockPO record) {
        return blockchainBlockMapper.updateByPrimaryKey(record) > 0;
    }
}
