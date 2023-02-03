package com.utils.worddfa;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 字状态
 * 
 * @author jedi
 *
 */
public class WordDFAState {
	/**
	 * 后续的字状态
	 */
	protected ConcurrentHashMap<Character, WordDFAState> next = null;
	/**
	 * 是否已结束一个关键词
	 */
	private boolean isEnd = false;

	/**
	 * 设置当前字为关键词的结束位置
	 * 
	 * @param isEnd
	 */
	public void setIsEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	/**
	 * 检测是否结束
	 * 
	 * @return
	 */
	public boolean getIsEnd() {
		return this.isEnd;
	}

	/**
	 * 获取当前位置是不是结束位置
	 * 
	 * @param txt   文本
	 * @param begin 开始位置(包含)
	 * @param end   结束位置(不包含)
	 * @return
	 */
	public boolean checkIsEnd(CharSequence txt, int begin, int end, Object... endParams) {
		return getIsEnd();
	}

	/**
	 * 增加一个字状态
	 * 
	 * @param w
	 * @param next
	 */
	public void addNextDFAWord(char w, WordDFAState next) {
		if (this.next == null) {
			this.next = new ConcurrentHashMap<>();
		}
		this.next.putIfAbsent(w, next);
	}

	/**
	 * 获取后续字状态, 如果没有的话返回null
	 * 
	 * @param w
	 * @return
	 */
	public WordDFAState getNextDFAWord(char w) {
		if (this.next == null) {
			return null;
		}
		return this.next.get(w);
	}
}
