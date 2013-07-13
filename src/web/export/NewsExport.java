package web.export;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.enums.ContentType;

public class NewsExport {
	
	public static Workbook exportConditonQueryData(List<News> dataList,String baseUrl) throws Exception {
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
		//worksheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 9));
		
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
		HSSFCellStyle cellStyle02 = workbook.createCellStyle();
		
		HSSFFont font02 = workbook.createFont();
		font02.setColor(HSSFFont.COLOR_RED);
		font02.setFontHeightInPoints((short) 9);
		cellStyle02.setFont(font02);
		cellStyle02.setWrapText(true);
		cellStyle02.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle02.setBorderLeft(CellStyle.BORDER_THIN);
		
		//导出excel表头
		Row row2 = worksheet.createRow(0);
		Cell row2cell0 = row2.createCell((short) 0,Cell.CELL_TYPE_STRING);
		Cell row2cell1 = row2.createCell((short) 1,Cell.CELL_TYPE_STRING);
		Cell row2cell2 = row2.createCell((short) 2,Cell.CELL_TYPE_STRING);
		Cell row2cell3 = row2.createCell((short) 3,Cell.CELL_TYPE_STRING);
		Cell row2cell4 = row2.createCell((short) 4,Cell.CELL_TYPE_STRING);
		Cell row2cell5 = row2.createCell((short) 5,Cell.CELL_TYPE_STRING);
		Cell row2cell6 = row2.createCell((short) 6,Cell.CELL_TYPE_STRING);
		Cell row2cell7 = row2.createCell((short) 7,Cell.CELL_TYPE_STRING);
		Cell row2cell8 = row2.createCell((short) 8,Cell.CELL_TYPE_STRING);
		row2cell0.setCellValue("新闻编号");
		row2cell1.setCellValue("新闻标题");
		row2cell2.setCellValue("责任编辑");
		row2cell3.setCellValue("目录编号");
		row2cell4.setCellValue("创建时间");
		row2cell5.setCellValue("最后修改时间 ");
		row2cell6.setCellValue("UV数据 ");
		row2cell7.setCellValue("点击量");
		row2cell8.setCellValue("链接");
		row2cell0.setCellStyle(cellStyle00);
		row2cell1.setCellStyle(cellStyle00);
		row2cell2.setCellStyle(cellStyle00);
		row2cell3.setCellStyle(cellStyle00);
		row2cell4.setCellStyle(cellStyle00);
		row2cell5.setCellStyle(cellStyle00);
		row2cell6.setCellStyle(cellStyle00);
		row2cell7.setCellStyle(cellStyle00);
		row2cell8.setCellStyle(cellStyle00);
		
		//内容
		for(int i=0;i<dataList.size();i++){
        	News news = dataList.get(i);
        	
        	Row row = worksheet.createRow(i + 1);
    		Cell rowcell0 = row.createCell((short) 0,Cell.CELL_TYPE_STRING);
    		Cell rowcell1 = row.createCell((short) 1,Cell.CELL_TYPE_STRING);
    		Cell rowcell2 = row.createCell((short) 2,Cell.CELL_TYPE_STRING);
    		Cell rowcell3 = row.createCell((short) 3,Cell.CELL_TYPE_STRING);
    		Cell rowcell4 = row.createCell((short) 4,Cell.CELL_TYPE_STRING);
    		Cell rowcell5 = row.createCell((short) 5,Cell.CELL_TYPE_STRING);
    		Cell rowcell6 = row.createCell((short) 6,Cell.CELL_TYPE_STRING);
    		Cell rowcell7 = row.createCell((short) 7,Cell.CELL_TYPE_STRING);
    		Cell rowcell8 = row.createCell((short) 8,Cell.CELL_TYPE_STRING);
    		
    		rowcell0.setCellValue(news.getId());
    		rowcell1.setCellValue(news.getTitle());
    		rowcell2.setCellValue(news.getAuthor());
    		rowcell3.setCellValue(news.getCateID());
    		rowcell4.setCellValue(news.getCreateTimeStr());
    		rowcell5.setCellValue(news.getUpdateTimeStr());
    		rowcell6.setCellValue(news.getUvData() == null ? "" : news.getUvData() + "");
    		rowcell7.setCellValue(news.getClick() == null ? "" : news.getClick() +"");
    		String url = news.getContentType().getValue() == ContentType.NEWSTYPE.getValue() ? baseUrl + news.getUrl() : "";
    		if (!url.equals("")) {
    			HSSFHyperlink hyperlink = new HSSFHyperlink(HSSFHyperlink.LINK_URL); 
        		hyperlink.setAddress(url);
        		rowcell8.setCellValue(new HSSFRichTextString(news.getUrl()));
        		rowcell8.setHyperlink(hyperlink);
    		} else {
    			rowcell8.setCellValue("");
    		}
        }
		
		return workbook;
	}
}
