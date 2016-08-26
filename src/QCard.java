package com.abhinav.qcards;

import java.util.ArrayList;

import org.opencv.core.Point;


public class QCard {

	private String answer;
	private String cardId;
	ArrayList<Point> centerList;
	
	public QCard(){
		
	}
	
	public ArrayList<Point> getOptionCenters(){
		return centerList;
	}
	
	public String getAnswer(){
		return answer;
	}
	
	public String getId(){
		return cardId;
	}
	
	public void setOptionCenters(ArrayList<Point> centerList){
		this.centerList = centerList;
	}
	
	public void setId(String id){
		cardId = id;
	}
	
	public void setAnswer(int option){
		switch(option){
		case 0:
			answer = "A";
			break;
		case 1:
			answer = "B";
			break;
		case 2:
			answer = "C";
			break;
		case 3:
			answer = "D";
			break;
		default:
			answer = "No response";
			break;
		}	
	}
}
