package web.export;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.ProvinceCityNameUtil;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.BankTypeForPay;
import com.lehecai.core.util.CoreDateUtils;

public class WithdrawExport {
	private static Set<BankType> chargeBankTypeSet = new HashSet<BankType>();
	
	static {
		chargeBankTypeSet.add(BankType.ICBC);
		chargeBankTypeSet.add(BankType.CMBC);
		chargeBankTypeSet.add(BankType.CBC);
		chargeBankTypeSet.add(BankType.ABC);
	}
	
	public static Workbook exportShengpay(List<WithdrawLog> dataList, String actionType) throws Exception {
		if (dataList == null || dataList.size() == 0) {
			return null;
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet worksheet = workbook.createSheet();
		workbook.setSheetName(0, "sheet1");
		
		//单元格样式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		worksheet.setDefaultColumnWidth(20);
		worksheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 9));
		
		Row row1 = worksheet.createRow(0);
		Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row1cell1 = row1.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row1cell2 = row1.createCell((short) 2,Cell.CELL_TYPE_STRING);
		row1cell0.setCellValue("批次号");
		HSSFCellStyle cellStyle00 = workbook.createCellStyle();
		HSSFFont font00 = workbook.createFont();
		font00.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellStyle00.setFont(font00);
		cellStyle00.setWrapText(true);
		cellStyle00.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle00.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle00.setBorderRight(CellStyle.BORDER_THIN);
		cellStyle00.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle00.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle00.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);// 设置背景色
		cellStyle00.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellStyle00.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		row1cell0.setCellStyle(cellStyle00);
		
		if (dataList != null && dataList.size() > 0) {
			row1cell1.setCellValue(dataList.get(0).getBatchNo());
		} else {
			row1cell1.setCellValue("");
		}
		row1cell1.setCellStyle(cellStyle00);
		
		row1cell2.setCellValue("注意：单个文件记录数不能超过3000条；\n" +
				"银行名称请填写完整名称：如“工商银行”；\n" +
				"当银行名称在“工商银行、建设银行、农业银行、招商银行、交通银行、" +
				"兴业银行、广东发展银行、中信银行、深圳发展银行、浦东发展银行”中时，" +
				"省份、城市、开户支行名称三列可以不填。");
		HSSFCellStyle cellStyle02 = workbook.createCellStyle();
		HSSFFont font02 = workbook.createFont();
		font02.setColor(HSSFFont.COLOR_RED);
		font02.setFontHeightInPoints((short) 9);
		cellStyle02.setFont(font02);
		cellStyle02.setWrapText(true);
		cellStyle02.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle02.setBorderLeft(CellStyle.BORDER_THIN);
		row1cell2.setCellStyle(cellStyle02);
		
		row1.setHeight((short)800);
		
		Row row2 = worksheet.createRow(1);
		Cell row2cell0 = row2.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row2cell1 = row2.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row2cell2 = row2.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row2cell3 = row2.createCell((short) 3,Cell.CELL_TYPE_STRING);
		Cell row2cell4 = row2.createCell((short) 4,Cell.CELL_TYPE_STRING);
		Cell row2cell5 = row2.createCell((short) 5,Cell.CELL_TYPE_STRING);
		Cell row2cell6 = row2.createCell((short) 6,Cell.CELL_TYPE_STRING);
		Cell row2cell7 = row2.createCell((short) 7,Cell.CELL_TYPE_STRING);
		Cell row2cell8 = row2.createCell((short) 8,Cell.CELL_TYPE_STRING);
		Cell row2cell9 = row2.createCell((short) 9,Cell.CELL_TYPE_STRING);
		row2cell0.setCellValue("商户流水号");
		row2cell1.setCellValue("省份");
		row2cell2.setCellValue("城市");
		row2cell3.setCellValue("开户支行名称");
		row2cell4.setCellValue("银行名称");
		row2cell5.setCellValue("收款人账户类型（C为个人B为企业）");
		row2cell6.setCellValue("收款人户名");
		row2cell7.setCellValue("收款方银行账号");
		row2cell8.setCellValue("付款金额（元）");
		row2cell9.setCellValue("付款理由");
		row2cell0.setCellStyle(cellStyle00);
		row2cell1.setCellStyle(cellStyle00);
		row2cell2.setCellStyle(cellStyle00);
		row2cell3.setCellStyle(cellStyle00);
		row2cell4.setCellStyle(cellStyle00);
		row2cell5.setCellStyle(cellStyle00);
		row2cell6.setCellStyle(cellStyle00);
		row2cell7.setCellStyle(cellStyle00);
		row2cell8.setCellStyle(cellStyle00);
		row2cell9.setCellStyle(cellStyle00);
		
				
		for(int i=0;i<dataList.size();i++){
        	WithdrawLog mgl = dataList.get(i);
        	
        	Row row = worksheet.createRow(i + 2);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
//    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_STRING);
    		
    		Double realAmount = getRealAmount(mgl.getBankType(), mgl.getAmount());
    		rowcell0.setCellValue((i+1)+"");
    		rowcell1.setCellValue(mgl.getProvinceName());
    		rowcell2.setCellValue(mgl.getCityName());
    		rowcell3.setCellValue(mgl.getBankBranch());
    		rowcell4.setCellValue(mgl.getBankType().getName());
    		rowcell5.setCellValue("C");
    		rowcell6.setCellValue(mgl.getBankRealname());
    		rowcell7.setCellValue(mgl.getBankCardno());
    		rowcell8.setCellValue(realAmount);
    		createCell(row, mgl, 9, actionType);
        }
		
		return workbook;
	}
	
	public static Workbook exportYeepay(List<WithdrawLog> dataList, String actionType) throws Exception {
		if (dataList == null || dataList.size() == 0) {
			return null;
		}
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
		row1cell0.setCellValue("批次号");
		row1cell1.setCellValue("订单号");
		row1cell2.setCellValue("账户名称");
		row1cell3.setCellValue("银行账号");
		row1cell4.setCellValue("开户银行");
		row1cell5.setCellValue("省");
		row1cell6.setCellValue("市");
		row1cell7.setCellValue("金额");
		row1cell8.setCellValue("打款原因");
		row1cell9.setCellValue("支行信息");
		
		Calendar cal = Calendar.getInstance();
		for(int i=0;i<dataList.size();i++){
        	WithdrawLog mgl = dataList.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
//    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_STRING);
    		
    		rowcell0.setCellValue(CoreDateUtils.formatDate(cal.getTime(), "yyyyMMddHHmmssSSSSS"));
    		rowcell1.setCellValue(mgl.getId());
    		rowcell2.setCellValue(mgl.getBankRealname());
    		rowcell3.setCellValue(mgl.getBankCardno());
    		rowcell4.setCellValue(mgl.getBankType().getName());
    		rowcell5.setCellValue(mgl.getProvinceName());
    		rowcell6.setCellValue(mgl.getCityName());
    		Double realAmount = getRealAmount(mgl.getBankType(), mgl.getAmount());
    		rowcell7.setCellValue(realAmount);
    		createCell(row, mgl, 8, actionType);
    		rowcell9.setCellValue(mgl.getBankBranch());
        }
		return workbook;
	}
	public static Workbook export(List<WithdrawLog> list) throws Exception{
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
		row1cell0.setCellValue("用户名");
		row1cell1.setCellValue("真实姓名");
		row1cell2.setCellValue("所属银行");
		row1cell3.setCellValue("分行");
		row1cell4.setCellValue("银行卡号");
		row1cell5.setCellValue("金额");
		row1cell6.setCellValue("提款类型");
		row1cell7.setCellValue("提款时间");
		row1cell8.setCellValue("状态");
 
        for(int i=0;i<list.size();i++){
        	WithdrawLog cl = list.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		
    		rowcell0.setCellValue(cl.getUsername());
    		rowcell1.setCellValue(cl.getBankRealname());
    		rowcell2.setCellValue(cl.getBankType().getName());
    		rowcell3.setCellValue(cl.getBankBranch());
    		rowcell4.setCellValue(cl.getBankCardno());
    		rowcell5.setCellValue(cl.getAmount());
    		rowcell6.setCellValue(cl.getWithdrawType().getName());
    		rowcell7.setCellValue(DateUtil.formatDate(cl.getTimeline(), DateUtil.DATETIME));
    		rowcell8.setCellValue(cl.getWithdrawStatus().getName());		
        }
		
		return workbook;
	}
	public static Workbook exportAlipay(List<WithdrawLog> dataList, String alipayAccount, String actionType) throws Exception {
		if (dataList == null || dataList.size() == 0)
			return null;
		double totalMoney = 0.00D;
		
		Workbook workbook = new HSSFWorkbook();
		Sheet worksheet = workbook.createSheet();
		worksheet.setDefaultColumnWidth(Short.parseShort("20"));
		workbook.setSheetName(0, "sheet1");
		
		Row row1 = worksheet.createRow(0);
		Cell row1cell0 = row1.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row1cell1 = row1.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row1cell2 = row1.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row1cell3 = row1.createCell((short) 3,Cell.CELL_TYPE_STRING);
		row1cell0.setCellValue("日期");
		row1cell1.setCellValue("总金额");
		row1cell2.setCellValue("总笔数");
		row1cell3.setCellValue("支付宝帐号(Email)");
		
		Row row2 = worksheet.createRow(1);
		Calendar cd = Calendar.getInstance();
		String today = "" + cd.get(Calendar.YEAR) + ((cd.get(Calendar.MONTH) + 1) > 9 ? (cd.get(Calendar.MONTH) + 1) : "0" + (cd.get(Calendar.MONTH) + 1)) + (cd.get(Calendar.DATE) > 9 ? (cd.get(Calendar.DATE)) : "0" + (cd.get(Calendar.DATE)));
		
		Cell row2cell0 = row2.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row2cell1 = row2.createCell((short) 1,Cell.CELL_TYPE_NUMERIC);
		Cell row2cell2 = row2.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row2cell3 = row2.createCell((short) 3,Cell.CELL_TYPE_STRING);

		row2cell0.setCellValue(today);
		//此处应改为总金额，在最后算出
		row2cell2.setCellValue(dataList.size() + "");
		row2cell3.setCellValue(alipayAccount);

		Row row3 = worksheet.createRow(2);
		Cell row3cell0 = row3.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row3cell1 = row3.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row3cell2 = row3.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row3cell3 = row3.createCell((short) 3,Cell.CELL_TYPE_STRING);
		Cell row3cell4 = row3.createCell((short) 4,Cell.CELL_TYPE_STRING);
		Cell row3cell5 = row3.createCell((short) 5,Cell.CELL_TYPE_STRING);
		Cell row3cell6 = row3.createCell((short) 6,Cell.CELL_TYPE_STRING);
		Cell row3cell7 = row3.createCell((short) 7,Cell.CELL_TYPE_STRING);
		Cell row3cell8 = row3.createCell((short) 8,Cell.CELL_TYPE_STRING);
		Cell row3cell9 = row3.createCell((short) 9,Cell.CELL_TYPE_STRING);
		
		row3cell0.setCellValue("商户流水号");
		row3cell1.setCellValue("收款银行户名");
		row3cell2.setCellValue("收款银行帐号");
		row3cell3.setCellValue("收款开户银行");
		row3cell4.setCellValue("收款银行所在省份");
		row3cell5.setCellValue("收款银行所在市");
		row3cell6.setCellValue("收款支行名称");
		row3cell7.setCellValue("金额");
		row3cell8.setCellValue("对公对私标志");
		row3cell9.setCellValue("备注");
		
		//设置为货币格式
		HSSFDataFormat format = (HSSFDataFormat) workbook.createDataFormat();
		HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle(); 
		cellStyle.setDataFormat(format.getFormat("#,##0.00"));
        
		Map<Integer, BankTypeForPay> alipayMap = BankTypeForPay.getAlipayMap();
        for(int i=0;i<dataList.size();i++){
        	WithdrawLog mgl = dataList.get(i);
        	
        	Row row = worksheet.createRow(i + 3);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_NUMERIC);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		Cell rowcell9 = row.createCell((short) 9,Cell.CELL_TYPE_STRING);
    		
    		Double realAmount = getRealAmount(mgl.getBankType(), mgl.getAmount());
    		rowcell0.setCellValue((i+1)+"");
    		//替换名字中的有个各种点为特殊字符中的点
    		rowcell1.setCellValue(replaceDot(mgl.getBankRealname()));
    		rowcell2.setCellValue(mgl.getBankCardno());
    		//替换支付宝中特殊银行名称
    		if (alipayMap.get(mgl.getBankType().getValue()) != null) {
    			rowcell3.setCellValue(alipayMap.get(mgl.getBankType().getValue()).getName());
    		} else {
    			rowcell3.setCellValue(mgl.getBankType().getName());
    		}
    		
    		rowcell4.setCellValue(ProvinceCityNameUtil.reviseProvince(mgl.getProvinceName()));
    		rowcell5.setCellValue(ProvinceCityNameUtil.reviseCity(mgl.getCityName()));
    		rowcell6.setCellValue(mgl.getBankBranch());
    		rowcell7.setCellValue(realAmount);
    		rowcell7.setCellStyle(cellStyle);
    		rowcell8.setCellValue("2");
    		//createCell(row, mgl, 9, actionType);
    		rowcell9.setCellValue(mgl.getId() + "");
    		totalMoney = totalMoney + realAmount;
        }
		
        row2cell1.setCellValue(Double.valueOf(totalMoney));
        row2cell1.setCellStyle(cellStyle);
		return workbook;
	}
	
	public static Double getRealAmount(BankType bankType, Double amount) {
		if(chargeBankTypeSet.contains(bankType)) {
			if(amount < 50000.00D) {
				amount = amount - 2;
			} else {
				amount = amount - 10;
			}
		} else {
			if(amount < 50000.00D) {
				amount = amount - 1;
			} else {
				amount = amount - 5;
			} 
		}
		return amount;
	}
	
	private static void createCell(Row row, WithdrawLog mgl, int i, String actionType) {
		Cell rowcell = row.createCell((short)i,Cell.CELL_TYPE_STRING);
		if (actionType != null) {
			if (actionType.equals("internal")) {
				rowcell.setCellValue("内部员工提款");
			} else if (actionType.equals("default")) {
				if (chargeBankTypeSet.contains(mgl.getBankType())) {
					rowcell.setCellValue("普通用户提款,5万以下每笔手续费2元，5万以上每笔手续费10元，由用户交纳");
				} else {
					rowcell.setCellValue("普通用户提款,5万以下每笔手续费1元，5万以上每笔手续费5元，由用户交纳");
				}
			}
		} else {
			if (chargeBankTypeSet.contains(mgl.getBankType())) {
				rowcell.setCellValue("普通用户提款,5万以下每笔手续费2元，5万以上每笔手续费10元，由用户交纳");
			} else {
				rowcell.setCellValue("普通用户提款,5万以下每笔手续费1元，5万以上每笔手续费5元，由用户交纳");
			}
		}
	}
	
	/**
	 * 替换名字中的点为特殊字符中的点
	 * @param name
	 * @return
	 */
	private static String replaceDot(String name) {
		if (name == null) {
			return null;
		}
		name = StringUtils.replace(name, ".", "·");
		name = StringUtils.replace(name, "。", "·");
		name = StringUtils.replace(name, "．", "·");
		return name;
	}
}
