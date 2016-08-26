package com.abhinav.qcards;

public class RosterEntryCards {
	private String sName;
	private String sId;
	public String getName() {
		return this.sName;
	}

	public RosterEntryCards(String sName, String sId) {
		this.sName = sName;
		this.sId = sId;
	}

	public RosterEntryCards() {
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
	
	
}
