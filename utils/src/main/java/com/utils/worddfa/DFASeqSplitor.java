package com.utils.worddfa;

import java.util.List;

/**
 * 将Seq转为V
 * 
 * @author jedi
 *
 * @param <V>
 * @param <S>
 */
public interface DFASeqSplitor<V, S> {
	/**
	 * 将Seq转为Value列表, 添加seq时使用
	 * 
	 * @param seq
	 * @return
	 */
	public List<V> splitOnAddSeq(S seq);

	/**
	 * 将Seq转为Value列表, 匹配时使用
	 * 
	 * @param seq
	 * @return
	 */
	public List<V> splitOnMatch(S seq);
}
