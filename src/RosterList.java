package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

public class RosterList extends ActionBarActivity {
	private static final int READ_REQUEST_CODE = 1;
	private static final String TAG = null;
	public RecyclerView rObject;
	public RosterListAdapter adapter;
	private CardView currentCard;
	Uri ur1;
	boolean test = true;
	String abc = null;
	ArrayList<RosterListCard> cardList = null;
	TextView curRost;
	AlertDialog dialBox = null;
	Menu menu;
	String rosterQuizStep1 = "Step 1 : Choose a roster for your quiz";
	KeyValues keyVal;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keyVal = new KeyValues(getApplicationContext(), "globals");
		//Toast.makeText(this, "RosterList", Toast.LENGTH_LONG).show();
		setContentView(R.layout.list_roster_recycle);
		rObject = (RecyclerView) findViewById(R.id.recycler_view);
		curRost = (TextView) findViewById(R.id.currentNav);
		currentCard = (CardView) findViewById(R.id.show_current);
		String prefRoster = keyVal.getRoster();
		if (prefRoster != null) {			
				curRost.setText("Current Roster: " + removeExt(prefRoster));
			} else {
				if (currentCard != null) {
					currentCard.setClickable(false);
				}
				curRost.setText("No roster selected");
				if (keyVal.getRosterAttendanceBool()) {
					Button quickBut = (Button) findViewById(R.id.rosterListQuick);
					Button createBut = (Button) findViewById(R.id.rosterListCreate);
					quickBut.setVisibility(View.GONE);
					createBut.setVisibility(View.VISIBLE);
				}
			
		} 
		if (keyVal.getRosterQuizBool()) {
			curRost.setText(rosterQuizStep1);
		}

		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rObject.setLayoutManager(llm);
		ArrayList<RosterListCard> cardList = makeCards();
		adapter = new RosterListAdapter(RosterList.this, cardList);
		rObject.setAdapter(adapter);

	}
	
	public void onBackPressed(){
		super.onBackPressed();
		Intent i = new Intent(this,JumpInMain.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	// This method create Card objects to be displayed when all stored Rosters
	// need to be displayed. The method adds the Card objects to an arraylist
	// and return it.
	public ArrayList<RosterListCard> makeCards() {

		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		// Boolean isSdCard =
		// android.os.Environment.isExternalStorageRemovable();
		File appDirectory;
		String[] fileList = null;
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
					fileList = appDirectory.list();
				}
			} else {
				fileList = appDirectory.list();
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}

		ArrayList<RosterListCard> rosterList = new ArrayList<RosterListCard>();
		// AssetManager assetManager = getResources().getAssets();

		// InputStream inputStream = null;
		// String[] files;
		try {
			// files = assetManager.list("roster_store");
			if (fileList!=null && fileList.length>0) {
				for (String file : fileList) {
					if (file.endsWith(".csv")) {
						String name = removeExt(file);
						/*
						 * InputStream is = assetManager.open("roster_store/" +
						 * file); // FileInputStream fis = openFileInput(file);
						 * InputStreamReader isr = new InputStreamReader(is);
						 * BufferedReader br = new BufferedReader(isr);
						 */
						BufferedReader br = Roster.rosterReader(file);
						if (br != null) {
							ReadCSV cr = new ReadCSV(br, 2, ",");
							cr.readLines();
							int num = cr.getCount();
							RosterListCard newCard = new RosterListCard(name,
									num);
							rosterList.add(newCard);
						}
					}
				}

				Log.e(TAG, "Whoa! Its found :)");
			} else {
				AlertDialog.Builder build = new AlertDialog.Builder(this)
						.setMessage(R.string.lonely_here)
						.setPositiveButton(R.string.create_new_roster, new OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent i = new Intent(context,CreateNewRoster.class);
								//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
								
							}
							
						});
				AlertDialog dialog = build.create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return rosterList;
	}

	// This method returns the filename after dropping its extension.
	public static String removeExt(String str) {
		return str.substring(0, str.length() - 4);
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
		case R.id.roster:
			if (keyVal.getRosterBool()) {
				onActionRosterClick();
			} else {

			}
			break;

		case R.id.attendance:
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

		return super.onOptionsItemSelected(item);
	}

	// ///////////////////////////////////////////////////////////

	public void onActionRosterClick() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(
				getString(R.string.action_roster_start) + keyVal.getRoster()
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
								currentCard.setClickable(false);
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

	// //////////////////////////////////////////////////////////

	public void addFromDrive(View view) {
		/*
		 * Intent intent = new Intent(this, CopyFileFromDrive.class); final
		 * String parentClass = "RosterList"; qListParent = 1;
		 * intent.putExtra(EXTRA_MESSAGE_PARENT, parentClass);
		 * startActivity(intent);
		 */
		performFileSearch();

	}

	// / for copying file to local storage

	public void performFileSearch() {

		// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
		// file
		// browser.

		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

		// Filter to only show results that can be "opened", such as a
		// file (as opposed to a list of contacts or timezones)
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		// Filter to show only images, using the image MIME data type.
		// If one wanted to search for ogg vorbis files, the type would be
		// "audio/ogg".
		// To search for all documents available via installed storage
		// providers,
		// it would be "*/*".

		// Mime Types : https://developers.google.com/drive/web/mime-types
		intent.setType("*/*");
		// Intent resultData = intent;
		startActivityForResult(intent, READ_REQUEST_CODE);

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent resultData) {

		if (requestCode == READ_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {

			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();

				String fName = qSetNameFromUri(uri);
				try {

					String msg = readTextFromUri(uri);
					copyQSetToLocalFile(msg, fName);
					cardList = makeCards();
					adapter = new RosterListAdapter(RosterList.this, cardList);
					rObject.setAdapter(adapter);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "Uri: " + uri.toString());

			}

		}
	}

	private String readTextFromUri(Uri uri) throws IOException {
		InputStream inputStream = getContentResolver().openInputStream(uri);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		int elements = 5;
		ReadCSV read1 = new ReadCSV(reader, elements, ",");

		return read1.readLines();
	}

	public String qSetNameFromUri(Uri uri) {
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

	public boolean copyQSetToLocalFile(String fileContent, String qSetFile)
			throws IOException {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		FileOutputStream fos;
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");

			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				if (ifQSetExists(qSetFile)) {
					/*
					 * if file exists then rename the new file by adding current
					 * system time
					 */
					File saveIt = new File(appDirectory,
							renameQSetFile(qSetFile));
					fos = new FileOutputStream(saveIt);
					fos.write(fileContent.getBytes());
					fos.close();
				} else {

					File saveIt = new File(appDirectory, qSetFile);
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

	public String renameQSetFile(String qSetFile) {
		String name = removeQSetExt(qSetFile);
		Long systemTime = System.nanoTime();
		return (name + systemTime.toString() + ".csv");
	}

	public String removeQSetExt(String str) {
		return str.substring(0, str.length() - 4);
	}

	public static boolean ifQSetExists(String qSetFile) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);

		File appDirectory;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Rosters");
		if (new File(appDirectory + "/" + qSetFile).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void currentOnClick(View view) {
		Intent intent = new Intent(this, Roster.class);
		String fileName = removeExt(keyVal.getRoster());
		intent.putExtra("fileName", fileName);
		RosterListAdapter.fromRosterList = true;
		startActivity(intent);

	}

	public void callQuickPoll(View view) {
		Intent cam = new Intent(this, QuickPoll.class);
		// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(cam);
	}

	public void callCreateRoster(View view) {
		Intent i = new Intent(this, CreateNewRoster.class);
		// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}

}
