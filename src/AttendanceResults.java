package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AttendanceResults extends ActionBarActivity {

	private static final String TAG = null;
	private RecyclerView rObject;
	private MyAdapter adapter;
	boolean test = true;
	public static Hashtable<String, String> presentNameId = new Hashtable<String, String>();
	private boolean attendanceAvailable = false;
	String abc = null;
	List<MyCards> cardList = null;
	private String attendanceRoster = null;
	String rosterQuizStep4 = "Step 4: View attendance results";
	KeyValues keyVal;
	TextView curRost;

	AlertDialog dialBox = null;
	Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_results);
		keyVal = new KeyValues(getApplicationContext(), "globals");
		curRost = (TextView)findViewById(R.id.currentNav);
		CardView navCard = (CardView) findViewById(R.id.show_current);
		navCard.setVisibility(View.GONE);
		rObject = (RecyclerView) findViewById(R.id.recycler_view);
		Button saveAttResult = (Button) findViewById(R.id.save_attendance_button);
		Button startQ = (Button) findViewById(R.id.startQuiz);
		if (!QSet.isParentQSet()) {
			startQ.setVisibility(View.GONE);
		}
		if (keyVal.getRosterQuizBool()) {
			startQ.setVisibility(View.VISIBLE);
			setAttendanceSharedPref();
			attendanceAvailable = getAttendanceSharedPref();
			setAttendanceRoster(keyVal.getRoster());
			navCard.setVisibility(View.VISIBLE);
			curRost.setText(rosterQuizStep4);
		} else {
			startQ.setVisibility(View.GONE);
			setAttendanceSharedPref();
			attendanceAvailable = getAttendanceSharedPref();
			setAttendanceRoster(keyVal.getRoster());
		}
		// attendanceButton.setVisibility(View.GONE);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rObject.setLayoutManager(llm);

		cardList = Roster.cardList;
		List<String> absList = TakeCameraAttendance.getAbsentIds();
		List<String> presList = TakeCameraAttendance.getPresentIds();
		if (presList != null) {
			for (int i = 0; i < cardList.size(); i++) {
				/*
				 * if(absList.contains(cardList.get(i).getId())){
				 * cardList.get(i).setPresent(false); }else
				 */if (presList.contains(cardList.get(i).getId())) {
					 presentNameId.put(cardList.get(i).getId(), cardList.get(i).getName());
					cardList.get(i).setPresent(true);
				} else {
					cardList.get(i).setPresent(false);
				}
			}
		}

		adapter = new MyAdapter(AttendanceResults.this, cardList, 1, getApplicationContext());
		rObject.setAdapter(adapter);
		int duration = Toast.LENGTH_SHORT;
		// attendance_results = true;

		/*
		 * Intent i = new Intent(this, TakeCameraAttendance.class);
		 * startActivity(i);
		 */

	}
	
	public void setAttendanceSharedPref(){
		SharedPreferences pref = getApplicationContext().getSharedPreferences("globals", 0);
		Editor editor = pref.edit();
		editor.putBoolean("attendance_bool", true);
		editor.commit();
	}
	
	public Boolean getAttendanceSharedPref(){
		SharedPreferences pref = getApplicationContext().getSharedPreferences("globals", 0);
		return pref.getBoolean("attendance_bool", false);
	}

	public void onBackPressed() {
		// attendance_results = false;

		try {
			saveNewRosterFile(cardList,getPollName()+".csv");
			currentAttendanceForQuiz(cardList);
			saveNameId(cardList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Otherwise defer to system default behavior.
		super.onBackPressed();
		Intent i = new Intent(this, JumpInMain.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qcards, menu);
		this.menu = menu;
		
		if (keyVal.getRosterBool()) {
			menu.getItem(0).setIcon(R.drawable.roster_ok_96);
		} else {
			menu.getItem(0).setIcon(R.drawable.roster_cross_96);
		}

		
		if (keyVal.getAttendanceBool()) {
			menu.getItem(1).setIcon(R.drawable.attendance_ok_96);
		} else {
			menu.getItem(1).setIcon(R.drawable.attendance_cross_96);
		}

		
		if (keyVal.getQSetBool()) {
			menu.getItem(2).setIcon(R.drawable.quest_ok_96);
		} else {
			menu.getItem(2).setIcon(R.drawable.quest_cross_96);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		// ids - roster,attendance, qset
		switch (item.getItemId()) {
		case R.id.roster :
			if (keyVal.getRosterBool()) {
				onActionRosterClick();
			} else {

			}
			break;

		case R.id.attendance :
			if (keyVal.getAttendanceBool()) {
				onActionAttendanceClick();
			} else {

			}
			break;

		case R.id.qset:
			if (keyVal.getQSetBool()) {
				onActionQuestionClick();
			} else {

			}
			break;
		}

		int id = item.getItemId();
		// item.setIcon(R.drawable.attendance_cross_96);
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	// ///////////////////////////////////////////////////////////

	public void onActionRosterClick() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				getString(R.string.action_roster_start)
						+ keyVal.getRoster()
						+ getString(R.string.action_roster_end))
				.setPositiveButton(R.string.action_keep,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialBox.dismiss();
							}
						})
				.setNegativeButton(R.string.action_remove,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								keyVal.setRoster(null);
								keyVal.setRosterBool(false);
								curRost.setText("No roster selected");
								menu.getItem(0).setIcon(
										getResources().getDrawable(
												R.drawable.roster_cross_96));
							}
						});
		dialBox = adb.create();
		dialBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialBox.show();
	}

	public void onActionAttendanceClick() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				getString(R.string.action_att_start)
						+ keyVal.getAttendanceRoster()
						+ getString(R.string.action_att_end))
				.setPositiveButton(R.string.action_keep,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialBox.dismiss();
							}
						})
				.setNegativeButton(R.string.action_remove,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								keyVal.setAttendanceBool(false);
								keyVal.setAttendanceRoster(null);
								// Roster.isRosterAvailable = false;
								menu.getItem(1)
										.setIcon(
												getResources()
														.getDrawable(
																R.drawable.attendance_cross_96));
							}
						});
		dialBox = adb.create();
		dialBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialBox.show();
	}

	public void onActionQuestionClick() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				getString(R.string.action_quest_start) + keyVal.getQSet()
						+ getString(R.string.action_quest_end))
				.setPositiveButton(R.string.action_keep,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialBox.dismiss();
							}
						})
				.setNegativeButton(R.string.action_remove,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								keyVal.setQSetBool(false);
								keyVal.setQSet(null);
								menu.getItem(2).setIcon(
										getResources().getDrawable(
												R.drawable.quest_cross_96));
							}
						});
		dialBox = adb.create();
		dialBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialBox.show();
	}
	
		

	public void saveAttendanceResults(View view) {
		saveAsNew(view, "Save Results");

	}
	
	public String removeExt(String str) {
		return str.substring(0, str.length() - 4);
	}
	
	public String getPollName() throws IOException {
		String qn1 = "att";

		String qn2 = null;
		if (keyVal.getAttendanceBool()) {
			qn2 = keyVal.getAttendanceRoster();
		} else {
			qn2 = "AWR";
		}

		Calendar c = Calendar.getInstance();
		Integer daySuf = c.get(Calendar.DAY_OF_MONTH);
		Integer monthSuf = c.get(Calendar.MONTH)+1;
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
				+ "/QCards/AttendanceResults/");
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/AttendanceResults/" + quizName + ".csv");
		// String copyName = getCopyName(fileDirectory, fName);
		if (!filePath.exists()) {
			pollFileName = quizName;
		} else {
			pollFileName = getCopyName(fileDirectory, quizName);
		}

		return pollFileName;
	}

	public String getCopyName(File fileDirectory, String fName) {

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
	
	public void currentAttendanceForQuiz(List<MyCards> cardList) throws IOException{
		
		File fileDirectory1 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults");
		if (!fileDirectory1.exists()) {
			if (fileDirectory1.mkdirs()) {
				
				Log.e(TAG, "Directory Created");
			}
		}
		
		
		File filePath1 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "pList.csv");
		// String copyName = getCopyName(fileDirectory, fName);
		File filePath2 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "aList.csv");
		
		StringBuilder pb = new StringBuilder();
		StringBuilder ab = new StringBuilder();
		for(int i=0; i<cardList.size();i++){
			if(cardList.get(i).isPresent()){
				pb.append(cardList.get(i).getId());
				pb.append("\n");
			}else{
				ab.append(cardList.get(i).getId());
				ab.append("\n");
			}
		}
		/*Set<String> pSet = presentNameId.keySet();
		Iterator<String> pIt = pSet.iterator();
		while(pIt.hasNext()){
			String key = pIt.next();
			//if(presentNameId.get(key).matches("true")){
				pb.append(key);
				pb.append("\n");
		//	}else{
				ab.append(key);
				ab.append("\n");
			//}
		}*/
		saveTempPresentFile(pb.toString(),"pList.csv");
		saveTempAbsentFile(ab.toString(),"aList.csv");
		
	}


	public void saveAsNew(View v, String title) {

		final Dialog dial = new Dialog(v.getContext());
		dial.setContentView(R.layout.save_attendance_dialog);
		dial.setTitle(title);
		Button sButton = (Button) dial.findViewById(R.id.saveAttendanceButton);
		final EditText fName = (EditText) dial.findViewById(R.id.newRosterName);
		sButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				if (!ifAttendanceFileExists(fName.getText().toString(), v)) {
					// saveModified();
					try {
						saveNewRosterFile(cardList, fName.getText().toString());
						dial.dismiss();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					saveAsNew(v, "File Name already used!");
				}
			}
		});
		dial.show();
	}

	public void saveNewRosterFile(List<MyCards> entries, String fileName)
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
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(putInFileBuilder.toString().getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}
	}
	
	public void saveTempPresentFile(String entries, String fileName)
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
					Log.e(TAG, "Directory Created");
					File saveIt = new File(appDirectory, fileName);
					fos = new FileOutputStream(saveIt);
					fos.write(entries.getBytes());
					fos.close();
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(entries.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
			}
	}

