package web.service.member;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ThirdPartMemberType;

public interface ThirdPartMemberService {
	
	public Map<String, Object> queryResult(Long uid, Long ruid, String rusername, ThirdPartMemberType memberType, PageBean pageBean)
		throws ApiRemoteCallFailedException;
}