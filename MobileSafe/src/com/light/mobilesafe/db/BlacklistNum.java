package com.light.mobilesafe.db;

public class BlacklistNum {
	private String num;
	private String name;
	private String mode;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public BlacklistNum(String num, String name, String mode) {
		super();
		this.num = num;
		this.name = name;
		this.mode = mode;
	}

	public BlacklistNum() {
		super();
	}

}