public void saveTempAbsentFile(String entries, String fileName)
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
				Log.e(TAG, "Directory Created");
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(entries.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
			}
		} else {
			File saveIt = new File(appDirectory, fileName);
			fos = new FileOutputStream(saveIt);
			fos.write(entries.getBytes());
			fos.close();
			Log.e(TAG, "Directory Created");
		}
	
}

	public boolean ifAttendanceFileExists(String file_name, View v) {
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
		FileOutputStream fos;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Attendance");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void startQuiz(View view) throws IOException {
		Intent i = new Intent(view.getContext(), QSetList.class);
		saveNewRosterFile(cardList,getPollName()+".csv");
		currentAttendanceForQuiz(cardList);
		saveNameId(cardList);
		startActivity(i);
	}

	public void setAttendanceRoster(String rosterName) {
		SharedPreferences pref = getApplicationContext().getSharedPreferences("globals", 0);
		Editor editor = pref.edit();
		editor.putString("attendance_current", rosterName);
		editor.commit();
		keyVal.setAttendanceBool(true);
		attendanceRoster = rosterName;
		keyVal.setAttendanceRoster(attendanceRoster);
	}

	public String getAttendanceRoster() {
		return attendanceRoster;
	}
	
	public void saveNameId(List<MyCards> cardList) throws IOException{
		File fileDirectory1 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults");
		if (!fileDirectory1.exists()) {
			if (fileDirectory1.mkdirs()) {
				
				Log.e(TAG, "Directory Created");
			}
		}
		
		
		File filePath1 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "pIdList.csv");
		// String copyName = getCopyName(fileDirectory, fName);
		File filePath2 = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempAttendanceResults/" + "pNameList.csv");
		
		StringBuilder idb = new StringBuilder();
		StringBuilder nameb = new StringBuilder();
		for(int i=0; i<cardList.size();i++){
			if(cardList.get(i).isPresent()){
				idb.append(cardList.get(i).getId());
				idb.append("\n");
				nameb.append(cardList.get(i).getName());
				nameb.append("\n");
			}
		}
		
		saveTempPresentFile(idb.toString(),"pIdList.csv");
		saveTempAbsentFile(nameb.toString(),"pNameList.csv");
	}

}
