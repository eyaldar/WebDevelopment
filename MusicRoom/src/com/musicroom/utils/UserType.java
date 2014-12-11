package com.musicroom.utils;

public enum UserType {
	STUDIO(1), BAND(2);
	private final int code;
	
	private UserType(int code) {
		this.code = code;
	}
	
	public int toInt() {
		return code;
	}
	
	public String toString() {
		return this.name();
	}
}