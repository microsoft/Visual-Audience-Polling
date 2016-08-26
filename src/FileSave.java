package com.abhinav.qcards;

import java.io.FileOutputStream;

import android.app.Activity;

public class FileSave extends Activity{
	
	public void saveIt(){
		String filename = "QCards/myfile.csv";
		String string = "Hello world!";
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename,MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}

}
