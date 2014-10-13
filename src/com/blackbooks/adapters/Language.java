package com.blackbooks.adapters;

public class Language {

	private String code;

	private String displayName;

	public Language(String code, String displayName) {
		this.code = code;
		this.displayName = displayName;
	}
	
	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}
}
