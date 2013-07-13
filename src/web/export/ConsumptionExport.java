package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.user.ConsumptionLog;

public class ConsumptionExport {
	public static Workbook export(List<ConsumptionLog> list) throws Exception{
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
		row1cell0.setCellValue("流水号");
		row1cell1.setCellValue("用户名");
		row1cell2.setCellValue("交易时间");
		row1cell3.setCellValue("彩种类型");
		row1cell4.setCellValue("订单编号");
		row1cell5.setCellValue("方案编号");
		row1cell6.setCellValue("交易类型");
		row1cell7.setCellValue("状态");
		row1cell8.setCellValue("金额");
		row1cell9.setCellValue("可用余额");
		row1cell10.setCellValue("冻结余额");
 
        for(int i=0;i<list.size();i++){
        	ConsumptionLog cl = list.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell10 = row.createCell((short) 10,Cell.CELL_TYPE_NUMERIC);
    		
    		rowcell0.setCellValue(cl.getLogId());
    		rowcell1.setCellValue(cl.getUsername());
    		rowcell2.setCellValue(DateUtil.formatDate(cl.getCreatedTime(), DateUtil.DATETIME));
    		if (cl.getLotteryType() != null) {
    			rowcell3.setCellValue(cl.getLotteryType().getName());
    		} else {
    			rowcell3.setCellValue("");
    		}
    		rowcell4.setCellValue(cl.getOrderId());
    		rowcell5.setCellValue(cl.getPlanId());
    		rowcell6.setCellValue(cl.getTransType().getName());
    		rowcell7.setCellValue(cl.getStatus().getName());
    		rowcell8.setCellValue(cl.getAmount());
    		rowcell9.setCellValue(cl.getBalance());
    		rowcell10.setCellValue(cl.getFrozen());
        }
		return workbook;
	}
}
