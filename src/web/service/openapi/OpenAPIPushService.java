package web.service.openapi;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.api.openapi.OpenAPIPush;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

import java.util.Date;
import java.util.Map;

public interface OpenAPIPushService {

    /**
     * 分页并多条件查询Push信息
     */
    Map<String, Object> findOpenAPIPushList(String id, String title, YesNoStatus status, OpenAPIAppType openAPIAppType, Date addTimeFrom, Date addTimeEnd, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;

    /**
     * 添加Push信息
     */
    boolean updateOpenAPIPush(OpenAPIPush openAPIPush) throws ApiRemoteCallFailedException;

    /**
     * 编辑Push信息
     */
    boolean addOpenAPIPush(OpenAPIPush openAPIPush) throws ApiRemoteCallFailedException;
}