package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.user.OperationLog;

public class OperationExport {
	public static Workbook export(List<OperationLog> list, boolean distinctMember) throws Exception{
		Workbook workbook = new HSSFWorkbook();
		Sheet worksheet = workbook.createSheet();
		worksheet.setDefaultColumnWidth(Short.parseShort("20"));
		workbook.setSheetName(0, "sheet1");
		
		
		if(distinctMember){
			Row row1 = worksheet.createRow(0);
			Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
			row1cell0.setCellValue("用户名");
	 
	        for(int i=0;i<list.size();i++){
	        	OperationLog cl = list.get(i);
	        	
	        	Row row = worksheet.createRow(i + 1);
	    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);

	    		rowcell0.setCellValue(cl.getUsername());
	        }
		}else{
			Row row1 = worksheet.createRow(0);
			Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
			Cell row1cell1 = row1.createCell((short) 1,Cell.CELL_TYPE_STRING);
			Cell row1cell2 = row1.createCell((short) 2,Cell.CELL_TYPE_STRING);
			Cell row1cell3 = row1.createCell((short) 3,Cell.CELL_TYPE_STRING);
			Cell row1cell4 = row1.createCell((short) 4,Cell.CELL_TYPE_STRING);
			Cell row1cell5 = row1.createCell((short) 5,Cell.CELL_TYPE_STRING);
			Cell row1cell6 = row1.createCell((short) 6,Cell.CELL_TYPE_STRING);
			Cell row1cell7 = row1.createCell((short) 7,Cell.CELL_TYPE_STRING);
			row1cell0.setCellValue("流水号");
			row1cell1.setCellValue("用户名");
			row1cell2.setCellValue("操作类型");
			row1cell3.setCellValue("操作时间");
			row1cell4.setCellValue("状态");
			row1cell5.setCellValue("操作ip地址");
			row1cell6.setCellValue("代理ID");
			row1cell7.setCellValue("扩展信息");
	 
	        for(int i=0;i<list.size();i++){
	        	OperationLog cl = list.get(i);
	        	
	        	Row row = worksheet.createRow(i + 1);
	    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
	    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
	    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
	    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
	    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
	    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
	    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
	    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
	    		
	    		rowcell0.setCellValue(cl.getId());
	    		rowcell1.setCellValue(cl.getUsername());
	    		rowcell2.setCellValue(cl.getOperationType().getName());
	    		rowcell3.setCellValue(DateUtil.formatDate(cl.getTimeline(), DateUtil.DATETIME));
	    		rowcell4.setCellValue(cl.getStatus().getName());
	    		rowcell5.setCellValue(cl.getIp());
	    		rowcell6.setCellValue(cl.getSourceId());
	    		rowcell7.setCellValue(cl.getExtra());
	        }
		}
		
		return workbook;
	}
}
