package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 彩金卡导出
 * @author He Wang
 *
 */
public class GiftCardsExport {
	public static Workbook export(List<Coupon> list) throws Exception{
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

		row1cell0.setCellValue("序号");
		row1cell1.setCellValue("充值券id");
		row1cell2.setCellValue("密码");
		row1cell3.setCellValue("金额");
		row1cell4.setCellValue("有效期");
		row1cell5.setCellValue("状态");
 
        for(int i=0;i<list.size();i++){
        	Coupon coupon = list.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);

    		rowcell0.setCellValue((i + 1) + "");
    		rowcell1.setCellValue(coupon.getCpId() == null ? "" : coupon.getCpId() + "");
    		rowcell2.setCellValue(coupon.getSecret() == null ? "" : coupon.getSecret());
    		rowcell3.setCellValue(coupon.getAmount() == null ? "0" : coupon.getAmount() + "");
    		rowcell4.setCellValue(coupon.getExpireTime() == null ? "" : CoreDateUtils.formatDate(coupon.getExpireTime()));
    		rowcell5.setCellValue(coupon.getStatus() == null ? "" : coupon.getStatus().getName());
        }
		return workbook;
	}
}
