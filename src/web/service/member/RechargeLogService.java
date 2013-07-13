package web.service.member;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.RechargeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RechargeLogService {

	Map<String, Object> getResult(String id, String username,
                                  Integer minAmount, Integer maxAmount, Integer minPayAmount,
                                  Integer maxPayAmount, String payNo, String rechargeType,
                                  String bankType, List<WalletType> walletTypeList, Date cbeginDate, Date cendDate, Date sbeginDate,
                                  Date sendDate, String status, String sourceId,
                                  String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;

	Map<String, Object> getRechargeStatistics(String id, String username,
                                              Integer minAmount, Integer maxAmount, Integer minPayAmount,
                                              Integer maxPayAmount, String payNo, String rechargeType,
                                              String bankType, List<WalletType> walletTypeList, Date cbeginDate, Date cendDate, Date sbeginDate,
                                              Date sendDate, String status, String sourceId) throws ApiRemoteCallFailedException;

   RechargeLog getInfo(String id) throws ApiRemoteCallFailedException;
}
