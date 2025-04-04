package risk.engine.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.BlockchainBlockPO;

@Mapper
public interface BlockchainBlockMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BlockchainBlockPO record);

    List<BlockchainBlockPO> selectByExample(BlockchainBlockPO example);

    BlockchainBlockPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(BlockchainBlockPO record);
}