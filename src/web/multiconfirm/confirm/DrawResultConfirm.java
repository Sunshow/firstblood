package web.multiconfirm.confirm;

import java.util.List;

import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;

public class DrawResultConfirm extends AbstractMulticonfirmConfirm {
	
	@Override
	protected boolean doConfirm(List<MulticonfirmRecord> recordList, StringBuffer sb) {
		
		if (sb == null) {
			sb = new StringBuffer();
		}
		
		sb.append("验证信息:<br />"); 
		boolean confirm = true;
		String result = "";
		for (MulticonfirmRecord record : recordList) {
			if (result == null || result.equals("")) {
				result = record.getResult();
				continue;
			} 
			String currentResult = record.getResult();
			String[] resultArray = result.split("\\,");
			String[] currentArray = currentResult.split("\\,");
			if (resultArray.length != currentArray.length) {
				sb.append("开奖号码个数不一致");
				confirm = false;
				break;
			}
			boolean flag = true;
			for (int i = 0; i < resultArray.length; i++) {
				if (!resultArray[i].equals(currentArray[i])) {
					sb.append("开奖号码第").append(i + 1).append("位有不一致输入,多次确认失败,请检查后重新输入");
					flag = false;
				}
			}
			if (!flag) {
				confirm = false;
				break;
			}
		}
		return confirm;
	}

}
