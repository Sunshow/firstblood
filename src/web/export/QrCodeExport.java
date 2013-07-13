package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.core.api.openapi.QrCodeScan;

/**
 * 二维码扫描信息导出
 * @author He Wang
 *
 */
public class QrCodeExport {
	public static Workbook export(String qrCodeName, List<QrCodeScan> list) throws Exception{
		Workbook workbook = new HSSFWorkbook();
		Sheet worksheet = workbook.createSheet();
		worksheet.setDefaultColumnWidth(Short.parseShort("20"));
		workbook.setSheetName(0, "sheet1");
		
		Row rowTitle = worksheet.createRow(0);
		Cell rowTitlecell0 = rowTitle.createCell((short) 0,Cell.CELL_TYPE_STRING);
		rowTitlecell0.setCellValue(qrCodeName + "结果列表");
		
		Row row1 = worksheet.createRow(1);
		Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row1cell1 = row1.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row1cell2 = row1.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row1cell3 = row1.createCell((short) 3,Cell.CELL_TYPE_STRING);
		Cell row1cell4 = row1.createCell((short) 4,Cell.CELL_TYPE_STRING);
		Cell row1cell5 = row1.createCell((short) 5,Cell.CELL_TYPE_STRING);
		Cell row1cell6 = row1.createCell((short) 6,Cell.CELL_TYPE_STRING);

		row1cell0.setCellValue("");
		row1cell1.setCellValue("时间");
		row1cell2.setCellValue("总扫描次数");
		row1cell3.setCellValue("iPhone扫描次数");
		row1cell4.setCellValue("Android扫描次数");
		row1cell5.setCellValue("iPad扫描次数");
		row1cell6.setCellValue("其他扫描次数");
 
        for(int i=0;i<list.size();i++){
        	QrCodeScan qrCodeScan = list.get(i);
        	
        	Row row = worksheet.createRow(i + 2);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);

    		rowcell0.setCellValue((i + 1) + "");
    		rowcell1.setCellValue(qrCodeScan.getScanDateStr() == null ? "" : qrCodeScan.getScanDateStr());
    		rowcell2.setCellValue(qrCodeScan.getTotalScanNum() == null ? "" : qrCodeScan.getTotalScanNum() + "");
    		rowcell3.setCellValue(qrCodeScan.getIphoneScanNum() == null ? "" : qrCodeScan.getIphoneScanNum() + "");
    		rowcell4.setCellValue(qrCodeScan.getAndroidScanNum() == null ? "" : qrCodeScan.getAndroidScanNum() + "");
    		rowcell5.setCellValue(qrCodeScan.getIpadScanNum() == null ? "" : qrCodeScan.getIpadScanNum() + "");
    		rowcell6.setCellValue(qrCodeScan.getElseScanNum() == null ? "" : qrCodeScan.getElseScanNum() + "");
        }
		return workbook;
	}
}
