package com.utils;

import com.logger.JLogger;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author cc
 *
 */
public class HttpRequest {
    private final static String DELIMITER="&";
    private final static String PARAM_KEY_SIGN="sign";
    private final static int DEFAULT_OUT_TIME=5000;

    public static String requestSign(Map<String, Object> params, String signKey) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        if(params.containsKey(PARAM_KEY_SIGN)){
            params.remove(PARAM_KEY_SIGN);
        }
        Map<String, Object> map= new TreeMap<>(params);
        StringBuilder ret=new StringBuilder("");
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if(ret.length()!=0){
                ret.append(DELIMITER);
            }
            ret.append(map.get(key));
        }
        return MD5.get32MD5(ret.append(signKey).toString(), false);
    }
    
    public static String requestParam(Map<String, Object> params){
        StringBuilder sb = new StringBuilder();
        for(Entry<String, Object> e : params.entrySet()){
            if(sb.length()!=0){
                sb.append(DELIMITER);
            }
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        
        return sb.toString();
    }
    
    public static String httpPost(String url, Map<String, Object> paramsMap) throws IOException{
        String params=requestParam(paramsMap);
		return httpRequest("POST",url,params);
    }
    
    public static String httpGet(String url, Map<String, Object> paramsMap) throws IOException{
        String params=requestParam(paramsMap);
        return httpRequest("GET",url,params);
    }
    
    public static String httpPost(String url, String params) throws IOException{
        return httpRequest("POST",url,params);
    }
    
    public static String httpGet(String url, String params) throws IOException{
        return httpRequest("GET",url,params);
    }
    
    /**
     * @param requestMethod
     * @param postUrl
     * @param params
     * @return
     * @throws IOException
     */
    public static String httpRequest(String requestMethod,String postUrl,String params) throws IOException{
    	return httpRequest(DEFAULT_OUT_TIME, requestMethod, postUrl, params);
    }
    
    /**
     * 
     * @param timeout
     * @param requestMethod
     * @param postUrl
     * @param params
     * @return
     * @throws IOException
     */
    public static String httpRequest(int timeout, String requestMethod,String postUrl,String params) throws IOException{
    	//TODO Proxy
    	HttpURLConnection httpURLConnection = null;
    	OutputStreamWriter osw=null;
    	BufferedWriter out = null;
    	InputStreamReader isr=null;
    	BufferedReader in = null;
		try {
			URL url = new URL(postUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// ?????????????????? 
	        httpURLConnection.setDoOutput(true);
	        // ?????????????????? 
	        httpURLConnection.setDoInput(true);
	        // ?????????????????? 
	        httpURLConnection.setRequestMethod(requestMethod);
	        // ?????????????????? 
	        httpURLConnection.setConnectTimeout(timeout);
	        httpURLConnection.setReadTimeout(timeout); 
	        httpURLConnection.connect();
	        
	        osw=new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
	        out = new BufferedWriter(osw);
	        // ???post?????????????????????&???????????? 
	        out.write(params);
	        out.flush(); 
	
	        //??????post?????????????????? 
			isr = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
	        in = new BufferedReader(isr); 
	        String line = null; 
	        StringBuilder sb = new StringBuilder(); 
	        while ((line = in.readLine()) != null) { 
	        	sb.append(line); 
	        } 

	        return sb.toString();
		}catch(IOException e){
			throw e;
		}finally{
	        safeClose(in);
	        safeClose(isr);
	        safeClose(out);
	        safeClose(osw);
	        safeClose(httpURLConnection);
		}
    }



	public static String send(String url, Object jsonObject,String encoding) throws ParseException, IOException{
		String body = "";
		CloseableHttpResponse response = null;
		try {
			//??????httpclient??????
			CloseableHttpClient client = HttpClients.createDefault();
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(1000)
					.setSocketTimeout(1000).setConnectTimeout(1000).build();

			//??????post??????????????????
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			//????????????
			StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
			s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			//??????????????????????????????
			httpPost.setEntity(s);
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			//??????????????????????????????????????????????????????
			response = client.execute(httpPost);
			//??????????????????
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				//????????????????????????????????????String??????
				body = EntityUtils.toString(entity, encoding);
			}
			EntityUtils.consume(entity);
		}catch (Exception e){
			throw e;
		}finally {
			if(response != null){
				//????????????
				response.close();
			}
		}
		return body;
	}

    private static void safeClose(HttpURLConnection conn) {
		if(conn!=null){
			try{
				conn.disconnect();//????????????
			}catch(Exception e){
				JLogger.error("Failed to disconnect HttpURLConnection", e);
			}
		}
	}

	private static void safeClose(Closeable closer) {
		if (closer != null) {
			try {
				closer.close();
			} catch (Exception e) {
				JLogger.error("Failed to close BufferedReader", e);
			}
		}
	}
}