package com.utils.worddfa;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态
 * 
 * @author jedi
 *
 * @param <V> 表示值的类型
 * @param <S> 表示值的序列
 */
public class DFAState<V> {
	/**
	 * 当前状态下后续状态
	 */
	protected ConcurrentHashMap<V, DFAState<V>> next = null;
	/**
	 * 这是个完整的状态
	 */
	protected boolean isEnd = false;

	/**
	 * 设置当前状态为一个结束位置
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
	 * @param seq   状态序列
	 * @param begin 开始位置(包含)
	 * @param end   结束位置(不包含)
	 * @return
	 */
	public boolean checkIsEnd(List<V> seq, int begin, int end, Object... endParams) {
		return getIsEnd();
	}

	/**
	 * 给当前状态增加一个后续状态
	 * 
	 * @param value
	 * @param next
	 */
	public void addNextDFAState(V value, DFAState<V> next) {
		if (this.next == null) {
			this.next = new ConcurrentHashMap<>();
		}
		this.next.putIfAbsent(value, next);
	}

	/**
	 * 获取后续状态, 如果没有的话返回null
	 * 
	 * @param value
	 * @return
	 */
	public DFAState<V> getNextDFAState(V value) {
		if (this.next == null) {
			return null;
		}
		return this.next.get(value);
	}
}