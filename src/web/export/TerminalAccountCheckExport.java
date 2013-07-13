package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;

/**
 * 出票商对账单导出对象
 * @author chirowong
 *
 */
public class TerminalAccountCheckExport {
	public static Workbook export(List<TerminalAccountCheckItem> list) throws Exception{
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
		row1cell0.setCellValue("彩种");
		row1cell1.setCellValue("出票商");
		row1cell2.setCellValue("日期/期数");
		row1cell3.setCellValue("出票商出票金额");
		row1cell4.setCellValue("出票商中奖金额");
		row1cell5.setCellValue("乐和彩出票金额");
		row1cell6.setCellValue("乐和彩中奖金额");
		row1cell7.setCellValue("出票差额");
		row1cell8.setCellValue("出票差额原因");
		row1cell9.setCellValue("中奖差额");
		row1cell10.setCellValue("中奖差额原因");
		row1cell11.setCellValue("佣金");
		row1cell12.setCellValue("备注");
 
        for(int i=0;i<list.size();i++){
        	TerminalAccountCheckItem cl = list.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell10 = row.createCell((short) 10,Cell.CELL_TYPE_STRING);
    		Cell rowcell11 = row.createCell((short) 12,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell12 = row.createCell((short) 13,Cell.CELL_TYPE_STRING);
    		
    		rowcell0.setCellValue(cl.getLotteryType().getName());
    		rowcell1.setCellValue(cl.getTerminalCompanyName());
    		rowcell2.setCellValue(cl.getAmountCheckDate());
    		rowcell3.setCellValue(cl.getTerminalDrawMoney());
    		rowcell4.setCellValue(cl.getTerminalPrizeMoney());
    		rowcell5.setCellValue(cl.getLehecaiDrawMoney());
    		rowcell6.setCellValue(cl.getLehecaiPrizeMoney());
    		rowcell7.setCellValue(cl.getDrawMoneyDiff());
    		rowcell8.setCellValue(cl.getDrawDiffReason());
    		rowcell9.setCellValue(cl.getPrizeMoneyDiff());
    		rowcell10.setCellValue(cl.getPrizeDiffReason());
    		rowcell11.setCellValue(cl.getCommission());
    		rowcell12.setCellValue(cl.getMemo());
        }
		return workbook;
	}
}
