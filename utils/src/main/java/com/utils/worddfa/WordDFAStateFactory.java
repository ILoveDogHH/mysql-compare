package com.utils.worddfa;

public interface WordDFAStateFactory {
	public WordDFAState createTopWordDFAState();
	public WordDFAState createMiddleWordDFAState(Object... params);
}
