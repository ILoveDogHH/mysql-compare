package com.utils.worddfa;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jedi
 *
 * @param <V>
 * @param <S>
 */
public class DFAMachine<V, S> {
	/** 匹配规则 */
	public static enum MatchType {
		/**
		 * 最小匹配 匹配到任意文字立即返回 </br>
		 * 比如关键词为ab和abc; 那abcd匹配的将是ab
		 */
		MIN,
		/**
		 * 最长匹配 匹配到最长的文字才返回</br>
		 * 比如关键词为ab和abc; 那abcd匹配的将是abc
		 */
		MAX;
	}

	public final DFASeqSplitor<V, S> splitor; // 字符串分割器

	protected final DFAStateFactory<V> factory; // 状态工厂

	protected final DFAState<V> topState; // 最上层的状态检测器

	public DFAMachine(DFASeqSplitor<V, S> splitor, DFAStateFactory<V> factory) {
		this.factory = factory;
		this.splitor = splitor;
		topState = factory.createTopWordDFAState();
	}

	public DFAMachine(DFASeqSplitor<V, S> splitor, DFAStateFactory<V> factory, DFAState<V> stateMachine) {
		this.factory = factory;
		this.splitor = splitor;
		this.topState = stateMachine;
	}

	/**
	 * 这个状态是不是该忽略
	 * 
	 * @param a
	 * @return
	 */
	protected boolean isIgnore(V value) {
		return false;
	}

	/**
	 * 添加一个完整状态
	 * 
	 * @param seq
	 * @param params
	 */
	public void addSequence(S seq, Object... params) {
		DFAState<V> last = this.topState;
		List<V> values = splitor.splitOnAddSeq(seq);
		int size = values.size();
		for (int i = 0; i < size; i++) {
			V nextValue = values.get(i);
			DFAState<V> next = last.getNextDFAState(nextValue);
			if (next == null) {
				next = factory.createMiddleWordDFAState(params);
			}
			if (i == size - 1) {
				next.setIsEnd(true);
			}
			last.addNextDFAState(nextValue, next);
			last = next;
		}
	}

	public class MatchInfo {
		/**
		 * 字符串起始位置(value的位置)
		 */
		public final int begin;
		/**
		 * 匹配的字符串总长度(value的位置)
		 */
		public int len;
		/**
		 * 中间有哪些字段是忽略的(相对于begin的value位置)
		 */
		public final List<Integer> ignores = new ArrayList<>();
		/**
		 * 最后匹配到的state
		 */
		public DFAState<V> matchState;

		public MatchInfo(int begin, int len) {
			this.begin = begin;
			this.len = len;
		}
	}

	/**
	 * 检查一个字符串从begin位置起开始是否有word符合， 如果有符合的word值，返回值为匹配word的长度，否则返回零
	 * 
	 * @param txt       字符串
	 * @param begin     开始位置
	 * @param matchType 匹配类型
	 * @return
	 */
	public MatchInfo checkFromBegin(List<V> values, int begin, MatchType matchType, Object... endParams) {
		MatchInfo matchInfo = new MatchInfo(begin, 0);
		DFAState<V> nowState = null;
		nowState = topState;
		int l = values.size();
		V value;
		for (int i = begin; i < l; i++) {
			value = values.get(i);
			if (isIgnore(value)) {
				matchInfo.ignores.add(i - begin);
				continue;
			}
			DFAState<V> next = nowState.getNextDFAState(value);
			if (next == null) {
				return matchInfo;
			}
			nowState = next;
			if (next.checkIsEnd(values, begin, i + 1, endParams)) {
				matchInfo.len = i - begin + 1;
				matchInfo.matchState = nowState;
				// 最短匹配 找到一个完全匹配就返回
				if (matchType == MatchType.MIN) {
					return matchInfo;
				}
			}
		}
		// 最长匹配全部找完才返回
		return matchInfo;
	}

	/**
	 * 检查一个字符串从初始位置起开始是否有word符合，如果有符合的word, 则返回全部的匹配信息
	 * 
	 * @param txt       字符串
	 * @param matchType 匹配类型, 见{@link MatchType#MAX} 和 {@link MatchType#MIN}
	 * @return {[开始字符位置1, 匹配长度1], [开始位置2, 匹配长度2], ...};
	 */
	public List<MatchInfo> matchWord(S seq, MatchType matchType, Object... endParams) {
		List<MatchInfo> infos = new ArrayList<>();
		List<V> values = splitor.splitOnMatch(seq);
		for (int begin = 0; begin < values.size();) {
			MatchInfo matchInfo = checkFromBegin(values, begin, matchType, endParams);
			if (matchInfo.len > 0) {
				infos.add(matchInfo);
				begin += matchInfo.len;
			} else {
				begin++;
			}
		}
		return infos;
	}

	/**
	 * 检测是否有任意匹配, 性能略优于{@link #matchWord(CharSequence)}
	 * 
	 * @param txt 字符
	 * @return
	 */
	public boolean isMatch(S seq, Object... endParams) {
		List<V> values = splitor.splitOnMatch(seq);
		for (int begin = 0; begin < values.size(); begin++) {
			MatchInfo matchInfo = checkFromBegin(values, begin, MatchType.MIN, endParams);
			if (matchInfo.len > 0) {
				return true;
			}
		}
		return false;
	}
}
