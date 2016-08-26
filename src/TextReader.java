package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextReader {
	private int count;
	BufferedReader buff;
	public TextReader(BufferedReader buff){
		this.buff = buff;
		
	}

	public int getQuestionCount(){
		BufferedReader br = buff;
		try {int tempCount = 0;
			while((br.readLine())!=null){
				if(tempCount<6){
					tempCount++;
				}
				if(tempCount == 6){
					count++;
					tempCount = 0;
				}
			}
		//	br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			count = 0;
			e.printStackTrace();
		}
		return count;
	}
	
	public ArrayList<QSetCards> getQuestionCards() throws IOException{
		//String str="temp";
		BufferedReader br = buff;
		QSetCards newCard = new QSetCards();
		ArrayList<QSetCards> quesCards = new ArrayList<QSetCards>();
		
			int tempCount = 0;
			String line;
			while ((line = br.readLine()) != null){
				switch(tempCount){
				case 0:
					newCard.setQuestion(line);
					tempCount++;
					break;
				case 1:
					newCard.setA(line);
					tempCount++;
					break;
				case 2:
					newCard.setB(line);
					tempCount++;
					break;
				case 3:
					newCard.setC(line);
					tempCount++;
					break;
				case 4:
					newCard.setD(line);
					tempCount++;
					break;
				case 5:
					newCard.setCorrect(line);
					tempCount=0;
					quesCards.add(newCard);
					newCard = new QSetCards();
					break;
				
				}
			}
		//br.close();
		return quesCards;
	}
}
