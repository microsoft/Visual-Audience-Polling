package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.util.Log;

public class ReadRoster {
	private static final String TAG = null;
	BufferedReader reader;
	private int elements;
	private String delim;
	public String filename;
	public ArrayList<String> wordList;
	public ArrayList<String> settingsList;
	public ArrayList<Integer> intList;
	private int count;
	private static ArrayList<String> allIds;
	private ArrayList<String> pres = TakeCameraAttendance.getPresentIds();
	private ArrayList<String> absent = TakeCameraAttendance.getAbsentIds();
	private ArrayList<String> extra = TakeCameraAttendance.getExtraIds();

	public ReadRoster(BufferedReader reader, int elements, String delim) {
		this.reader = reader;
		this.elements = elements;
		this.delim = delim;
	}

	public ArrayList<MyCards> getCards() throws IOException {
		BufferedReader br = reader;
		ArrayList<String> tempIds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		int i = 0;
		MyCards newCard = new MyCards();
		ArrayList<MyCards> rosterCards = new ArrayList<MyCards>();
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();

			if (i % 2 == 0) {
				newCard.setName(temp);
			} else {
				newCard.setId(temp);
				tempIds.add(temp);
				Log.d(TAG, "It worked!");
			}
			if (i % 2 == 1) {
				rosterCards.add(newCard);
				newCard = new MyCards();
			}

			i++;
		}
		count = rosterCards.size();
		allIds = tempIds;
		return rosterCards;

	}

	public ArrayList<QSetCards> getQuestionCards() throws IOException {
		BufferedReader br = reader;
		ArrayList<String> tempIds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			// the string "@#$&" identifies the "\n" character signaling a new
			// line
			sb.append(",@#$&,");
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		int i = 0;
		QSetCards newCard = new QSetCards();
		String[] arr = new String[elements+1];
		ArrayList<QSetCards> quesCards = new ArrayList<QSetCards>();
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();
			if (!temp.equals("@#$&")) {
				arr[i] = temp;
				i++;
			} else {
				i=0;
				int j = 0;
				while (j<elements+1 && arr[j] != null) {
					switch (j) {
					case 0:
						newCard.setQuestion(arr[j]);
						break;
					case 1:
						newCard.setA(arr[j]);
						break;
					case 2:
						newCard.setB(arr[j]);
						break;
					case 3:
						newCard.setC(arr[j]);
						break;
					case 4:
						newCard.setD(arr[j]);
						break;
					case 5:
						newCard.setCorrect(arr[j]);
					}
					j++;
				}
				
				quesCards.add(newCard);
				newCard = new QSetCards();
				for(int k=0;k<elements+1;k++){
					arr[k] = "_";
				}
			}
		}
		count = quesCards.size();
		allIds = tempIds;
		return quesCards;

	}

	public int getCardCount() {
		return count;
	}

	public static ArrayList<String> getIds() {
		return allIds;
	}

	public ArrayList<MyCards> attendance() throws IOException {
		BufferedReader br = reader;
		ArrayList<String> tempIds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(delim);
		}

		String st = sb.toString();
		StringTokenizer tokStr = new StringTokenizer(st, delim);
		int i = 0;
		MyCards newCard = new MyCards();
		ArrayList<MyCards> rosterCards = new ArrayList<MyCards>();
		while (tokStr.hasMoreTokens()) {
			String temp = tokStr.nextToken();

			if (i % 2 == 0) {
				newCard.setName(temp);
			} else {
				newCard.setId(temp);
				if (absent.contains(temp)) {
					newCard.setPresent(false);
				} else {
					newCard.setPresent(true);
				}
				tempIds.add(temp);

				Log.d(TAG, "It worked!");
			}
			if (i % 2 == 1) {
				rosterCards.add(newCard);
				newCard = new MyCards();
			}

			i++;
		}
		count = rosterCards.size();
		allIds = tempIds;
		return rosterCards;

	}

}
