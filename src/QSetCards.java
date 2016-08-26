package com.abhinav.qcards;

public class QSetCards {
	private String question = null;
	private String opA = null;
	private String opB = null;
	private String opC = null;
	private String opD = null;
	private String correct = null;
	private boolean expanded = false;

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setA(String option) {
		this.opA = option;
	}

	public void setB(String option) {
		this.opB = option;
	}

	public void setC(String option) {
		this.opC = option;
	}

	public void setD(String option) {
		this.opD = option;
	}
	
	public void setCorrect(String option){
		this.correct = option;
	}

	public String getQuestion() {
		return this.question;
	}

	public String getA() {
		return this.opA;
	}

	public String getB() {
		return this.opB;
	}

	public String getC() {
		return this.opC;
	}

	public String getD() {
		return this.opD;
	}
	
	public Integer getCorrectString(){
		Integer answer = 0;
		if(correct.toUpperCase().matches("A")){
			answer = 0;
		}else if(correct.toUpperCase().matches("B")){
			answer = 1; 
		}else if(correct.toUpperCase().matches("C")){
			answer = 2; 			
		}else if(correct.toUpperCase().matches("D")){
			answer = 3; 			
		}
		return answer;
	}
	
	public Integer getCorrect(){
		return Integer.parseInt(this.correct)-1;
	}
	
	public boolean isExpanded(){
		return expanded;
	}
	
	public void setExpanded(boolean state){
		expanded = state;
	}
}
