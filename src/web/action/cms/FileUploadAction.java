package web.action.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.opensymphony.xwork2.Action;

public class FileUploadAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String uploadContentType;     
	private String uploadFileName;
	private String CKEditorFuncNum;
	private String CKEditor;
	private String langCode;
	private File upload;
	private String saveDir;
	private String imgUrl;
	
	public String handle() throws Exception{
		 String strPath = ServletActionContext.getServletContext().getRealPath(this.getSaveDir());  
		 Calendar cd = Calendar.getInstance();
		 String year = cd.get(Calendar.YEAR) + "";
		 String month = (cd.get(Calendar.MONTH) + 1) + "";
		 String secondFilePath = year + File.separator + month;
		 strPath = strPath+ File.separator + secondFilePath;
         File path = new File(strPath);  
         if(!path.exists()){  
             path.mkdirs();  
         }  
         InputStream is = new FileInputStream(this.upload);
         logger.info("fullPath:{}",strPath + File.separator + this.uploadFileName);
         uploadFileName = cd.getTime().getTime() + uploadFileName.substring(uploadFileName.lastIndexOf("."),uploadFileName.length());
         OutputStream os = new FileOutputStream(new File(strPath + File.separator + uploadFileName));
           
         try {
             int len;  
             byte[] buffer = new byte[1024];  
             while ((len=is.read(buffer)) > 0) {  
                 os.write(buffer,0,len);  
             }  
         } catch (Exception e) {  
             e.printStackTrace();  
         } finally {  
             if(is!=null){  
                 is.close();  
             }  
             if(os!=null){  
                 os.close();  
             }  
         }  
         PrintWriter out = ServletActionContext.getResponse().getWriter();  
         //返回给ckeditor  
         out.write("<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction("+this.CKEditorFuncNum+", '"+ this.getImgUrl() + this.getSaveDir() + year+"\\/"+month+"\\/" + this.uploadFileName+"', '');</script>");  
         return Action.NONE; 
	}
	
	public String getUploadContentType() {
		return uploadContentType;
	}
	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getCKEditorFuncNum() {
		return CKEditorFuncNum;
	}
	public void setCKEditorFuncNum(String cKEditorFuncNum) {
		CKEditorFuncNum = cKEditorFuncNum;
	}
	public String getCKEditor() {
		return CKEditor;
	}
	public void setCKEditor(String cKEditor) {
		CKEditor = cKEditor;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public File getUpload() {
		return upload;
	}
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public String getSaveDir() {
		return saveDir;
	}
	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
