package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class IDMap{
Context cont;

public IDMap(Context context){
	this.cont = context;
}
	public void populateIdMap() throws IOException{
		StringBuilder sb = new StringBuilder();
		AssetManager am = cont.getAssets();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(cont.getAssets().open("idmap.csv")));
			String inputLine;
			while ((inputLine = reader.readLine()) != null){
				sb.append(inputLine);
				sb.append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/IDMAP");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {

				}
			}
			if(appDirectory.list().length==0){
				File saveIt = new File(appDirectory, "idmap.csv");
				fos = new FileOutputStream(saveIt);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			}
		}
	}

	public void populateSampleQSet() throws IOException{
		StringBuilder sb = new StringBuilder();
		AssetManager am = cont.getAssets();
		BufferedReader reader = null;
		ArrayList<String> readerList = new ArrayList<>();
		try {
			reader = new BufferedReader(
					new InputStreamReader(cont.getAssets().open("sample_questionnaire.txt")));
			String inputLine;
			while ((inputLine = reader.readLine()) != null){
				readerList.add(inputLine);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/QuestionSet");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {

				}
			}
			if(appDirectory.list().length==0){
				Iterator<String> iterator = readerList.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					if (i>0) {
						sb.append("\n");
						sb.append(iterator.next());
						i++;
					} else {
						sb.append(iterator.next());
						i++;
					}
				}
				File saveIt = new File(appDirectory, "sample_questionnaire.txt");
				fos = new FileOutputStream(saveIt);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			}
		}
	}

	public void populateSampleRosters() throws IOException{
		StringBuilder sb = new StringBuilder();
		AssetManager am = cont.getAssets();
		BufferedReader reader = null;
		ArrayList<String> readerList = new ArrayList<>();
		try {
			reader = new BufferedReader(
					new InputStreamReader(cont.getAssets().open("sample_roster.csv")));
			String inputLine;
			while ((inputLine = reader.readLine()) != null){
				readerList.add(inputLine);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {

				}
			}
			if(appDirectory.list().length==0){
				Iterator<String> iterator = readerList.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					if (i>0) {
						sb.append("\n");
						sb.append(iterator.next());
						i++;
					} else {
						sb.append(iterator.next());
						i++;
					}
				}
				File saveIt = new File(appDirectory, "sample_roster.csv");
				fos = new FileOutputStream(saveIt);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();
			}
		}
	}

}
