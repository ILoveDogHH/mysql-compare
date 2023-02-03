package com.utils.language;

import com.alibaba.fastjson.JSONObject;
import com.logger.JLogger;
import com.utils.LangFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LangManager {
	private static String defualtLang;
	private static ConcurrentHashMap<String, HashMap<String, String>> languagesMap = null;
	private static String[] languages;

	public static void init(String dir, String langs) {
		ConcurrentHashMap<String, HashMap<String, String>> map = new ConcurrentHashMap<>();
		// 删除中间的空格
		langs = langs.replace(" ", "");
		languages = langs.split(",");
		if (languages.length < 1) {
			return;
		}
		defualtLang = languages[0];
		for (String lang : languages) {
			loadLanguageFile(dir, lang, map);
		}
		languagesMap = map;
	}

	private static void loadLanguageFile(String dir, String lang,
			ConcurrentHashMap<String, HashMap<String, String>> languageMap) {
		String path = dir + lang + ".json";
		File jsonFile = new File(path);
		if (!jsonFile.isFile() || !jsonFile.exists()) {
			return;
		}
		try (FileReader fileReader = new FileReader(jsonFile);
				BufferedReader reader = new BufferedReader(fileReader);) {

			StringBuffer sb = new StringBuffer();
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
			HashMap<String, String> langs = new HashMap<>();
			JSONObject json = JSONObject.parseObject(sb.toString());
			json.forEach((key, value) -> {
				langs.put(key, value.toString());
			});
			languageMap.put(lang, langs);
		} catch (Exception e) {
			JLogger.error("initLanguageFromJson error: ", e);
			e.printStackTrace();
		}
	}

	public static String[] getLanguages() {
		return languages;
	}

	/**
	 * 只标记为待翻译语言
	 * 
	 * @param key  待翻译字符串
	 * @param args 参数
	 * @return 被标记的待翻译语言字符串
	 */
	public static String text(String key, Object... args) {
		List<Object> values = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			values.add(i, args[i]);
		}
		return LangFactory.text(key, values);
	}

	/**
	 * 检测是不是标记为待翻译的语言
	 * 
	 * @param str
	 * @return true:已标记为待翻译的语言,false:未标记为待翻译的语言
	 */
	public static boolean checkIsLangMarkText(String str) {
		LangMark lm = new LangMark(str);
		boolean isSame = lm.getKey().equals(str);
		return !isSame;
	}

	/**
	 * 将参数替换进去
	 * 
	 * @param lang 目标语言
	 * @param key  待替换字符串
	 * @param args 参数
	 * @return 替换后的字符串
	 */
	public static String translate(String lang, String key, Object... args) {
		List<String> values = new ArrayList<>();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				values.add(i, args[i].toString());
			}
		}
		key = getText(lang, key);
		for (int i = 0; i < values.size(); i++) {
			String rep = String.format("{%d}", i + 1);
			key = key.replace(rep, getText(lang, values.get(i)));
		}
		return key;
	}

	/**
	 * @param lang
	 * @param text
	 * @return
	 */
	private static String getText(String lang, String text) {
		if (null == lang) {
			lang = defualtLang;
		}
		if (languagesMap.containsKey(lang) && languagesMap.get(lang).containsKey(text)) {
			return languagesMap.get(lang).get(text);
		}
		return text;
	}


}
