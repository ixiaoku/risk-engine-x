package risk.engine.db.dao;

import risk.engine.db.entity.QueueFailedMessagePO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/26 15:47
 * @Version: 1.0
 */
public interface QueueFailedMessageMapper {

    List<QueueFailedMessagePO> selectByStatus(Integer status);

    int insert(QueueFailedMessagePO queueFailedMessagePO);

    int updateById(QueueFailedMessagePO queueFailedMessagePO);

}
