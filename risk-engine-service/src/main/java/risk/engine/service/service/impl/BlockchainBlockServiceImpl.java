package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.BlockchainBlockMapper;
import risk.engine.db.entity.BlockchainBlock;
import risk.engine.service.service.IBlockchainBlockService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/13 21:01
 * @Version: 1.0
 */
@Service
public class BlockchainBlockServiceImpl implements IBlockchainBlockService {

    @Resource
    private BlockchainBlockMapper blockchainBlockMapper;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return blockchainBlockMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(BlockchainBlock record) {
        return  blockchainBlockMapper.insert(record) > 0;
    }

    @Override
    public List<BlockchainBlock> selectByExample(BlockchainBlock example) {
        return blockchainBlockMapper.selectByExample(example);
    }

    @Override
    public BlockchainBlock selectByPrimaryKey(Long id) {
        return blockchainBlockMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKey(BlockchainBlock record) {
        return blockchainBlockMapper.updateByPrimaryKey(record) > 0;
    }
}
