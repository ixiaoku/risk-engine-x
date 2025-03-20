package risk.engine.service.handler.penalty;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import risk.engine.db.entity.ListData;
import risk.engine.db.entity.PenaltyRecord;
import risk.engine.dto.dto.ListDataDTO;
import risk.engine.dto.enums.PenaltyStatusEnum;
import risk.engine.service.handler.IPenaltyHandler;
import risk.engine.service.service.IListDataService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/16 22:07
 * @Version: 1.0
 */
@Slf4j
@Component
public class AppendListDataHandler implements IPenaltyHandler {

    @Autowired
    private IListDataService listDataService;


    @Override
    public PenaltyStatusEnum doPenalty(PenaltyRecord record) {

        try {
            List<ListDataDTO> listDataDTOS = JSON.parseArray(record.getPenaltyJson(), ListDataDTO.class);
            if (CollectionUtils.isEmpty(listDataDTOS)) {
                return PenaltyStatusEnum.FAIL;
            }
            listDataDTOS.forEach(listDataDTO -> {
                ListData listData = new ListData();
                listData.setListLibraryCode(listDataDTO.getListLibraryCode());
                listData.setListLibraryName(listDataDTO.getListLibraryName());
                listData.setStatus(listDataDTO.getStatus());
                listData.setListType(1);
                listData.setListDesc("qaq");
                listData.setListName(StringUtils.isEmpty(listDataDTO.getListName()) ? "qq" : listDataDTO.getListName());
                listData.setListCode("qq");
                listData.setListValue("qq");
                listData.setOperator("System");
                listData.setCreateTime(LocalDateTime.now());
                listData.setUpdateTime(LocalDateTime.now());
                listDataService.insert(listData);
            });
            System.out.println("处罚执行成功");
        } catch (Exception e) {
            log.error("加名单执行报错：{}", e.getMessage(), e);
            record.setStatus(PenaltyStatusEnum.WAIT.getCode());
            return PenaltyStatusEnum.WAIT;
        }
        return PenaltyStatusEnum.SUCCESS;
    }
}
