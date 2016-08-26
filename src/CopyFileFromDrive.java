package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class CopyFileFromDrive extends ActionBarActivity {

	private static final String TAG = null;
	final int READ_REQUEST_CODE = 1;
	private static String parent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		parent = intent.getStringExtra(QSetList.EXTRA_MESSAGE_PARENT);
		// AttendanceResults.attendance_results = false;
		// setContentView(R.layout.roster_recycle);
		performFileSearch();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qcards, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	public void performFileSearch() {

		// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
		// file
		// browser.

	//	Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

		// Filter to only show results that can be "opened", such as a
		// file (as opposed to a list of contacts or timezones)
	//	intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only images, using the image MIME data type.
		// If one wanted to search for ogg vorbis files, the type would be
		// "audio/ogg".
		// To search for all documents available via installed storage
		// providers,
		// it would be "*/*".

		// Mime Types : https://developers.google.com/drive/web/mime-types
	//	intent.setType("*/*");
		// Intent resultData = intent;
	//	startActivityForResult(intent, READ_REQUEST_CODE);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent resultData) {

		// super.onActivityResult(requestCode, resultCode, resultData);

		if (requestCode == READ_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {

			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();

				String fName = fileNameFromUri(uri);
				try {
				
					String msg = readTextFromUri(uri);
					copyToLocalFile(msg, fName);/*
												 * TextView textView = new
												 * TextView(this);
												 * textView.setText(msg);
												 * setContentView(textView);
												 */
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "Uri: " + uri.toString());

			}

		}
		//Intent intent = new Intent(this, RosterList.class);
		/*if (QSet.isParentQSet()) {
			final String qSetNameGlobal = QSetAdapter.qSetNameGlobal;
			intent = new Intent(this, QSet.class);
			intent.putExtra(qSetNameGlobal, QSet.currentQSet());
		}else{*/
			
		/*}*/
	//	startActivity(intent);

	}

	private String readTextFromUri(Uri uri) throws IOException {
		InputStream inputStream = getContentResolver().openInputStream(uri);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		int elements = 2;
		ReadCSV read1 = new ReadCSV(reader, elements, ",");

		return read1.readLines();
	}

	public static String fileNameFromUri(Uri uri) {
		String lastSegment = uri.getLastPathSegment();
		/*
		 * getLastPathSegment() returns a String of the format
		 * 2A5C-5322:Documents/Roster1.csv So we need to parse the string and
		 * store the substring after '/' as the file name
		 */

		String[] segments = lastSegment.split("/");
		String fName = segments[1];
		return fName;

	}

	public static boolean copyToLocalFile(String fileContent, String file_name)
			throws IOException {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards");
			if(parent.equals("RosterList")){
				appDirectory = new File(Environment.getExternalStorageDirectory()
						+ "/QCards/Rosters");
			}
			if (!appDirectory.exists()) {
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				if (!Roster.ifFileExists(file_name, parent)) {
					File saveIt = new File(appDirectory, file_name);
					fos = new FileOutputStream(saveIt);
					fos.write(fileContent.getBytes());
					fos.close();
				} else {
					/*
					 * if file exists then rename the new file by adding current
					 * system time
					 */
					File saveIt = new File(appDirectory, renameFile(file_name));
					fos = new FileOutputStream(saveIt);
					fos.write(fileContent.getBytes());
					fos.close();
				}
				return true;
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}
		return false;
	}

	public static String renameFile(String file_name) {
		String name = RosterList.removeExt(file_name);
		Long systemTime = System.nanoTime();
		return (name + systemTime.toString() + ".csv");
	}

}
