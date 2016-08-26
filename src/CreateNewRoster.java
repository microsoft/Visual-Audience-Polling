package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewRoster extends ActionBarActivity {

	public static String newRosterName;
	AlertDialog dialBox;
	Menu menu;
	KeyValues keyVal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_roster_name);
		keyVal = new KeyValues(getApplicationContext(),"globals");

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
										//Roster.isRosterAvailable = false;
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
						getString(R.string.action_quest_start)
								+ keyVal.getQSet()
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
										menu.getItem(2)
										.setIcon(
												getResources()
														.getDrawable(
																R.drawable.quest_cross_96));
									}
								});
				dialBox = adb.create();
				dialBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialBox.show();
			}
			


	public void createRoster(View view) {
		EditText rName = (EditText) findViewById(R.id.newRosterName);
		if (!rName.getText().toString().matches("")) {
			if (!ifRosterExists(rName.getText().toString())) {
				newRosterName = rName.getText().toString();
				Intent cam = new Intent(this, RosterEntry.class);
				//cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(cam);
			}else{
				Toast.makeText(getApplicationContext(),
						"Please choose a different name for new roster. A roster with the same name already exists!",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Please enter a name for the new roster!",
					Toast.LENGTH_LONG).show();
		}

	}

	public boolean ifRosterExists(String rosterName) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);

		String file_name = rosterName + ".csv";
		File appDirectory;
		FileOutputStream fos;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Rosters");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

}
