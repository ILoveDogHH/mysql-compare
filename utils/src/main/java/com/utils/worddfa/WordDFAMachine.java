package com.utils.worddfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 关键字检查器
 * 
 * @author jedi
 *
 */
public class WordDFAMachine extends DFAMachine<Character, String> {
	/**
	 * 忽略的字符
	 */
	private Collection<Character> ignoreChars = new ArrayList<>();
	/**
	 * 忽略的字符长度
	 */
	private Collection<char[]> ignoreCharRanges = new ArrayList<>();

	public WordDFAMachine() {
		super(new DefaultWordDFASeqSplitor(), new DefaultWordDFAStateFactory());
	}

	public WordDFAMachine(DFASeqSplitor<Character, String> splitor) {
		super(splitor, new DefaultWordDFAStateFactory());
	}

	public WordDFAMachine(DFAStateFactory<Character> factory) {
		super(new DefaultWordDFASeqSplitor(), factory);
	}

	public WordDFAMachine(DFASeqSplitor<Character, String> splitor, DFAStateFactory<Character> factory) {
		super(splitor, factory);
	}

	public WordDFAMachine(DFASeqSplitor<Character, String> splitor, DFAStateFactory<Character> factory,
			DFAState<Character> state) {
		super(splitor, factory, state);
	}

	/**
	 * 默认的State工厂
	 * 
	 * @author jedi
	 *
	 */
	private static class DefaultWordDFAStateFactory implements DFAStateFactory<Character> {
		@Override
		public DFAState<Character> createTopWordDFAState() {
			return createMiddleWordDFAState();
		}
		@Override
		public DFAState<Character> createMiddleWordDFAState(Object... params) {
			return new DFAState<Character>();
		}
	}

	private static class DefaultWordDFASeqSplitor implements DFASeqSplitor<Character, String> {
		@Override
		public List<Character> splitOnAddSeq(String seq) {
			List<Character> values = new ArrayList<>();
			char[] chars = seq.toCharArray();
			for (char ch : chars) {
				values.add(ch);
			}
			return values;
		}

		@Override
		public List<Character> splitOnMatch(String seq) {
			return splitOnAddSeq(seq);
		}
	}

	/**
	 * 忽略的字符
	 * 
	 * @param chars
	 */
	public void setIgnoreChars(Collection<Character> chars) {
		this.ignoreChars = chars;
	}

	/**
	 * 忽略的字符范围
	 * 
	 * @param ranges
	 */
	public void setIgnoreCharRanges(Collection<char[]> ranges) {
		this.ignoreCharRanges = ranges;
	}

	/**
	 * 这个字符是不是该忽略
	 * 
	 * @param a
	 * @return
	 */
	@Override
	protected boolean isIgnore(Character a) {
		if (ignoreChars.contains(a)) {
			return true;
		}
		for (char[] reg : ignoreCharRanges) {
			if (a <= reg[1] && a >= reg[0]) {
				return true;
			}
		}
		return false;
	}
}
