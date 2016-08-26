package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

// TODO: Auto-generated Javadoc
/**
 * The Class QCardsActivity.
 */
public class QCardsActivity extends ActionBarActivity {
	
	/** The card view. */
	CardView cardView;
	
	/** The Constant READ_REQUEST_CODE. */
	private static final int READ_REQUEST_CODE = 42;
	
	/** The Constant TAG. */
	private static final String TAG = null;
	
	/** The dialbox. */
	AlertDialog dialBox = null;
	
	/** The menu. */
	Menu menu;
	
	/** The keyVal. */
	KeyValues keyVal;

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	
	@Override
	  public void onResume() {
	    super.onResume();
	    checkForCrashes();
	    checkForUpdates();
	  }
	
	private void checkForCrashes() {
	    CrashManager.register(this, "5c06003cabc24383a75d89bd34f3619e");
	  }

	  private void checkForUpdates() {
	    // Remove this for store builds!
	    UpdateManager.register(this, "5c06003cabc24383a75d89bd34f3619e");
	  }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qcards);
		keyVal = new KeyValues(getApplicationContext(),"globals");	
		if(!JumpInMain.fromJumpIn && !GetCards.fromGetCards ){
			keyVal.clearAll();
		}
		checkForUpdates();
	}

	
	public void onBackPressed(){
		super.onBackPressed();
		keyVal.clearAll();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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
		
		return super.onOptionsItemSelected(item);
	}


	

	// Card onClick methods
	/**
	 * Called on clicking 'First Time Here' button.
	 *
	 * @param view the view
	 */
	public void clickFirstFirst(View view) {
		Toast.makeText(view.getContext(),"This feature is currently disabled. Try Jump In",Toast.LENGTH_SHORT).show();
	}

	/**
	 * onClick for 'Get Cards' button
	 *
	 * @param view the view
	 */
	public void clickFirstGet(View view) {
		Toast.makeText(view.getContext(),"This feature is currently disabled. Try Jump In",Toast.LENGTH_SHORT).show();
/*
		if (isSdCardPresent()) {
			Intent intent = new Intent(this, GetCards.class);
			startActivity(intent);
		} else {
			showNoSdToast();
		}
*/
		// performFileSearch();
	}

	/**
	 * onClick for 'Jump In' button
	 *
	 * @param view the view
	 */
	public void clickFirstJump(View view) {
		if (isSdCardPresent()) {
			Intent intent = new Intent(this, JumpInMain.class);
			startActivity(intent);
		} else {
			showNoSdToast();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent resultData) {

		if (requestCode == READ_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {

			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();
				try {
					String msg = readTextFromUri(uri);
					TextView textView = new TextView(this);
					textView.setText(msg);
					setContentView(textView);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "Uri: " + uri.toString());

			}
		}
	}

	/**
	 * Read text from uri.
	 *
	 * @param uri the uri
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readTextFromUri(Uri uri) throws IOException {
		InputStream inputStream = getContentResolver().openInputStream(uri);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		int elements = 4;
		ReadCSV read1 = new ReadCSV(reader, elements, ",");
		String abc = read1.readLines();
		return abc;
	}

	// ///////////////////////////////////////////////////////////

	/**
	 * On action roster click.
	 */
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

	/**
	 * On action attendance click.
	 */
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

	/**
	 * On action question click.
	 */
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

	/**
	 * Checks if SD Card is present or not
	 *
	 * @return true, SD Card is present
	 */
	public boolean isSdCardPresent() {

		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		Boolean isSdCard = android.os.Environment.isExternalStorageRemovable();

		if (isExternalAvailable && isSdCard) {
			return true;
		} else {
			return true;
		}
	}

	/**
	 * Show no sd toast.
	 */
	public void showNoSdToast() {
		Toast toast = Toast.makeText(
				this,
				"This requires an SD Card. Please insert SD Card in your phone!",
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	
}
