package com.utils;

public enum ServerConst {
	GAME_SERVER(1),
	GATE_SERVER(2),
	CHAT_SERVER(3);
	private int type;
	ServerConst(int type)
	{
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
