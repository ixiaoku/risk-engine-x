package risk.engine.service.service.impl;

import org.springframework.stereotype.Service;
import risk.engine.db.dao.QueueFailedMessageMapper;
import risk.engine.db.entity.QueueFailedMessagePO;
import risk.engine.service.service.IQueueFailedMessageService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/26 15:51
 * @Version: 1.0
 */
@Service
public class QueueFailedMessageServiceImpl implements IQueueFailedMessageService {

    @Resource
    private QueueFailedMessageMapper queueFailedMessageMapper;

    @Override
    public List<QueueFailedMessagePO> selectByStatus(Integer status) {
        return queueFailedMessageMapper.selectByStatus(status);
    }

    @Override
    public boolean insert(QueueFailedMessagePO queueFailedMessagePO) {
        return queueFailedMessageMapper.insert(queueFailedMessagePO) > 0;
    }

    @Override
    public boolean updateById(QueueFailedMessagePO queueFailedMessagePO) {
        return queueFailedMessageMapper.updateById(queueFailedMessagePO) > 0;
    }
}
