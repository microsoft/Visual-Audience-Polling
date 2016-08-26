package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FileOperations {

	public static ArrayList<String> getPresentList(Context appContext)
			throws IOException {
		ArrayList<String> presentList = new ArrayList<String>();
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {
			Toast.makeText(appContext, "SD Card not detected",
					Toast.LENGTH_LONG);

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/TempAttendanceResults");
			if (appDirectory.exists()) {
				String filePath = appDirectory + "/pList.csv";

				File rosterFile = new File(filePath);
				FileInputStream fis = new FileInputStream(rosterFile);
				bufRead = new BufferedReader(new InputStreamReader(fis));
				String line = null;
				while ((line = bufRead.readLine()) != null) {
					presentList.add(line);
				}
				bufRead.close();
			} else {
				Toast.makeText(
						appContext,
						"No saved attendance results found for this session. Consider conducting an attendance again",
						Toast.LENGTH_LONG);
			}
		}
		return presentList;
	}

	public static void saveNameId(List<MyCards> cardList) throws IOException {
		File fileDirectory1 = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempAttendanceResults");
		if (!fileDirectory1.exists()) {
			if (fileDirectory1.mkdirs()) {

			}
		}

		File filePath1 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "pIdList.csv");
		// String copyName = getCopyName(fileDirectory, fName);
		File filePath2 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "pNameList.csv");

		StringBuilder idb = new StringBuilder();
		StringBuilder nameb = new StringBuilder();
		for (int i = 0; i < cardList.size(); i++) {
			if (cardList.get(i).isPresent()) {
				idb.append(cardList.get(i).getId());
				idb.append("\n");
				nameb.append(cardList.get(i).getName());
				nameb.append("\n");
			}
		}

		saveTempPresentFile(idb.toString(), "pIdList.csv");
		saveTempPresentFile(nameb.toString(), "pNameList.csv");
	}

	private static void saveTempPresentFile(String entries, String fileName)
			throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;

		// get path to QCards directory on External Storage
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults");
		if (!appDirectory.exists()) {
			if (appDirectory.mkdirs()) {
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(entries.getBytes());
				fos.close();
			}
		} else {
			File saveIt = new File(appDirectory, fileName);
			fos = new FileOutputStream(saveIt);
			fos.write(entries.getBytes());
			fos.close();
		}
	}

	public static void saveAttendanceForQuiz(List<MyCards> entries, String fileName)
			throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {
			for (int i = 0; i < entries.size(); i++) {
				MyCards entry = entries.get(i);
				putInFileBuilder.append(entry.getName());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.getId());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.isPresent().toString());
				putInFileBuilder.append("\n");
			}

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Attendance");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					File saveIt = new File(appDirectory, fileName);
					fos = new FileOutputStream(saveIt);
					fos.write(putInFileBuilder.toString().getBytes());
					fos.close();
				}
			} else {
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(putInFileBuilder.toString().getBytes());
				fos.close();
			}
		} else {
			// SD Card unavailable warning
		}
	}

	public static boolean ifAttendanceFileExists(String file_name, View v) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {
			Dialog dial = new Dialog(v.getContext());
			dial.setContentView(R.layout.sd_warning);
			dial.setTitle("Warning!");
			TextView warn = (TextView) dial.findViewById(R.id.warning);
			warn.setText("SD Card not found. Please check that Memory card is inserted properly.");
			warn.setTextColor(Color.WHITE);
			warn.setBackgroundColor(Color.RED);
			CardView warnCard = (CardView) dial.findViewById(R.id.card_view);
			warnCard.setBackgroundColor(Color.RED);
			dial.show();
		}
		File appDirectory;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Attendance");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static Hashtable<String, String> getPresentNameId() throws IOException {

		Hashtable<String, String> pNameId = new Hashtable<String, String>();
		ArrayList<String> pIdList = new ArrayList<String>();
		ArrayList<String> pNameList = new ArrayList<String>();
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/TempAttendanceResults");
			String filePath1 = appDirectory + "/pIdList.csv";
			String filePath2 = appDirectory + "/pNameList.csv";

			File idFile = new File(filePath1);
			FileInputStream fis = new FileInputStream(idFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = bufRead.readLine()) != null) {
				pIdList.add(line);
			}
			bufRead.close();

			File nameFile = new File(filePath2);
			fis = new FileInputStream(nameFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			line = null;
			while ((line = bufRead.readLine()) != null) {
				pNameList.add(line);
			}
			bufRead.close();

			for (int i = 0; i < pNameList.size(); i++) {
				pNameId.put(pIdList.get(i), pNameList.get(i));
			}
		}
		return pNameId;
	}
	
	public static void saveNamedRecords(Hashtable<String,ArrayList<String>> namedRecords, KeyValues keyVal){
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = namedRecords.keySet();
		Iterator<String> it  = keySet.iterator();
		while(it.hasNext()){
			String sName = it.next();
			sb.append(sName);
			ArrayList<String> answers = namedRecords.get(sName);
			Iterator<String> ansIt = answers.iterator();
			while(ansIt.hasNext()){
				String ans = ansIt.next();
				sb.append(",");
				sb.append(ans);
			}
			sb.append("\n");
		}
		String studentStats = sb.toString();
		try {
			saveNewPollFile(studentStats,getPollName(keyVal));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void saveNewPollFile(String studentData,
			String fileName) throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File studentDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

		
			studentDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/StudentStats");
			if (!studentDirectory.exists()) {
				if (studentDirectory.mkdirs()) {
				}
			} 
				File saveIt = new File(studentDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(studentData.getBytes());
			
		} else {
			//SD Card unavailable
		}
	}
	
	public static String getPollName(KeyValues keyVal) throws IOException {
		String qn1 = removeExt(keyVal.getQSet());

		String qn2 = null;
		if (keyVal.getRosterQuizBool()) {
			qn2 = keyVal.getAttendanceRoster();
		} else {
			qn2 = "null";
		}

		Calendar c = Calendar.getInstance();
		Integer daySuf = c.get(Calendar.DAY_OF_MONTH);
		Integer monthSuf = c.get(Calendar.MONTH);
		Integer yearSuf = c.get(Calendar.YEAR);
		StringBuilder fNameBuilder = new StringBuilder();
		fNameBuilder.append(qn1);
		fNameBuilder.append("_");

		fNameBuilder.append(qn2);
		fNameBuilder.append("_");
		fNameBuilder.append(daySuf);
		fNameBuilder.append("_");
		fNameBuilder.append(monthSuf);
		fNameBuilder.append("_");
		fNameBuilder.append(yearSuf);
		String pollFileName = null;

		String quizName = fNameBuilder.toString();
		File fileDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/PollResults/");
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/PollResults/" + quizName + ".csv");
		// String copyName = getCopyName(fileDirectory, fName);
		if (!filePath.exists()) {
			pollFileName = quizName;
		} else {
			pollFileName = getCopyName(fileDirectory, quizName);
		}

		return pollFileName;
	}

	public static String getCopyName(File fileDirectory, String fName) {

		String fileList[] = fileDirectory.list();
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].endsWith(".csv")) {
				String extStripped = RosterList.removeExt(fileList[i]);
				files.add(extStripped);
			}
		}
		int j = 1;
		for (int k = 1; k < files.size(); k++) {
			if (files.contains(fName + "_" + k)) {
				j++;
			} else {
				break;
			}
		}

		String copyName = fName + "_" + j;

		return copyName;

	}

	// This method returns the filename after dropping its extension(works only for csv files).
		public static String removeExt(String str) {
			return str.substring(0, str.length() - 4);
		}


}
