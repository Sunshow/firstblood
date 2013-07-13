package com.bjgl.web.action;

import com.bjgl.web.utils.CaptchaServiceSingleton;
import com.octo.captcha.service.CaptchaServiceException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RandomImageCode extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7148763944826457452L;
	private static final Log logger = LogFactory.getLog(RandomImageCode.class);//日志类实例
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	}

	protected void doGet(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		long start = System.currentTimeMillis();
		logger.debug("---------------------------------");
		byte[] captchaChallengeAsJpeg = null;
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			String captchaId = "";
			if(httpServletRequest.getSession(false) == null){
				captchaId = httpServletRequest.getSession(true).getId();
			}else{
				captchaId = httpServletRequest.getSession(false).getId();
			}
			logger.info("SessionId==========>"+captchaId);
			BufferedImage challenge = CaptchaServiceSingleton.getInstance()
					.getImageChallengeForID(captchaId,
							httpServletRequest.getLocale());

			JPEGImageEncoder jpegEncoder = JPEGCodec
					.createJPEGEncoder(jpegOutputStream);
			jpegEncoder.encode(challenge);
		} catch (IllegalArgumentException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		} catch (CaptchaServiceException e) {
			httpServletResponse
					.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
		httpServletResponse.setHeader("Cache-Control", "no-store");
		httpServletResponse.setContentType("image/JPEG");
		ServletOutputStream responseOutputStream = httpServletResponse
				.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
		long end = System.currentTimeMillis();
		logger.debug("-------------"+(end-start)+"ms-------------");
	}

}
