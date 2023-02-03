package com.utils.translate;

public interface ITranslate {
	/**
	 * 翻译语言
	 * @param text 需要翻译的文字
	 * @param targetLanguage 目标语言标识
	 * @param sourceLanguage 需要翻译的文字的语言标识
	 * @return 翻译成功返回翻译后的字符串 失败抛出异常
	 * @throws TranslateException 翻译异常
	 * @throws TranslateFailedException 翻译失败
	 */
	String translate(String text, String targetLanguage, String sourceLanguage) throws TranslateException, TranslateFailedException;
	
	/**
	 * 翻译语言
	 * @param text 需要翻译的文字
	 * @param targetLanguage 目标语言标识
	 * @return 翻译成功返回翻译后的字符串 失败抛出异常
	 * @throws TranslateException 翻译异常
	 * @throws TranslateFailedException 翻译失败
	 */
	String translate(String text, String targetLanguage) throws TranslateException, TranslateFailedException;
}
