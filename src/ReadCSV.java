package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReadCSV {
	BufferedReader reader;
	private int elements;
	private String delim;
	private int cardCount = 0;
	private int fileContentCount = 0;
	public ArrayList<String> wordList;
	public ArrayList<String> settingsList;
	public ArrayList<Integer> intList;

	public ReadCSV(BufferedReader reader, int elements, String delim) {
		this.reader = reader;
		this.elements = elements;
		this.delim = delim;
	}

	public String readLines() throws IOException {
		// ArrayList<ArrayList<String>> lines = new
		// ArrayList<ArrayList<String>>();
		if(reader!=null){
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
			//fileContentCount++;
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		StringTokenizer tok2 = tokStr;
		String tok;
		ArrayList<String> stElem;
		int i = 0;
		ArrayList<String> stElem1 = new ArrayList<String>();
		int count = 0;
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();
			/*
			 * stElem.add(temp); if(i == elements-1){ lines.add(stElem);
			 * count+=i; i = -1;
			 * 
			 * 
			 * }
			 */
			stElem1.add(temp);
			i++;
		}
		wordList = stElem1;
		return displayString(stElem1);
		}else{
			return null;
		}

	}
	
	public int contentCount() throws IOException{
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
			fileContentCount++;
		}
		return fileContentCount;
	}

	public int getCount() {
		return cardCount;
	}

	private String displayString(ArrayList<String> abc) {
		if(!abc.isEmpty()){
		String stw = abc.get(0);
stw+=",";
		for (int i = 2; i < abc.size() + 1; i++) {
			stw += abc.get(i - 1);
			
			if (i % elements == 0) {
				stw += "\n";
				cardCount++;
			}else{
				stw+=",";
			}
		}
		
		return stw;
		}else{
			return null;
		}
	}

	public ArrayList<String> getContentString() {

		return wordList;
	}

	public ArrayList<Integer> getContentInt() throws IOException {
		// ArrayList<ArrayList<String>> lines = new
		// ArrayList<ArrayList<String>>();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		StringTokenizer tok2 = tokStr;
		String tok;
		ArrayList<String> stElem;
		int i = 0;
		ArrayList<String> stElem1 = new ArrayList<String>();
		int count = 0;
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();
			stElem1.add(temp);
			i++;
		}
		wordList = stElem1;
		intList = new ArrayList<Integer>();
		String stw = wordList.get(0);
		int el = Integer.parseInt(stw);
		intList.add(el);
		for (int j = 2; j < wordList.size() + 1; j++) {
			intList.add(Integer.parseInt(wordList.get(j - 1)));
			if (i % elements == 0) {
				intList.add(10000);
				// cardCount++;
			}
		}
		return intList;

	}

	public void getSettings() throws IOException {
		// ArrayList<ArrayList<String>> lines = new
		// ArrayList<ArrayList<String>>();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		StringTokenizer tok2 = tokStr;
		String tok;
		ArrayList<String> stElem;
		int i = 0;
		ArrayList<String> stElem1 = new ArrayList<String>();
		int count = 0;
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();
			stElem1.add(temp);
			i++;
		}
		settingsList = stElem1;

	}
	
	public ArrayList<Integer> getId(int index) throws IOException{
		ArrayList<Integer> tempInt = getContentInt();
		ArrayList<Integer> rosterIds = null;
		for(int i=0;i<tempInt.size();i++){
			if((i+1)%elements == 0){
				rosterIds.add(tempInt.get(i));
			}
		}
		return rosterIds;
	}
}
