package com.utils.translate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.utils.HttpRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用API版本
 * 参考 https://cloud.google.com/translate/v2/quickstart
 */
public class GoogleTranslateV2 implements ITranslate{
	private final static String DEFAULT_TRANSLATE_URL = "https://www.googleapis.com/language/translate/v2";
	
	private String translate_url;				//翻译地址
	private String api_key;								//密钥
	
	private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> translated = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();
	
	public GoogleTranslateV2(String apiKey){
		this.translate_url = DEFAULT_TRANSLATE_URL;
		this.api_key = apiKey;
	}
	
	public GoogleTranslateV2(String translateUrl, String apiKey){
		this.translate_url = translateUrl;
		this.api_key = apiKey;
	}
	
	/**
	 * 检测是否已翻译过
	 * @param text
	 * @param targetLanguage
	 * @return
	 */
	private String checkTranslated(String text, String targetLanguage){
		if(translated.containsKey(text)&&translated.get(text).containsKey(targetLanguage)){
			return translated.get(text).get(targetLanguage);
		}
		return "";
	}
	
	/**
	 * 更新已翻译内容
	 * @param text
	 * @param targetLanguage
	 * @param resultText
	 */
	private void updateTranslated(String text, String targetLanguage, String resultText) {
		translated.putIfAbsent(text, new ConcurrentHashMap<String, String>());
		if(translated.get(text).replace(targetLanguage, resultText)==null){
			translated.get(text).put(targetLanguage, resultText);
		}
	}
	
	/**
	 * 提取翻译
	 * @param ret
	 * @return 最终翻译文字
	 * @throws TranslateFailedException 
	 */
	private String extract(String ret) throws TranslateFailedException{
	    try{
			JSONObject js = JSON.parseObject(ret);
			if (js.containsKey("data") && js.getJSONObject("data").containsKey("translations")) {
    			return js.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");
    		}
    		throw new TranslateFailedException("extract error");
	    }catch (JSONException e){
	        throw new TranslateFailedException(e.getMessage());
	    }
	}
	
	private String convertLanguage(String gameLanguage) {
		String[] sp = gameLanguage.split("[_]");
		/**
		 * 游戏内zh_cn, zh_tw,ko_kr, en_us,... 为 语种_地区 格式,均为小写
		 * 谷歌要求的是语种, 目前只有简中和繁中需要特殊处理
		 * 参考https://cloud.google.com/translate/v2/translate-reference
		 */
		if(sp[0].equalsIgnoreCase("zh")){
			return sp[0].toLowerCase()+"-"+sp[1].toUpperCase();
		}
		return sp[0].toLowerCase();
	}
	
	@Override
	public String translate(String text, String targetLanguage, String sourceLanguage) throws TranslateException, TranslateFailedException {
		if(targetLanguage.equals(sourceLanguage)){
			return text;
		}
		String chk = checkTranslated(text, targetLanguage);
		if(!chk.equals("")){
			return chk;
		}
		try{
			HashMap<String, Object> paramsMap = new HashMap<String, Object> ();
			paramsMap.put("q", URLEncoder.encode(text,"UTF-8"));
			paramsMap.put("key", this.api_key);
			if(!sourceLanguage.equals("")){
				paramsMap.put("source", convertLanguage(sourceLanguage));
			}
			paramsMap.put("target", convertLanguage(targetLanguage));
			String ret = HttpRequest.httpGet(this.translate_url+"?"+HttpRequest.requestParam(paramsMap),"");
			String result = extract(ret);
			updateTranslated(text, targetLanguage, result);
			if(result.equals("")&&!text.equals("")){
				throw new TranslateFailedException(String.format("Translate failed:\n \ttest=%s, \n \ttargetLanguage=%s, \n \tsourceLanguage=%s",text,targetLanguage,sourceLanguage));
			}
			return result;
		}catch(IOException e){
			throw new TranslateException("translate error:", e);
		}
	}

	@Override
	public String translate(String text, String targetLanguage) throws TranslateException, TranslateFailedException{
		return translate(text, targetLanguage, "");
	}

}
