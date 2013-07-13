package web.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sun.net.www.protocol.https.HttpsURLConnectionImpl;

public class HttpUtil {

	@SuppressWarnings("unchecked")
	static public String getHttpUrl(String urlString) throws Exception{

		URL url = null;
		URLConnection connection = null;
		BufferedReader in = null;

			url = new URL(urlString);
			connection = url.openConnection();
			connection.setReadTimeout(60000);
			if (connection instanceof HttpsURLConnectionImpl) {

				HttpsURLConnection https = (HttpsURLConnection) connection;
				https.setHostnameVerifier(new MyVerified());

				X509TrustManager xtm = new MyTrustManager();
				TrustManager mytm[] = { xtm };
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(null, mytm, null);
				SSLSocketFactory sf = ctx.getSocketFactory();
				https.setSSLSocketFactory(sf);

			}

			Map headers = connection.getHeaderFields();
			if (headers.size() > 0) {
				String response = headers.get(null).toString();
				if (response.indexOf("200 OK") < 0) {
					throw new Exception("读取地址:" + url + " 错误:" + response);
				}
			}
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			String temp = in.readLine();
			return temp;
	}
	
	@SuppressWarnings("unchecked")
	static public String getHttpUrlContent(String urlString,String postString) throws Exception{
		URL url = null;
		URLConnection connection = null;
		BufferedReader in = null;
		BufferedWriter writer = null;
			url = new URL(urlString);
			connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(60000);
			if (connection instanceof HttpsURLConnectionImpl) {

				HttpsURLConnection https = (HttpsURLConnection) connection;
				https.setHostnameVerifier(new MyVerified());

				X509TrustManager xtm = new MyTrustManager();
				TrustManager mytm[] = { xtm };
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(null, mytm, null);
				SSLSocketFactory sf = ctx.getSocketFactory();
				https.setSSLSocketFactory(sf);

			}
			
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "GBK"));

			writer.write(postString);
			writer.flush();
			writer.close();

			Map headers = connection.getHeaderFields();
			if (headers.size() > 0) {
				String response = headers.get(null).toString();
				if (response.indexOf("200 OK") < 0) {
					throw new Exception("读取地址:" + url + " 错误:" + response);
				}
			}
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String temp = "";
			while((temp = in.readLine())!=null){
				sb.append(temp);
			}
			return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	static public String getHttpUrlContent(String urlString)  throws Exception{

		URL url = null;
		URLConnection connection = null;
		BufferedReader in = null;

			url = new URL(urlString);
			connection = url.openConnection();
			connection.setReadTimeout(60000);
			if (connection instanceof HttpsURLConnectionImpl) {

				HttpsURLConnection https = (HttpsURLConnection) connection;
				https.setHostnameVerifier(new MyVerified());

				X509TrustManager xtm = new MyTrustManager();
				TrustManager mytm[] = { xtm };
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(null, mytm, null);
				SSLSocketFactory sf = ctx.getSocketFactory();
				https.setSSLSocketFactory(sf);

			}

			Map headers = connection.getHeaderFields();
			if (headers.size() > 0) {
				String response = headers.get(null).toString();
				if (response.indexOf("200 OK") < 0) {
					throw new Exception("读取地址:" + url + " 错误:" + response);
				}
			}
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String temp = "";
			while((temp = in.readLine())!=null){
				sb.append(temp);
			}
			return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	static public List getHttpUrlContentList(String urlString)  throws Exception{

		URL url = null;
		URLConnection connection = null;
		BufferedReader in = null;

			url = new URL(urlString);
			connection = url.openConnection();
			connection.setReadTimeout(60000);
			if (connection instanceof HttpsURLConnectionImpl) {

				HttpsURLConnection https = (HttpsURLConnection) connection;
				https.setHostnameVerifier(new MyVerified());

				X509TrustManager xtm = new MyTrustManager();
				TrustManager mytm[] = { xtm };
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(null, mytm, null);
				SSLSocketFactory sf = ctx.getSocketFactory();
				https.setSSLSocketFactory(sf);

			}

			Map headers = connection.getHeaderFields();
			if (headers.size() > 0) {
				String response = headers.get(null).toString();
				if (response.indexOf("200 OK") < 0) {
					throw new Exception("读取地址:" + url + " 错误:" + response);
				}
			}
			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			//StringBuffer sb = new StringBuffer();
			List ret = new ArrayList();
			String temp = "";
			while((temp = in.readLine())!=null){
				ret.add(temp);
			}
			return ret;
	}

}

class MyVerified implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}

class MyTrustManager implements X509TrustManager {
	MyTrustManager() { // constructor
		// create/load keystore
	}

	public void checkClientTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
		// special handling such as poping dialog boxes
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
