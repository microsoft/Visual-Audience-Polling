package com.abhinav.qcards;

public class RosterListCard {
	private String name;
	private String fileName;
	private String size;

	public RosterListCard(String name, Integer num) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.size = num.toString();
		//this.fileName = name+".csv";
	}

	public void setName(String fname) {
		name = fname;
	}

	public String getName() {
		return this.name;
	}

	public String getNum(){
		return this.size;
	}
	
	private void setNum(Integer num){
		size = num.toString();
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return this.fileName;
	}
}
