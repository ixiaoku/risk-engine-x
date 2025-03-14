package risk.engine.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import risk.engine.db.entity.BlockchainBlock;

@Mapper
public interface BlockchainBlockMapper {

    int deleteByPrimaryKey(Long id);

    int insert(BlockchainBlock record);

    List<BlockchainBlock> selectByExample(BlockchainBlock example);

    BlockchainBlock selectByPrimaryKey(Long id);

    int updateByPrimaryKey(BlockchainBlock record);
}