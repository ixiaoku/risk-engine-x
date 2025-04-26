package risk.engine.service.service;

import risk.engine.db.entity.QueueFailedMessagePO;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/26 15:50
 * @Version: 1.0
 */
public interface IQueueFailedMessageService {

    List<QueueFailedMessagePO> selectByStatus(Integer status);

    boolean insert(QueueFailedMessagePO queueFailedMessagePO);

    boolean updateById(QueueFailedMessagePO queueFailedMessagePO);

    void updateAsync(QueueFailedMessagePO queueFailedMessagePO);

}
