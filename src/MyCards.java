package com.abhinav.qcards;

public class MyCards {
	private String sName;
	private String sId;
	private Boolean sPresent = true;
	private int position;
	public String getName() {
		return this.sName;
	}

	public MyCards(String sName, String sId, boolean sPresent) {
		this.sName = sName;
		this.sId = sId;
		this.sPresent = sPresent;
	}

	public MyCards() {
		// TODO Auto-generated constructor stub
	}

	public void setName(String t) {
		this.sName = t;
	}

	public String getId() {
		return sId;
	}

	public void setId(String id) {
		this.sId = id;
	}

	public Boolean isPresent() {
		return sPresent;
	}

	public void setPresent(Boolean present) {
		this.sPresent = present;
	}
	
	
}
