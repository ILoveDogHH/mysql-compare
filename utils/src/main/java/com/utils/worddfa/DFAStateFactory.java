package com.utils.worddfa;

public interface DFAStateFactory<V> {
	public DFAState<V> createTopWordDFAState();

	public DFAState<V> createMiddleWordDFAState(Object... params);
}
