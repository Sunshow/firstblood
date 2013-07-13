package com.bjgl.web.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileUtil {
	protected final static Logger logger = LoggerFactory.getLogger(FileUtil.class.getName());
	
	public static void uploadFile(File file, String toFilePath)
	throws FileNotFoundException, IOException {
		InputStream is = null;
		BufferedOutputStream os = null;
		try {
			is = new FileInputStream(file);
			os = new BufferedOutputStream(new FileOutputStream(toFilePath));
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
		}
	}
	
	public static void uploadFileAndMkdir(File file, String toFilePath, String pathName)
	throws FileNotFoundException, IOException {
		if (mkdir(pathName)) {
			InputStream is = null;
			BufferedOutputStream os = null;
			try {
				is = new FileInputStream(file);
				os = new BufferedOutputStream(new FileOutputStream(toFilePath));
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
			} catch (FileNotFoundException fnfe) {
				throw fnfe;
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
			}
		}
	}
	
	public static boolean mkdir(String pathname) {
		File path = new File(pathname);
		if (!path.exists()) {
			return path.mkdirs();
		}
		return true;
	}
	
	public static void rm(String pathname) {
		File file = new File(pathname);
		if(file.exists()) {
			if(file.isFile())
				file.delete();
		}
	}
	
	public static boolean write(String pathname,String content) {
		File file = new File(pathname);
		boolean b = true;
		try {
			if(!file.exists()) {
				mkdir(file.getParentFile().getPath());
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes("utf-8"));
			fos.flush();
			fos.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			b = false;
		}
		return b;
	}
	public static boolean write(String pathname,String content,String charset) {
		File file = new File(pathname);
		if(charset==null ||charset.equals("")){
			charset="utf-8";
		}
		boolean b = true;
		try {
			if(!file.exists()) {
				mkdir(file.getParentFile().getPath());
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes(charset));
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	public static String read(String pathname) {
		File file = new File(pathname);
		String s = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			s = new String(bytes);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String toURIPath(String pathname,boolean isDirectory) {
		String p = pathname;
		if (File.separatorChar != '/')
		    p = p.replace(File.separatorChar, '/');
		if (!p.startsWith("/") && p.length() > 0)
		    p = "/" + p;
		if (!p.endsWith("/") && isDirectory)
			p = p + "/";
		if (p.startsWith("//"))
			p = "//" + p;
		URI uri = null;
		try {
			uri = new URI(null, null, p, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri == null ? null : uri.toString();
	}
}
