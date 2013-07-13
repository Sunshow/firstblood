package web.service.member;

import com.lehecai.core.api.user.MemberCredit;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface MemberCreditService {

	public MemberCredit get(long uid) throws ApiRemoteCallFailedException;
	public boolean add(long uid, long amount, String remark) throws ApiRemoteCallFailedException;
	public boolean deduct(long uid, long amount, String remark) throws ApiRemoteCallFailedException;
	
}
