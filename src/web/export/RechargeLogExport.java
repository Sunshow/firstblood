package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.user.RechargeLog;

public class RechargeLogExport {

	public static Workbook export(List<RechargeLog> list) {
		Workbook workbook = new HSSFWorkbook();
		Sheet worksheet = workbook.createSheet();
		worksheet.setDefaultColumnWidth(Short.parseShort("20"));
		workbook.setSheetName(0, "sheet1");
		
		Row row1 = worksheet.createRow(0);
		Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row1cell1 = row1.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row1cell2 = row1.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row1cell3 = row1.createCell((short) 3,Cell.CELL_TYPE_STRING);
		Cell row1cell4 = row1.createCell((short) 4,Cell.CELL_TYPE_STRING);
		Cell row1cell5 = row1.createCell((short) 5,Cell.CELL_TYPE_STRING);
		Cell row1cell6 = row1.createCell((short) 6,Cell.CELL_TYPE_STRING);
		Cell row1cell7 = row1.createCell((short) 7,Cell.CELL_TYPE_STRING);
		Cell row1cell8 = row1.createCell((short) 8,Cell.CELL_TYPE_STRING);
		Cell row1cell9 = row1.createCell((short) 9,Cell.CELL_TYPE_STRING);
		Cell row1cell10 = row1.createCell((short) 10,Cell.CELL_TYPE_STRING);
		Cell row1cell11 = row1.createCell((short) 11,Cell.CELL_TYPE_STRING);
		Cell row1cell12 = row1.createCell((short) 12,Cell.CELL_TYPE_STRING);
		Cell row1cell13 = row1.createCell((short) 13,Cell.CELL_TYPE_STRING);
		Cell row1cell14 = row1.createCell((short) 14,Cell.CELL_TYPE_STRING);
		Cell row1cell15 = row1.createCell((short) 15,Cell.CELL_TYPE_STRING);
		row1cell0.setCellValue("流水号");
		row1cell1.setCellValue("用户名");
		row1cell2.setCellValue("用户编号");
		row1cell3.setCellValue("充值金额");
		row1cell4.setCellValue("到账金额");
		row1cell5.setCellValue("支付编号");
		row1cell6.setCellValue("充值类型");
		row1cell7.setCellValue("银行");
		row1cell8.setCellValue("发起时间");
		row1cell9.setCellValue("成功时间");
		row1cell10.setCellValue("状态");
		row1cell11.setCellValue("充值人");
		row1cell12.setCellValue("充值人编号");
		row1cell13.setCellValue("来源");
		row1cell14.setCellValue("请求信息");
		row1cell15.setCellValue("返回信息");
 
        for(int i=0;i<list.size();i++){
        	RechargeLog rl = list.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_STRING);
    		Cell rowcell10 = row.createCell((short) 10,Cell.CELL_TYPE_STRING);
    		Cell rowcell11 = row.createCell((short) 11,Cell.CELL_TYPE_STRING);
    		Cell rowcell12 = row.createCell((short) 12,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell13 = row.createCell((short) 13,Cell.CELL_TYPE_STRING);
    		Cell rowcell14 = row.createCell((short) 14,Cell.CELL_TYPE_STRING);
    		Cell rowcell15 = row.createCell((short) 15,Cell.CELL_TYPE_STRING);
    		
    		rowcell0.setCellValue(rl.getId());
    		rowcell1.setCellValue(rl.getUsername());
    		rowcell2.setCellValue(rl.getUid());
    		rowcell3.setCellValue(rl.getAmount());
    		rowcell4.setCellValue(rl.getPayAmount());
    		rowcell5.setCellValue(rl.getPayNo());
    		rowcell6.setCellValue(rl.getRechargeType() == null ? "" : rl.getRechargeType().getName());
    		rowcell7.setCellValue(rl.getBankType() == null ? "" : rl.getBankType().getName());
    		rowcell8.setCellValue(DateUtil.formatDate(rl.getCreatedTime(), DateUtil.DATETIME));
    		rowcell9.setCellValue(DateUtil.formatDate(rl.getSuccessTime(), DateUtil.DATETIME));
    		rowcell10.setCellValue(rl.getStatus() == null ? "" : rl.getStatus().getName());
    		rowcell11.setCellValue(rl.getRechargerUsername());
    		rowcell12.setCellValue(rl.getRechargerUid());
    		rowcell13.setCellValue(rl.getSourceId());
    		rowcell14.setCellValue(rl.getRequestInfo());
    		rowcell15.setCellValue(rl.getReturnInfo());
        }
		return workbook;
	}

}
