package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Roster extends ActionBarActivity {
	private static final int READ_REQUEST_CODE = 1;
	private static final String TAG = null;
	private static final String Tag = "URI";
	private static String fileName;
	private RecyclerView rObject;
	private MyAdapter adapter;
	public boolean saveAsNew = false;
	private static boolean rosterModified = false;
	private boolean isEntryAdded = false;
	Uri ur1;
	Button attBut;
	Button quizBut;
	TextView curRost;
	ImageButton addEnt;
	final Context context = this;
	boolean test = true;
	String abc = null;
	public static List<MyCards> cardList;
	private static String currentRosterFile;
	private static ArrayList<String> colorIndices = new ArrayList<String>();
	String rosterQuizStep2 = "Step 2: Start attendance for this roster";
	LinearLayoutManager llm;

	AlertDialog dialBox = null;
	KeyValues keyVal;
	Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// AttendanceResults.attendance_results = false;

		keyVal = new KeyValues(getApplicationContext(),"globals");
		keyVal.setRosterBool(true);
		Intent intent = getIntent();
		fileName = intent.getExtras().getString("fileName");
		
		fileName = fileName+".csv";
		
		if (!RosterListAdapter.fromRosterList) {
			fileName = currentRosterFile;
			keyVal.setRoster(fileName);
		} else {
			currentRosterFile = fileName;
			keyVal.setRoster(currentRosterFile);
		}

		
	
		if (keyVal.getManageBool()) {
			setContentView(R.layout.manage_roster_recycle);		
		} else if (keyVal.getRosterAttendanceBool()) {
			setContentView(R.layout.attendance_roster);
			attBut = (Button) findViewById(R.id.start_attendance_button);
			addEnt = (ImageButton) findViewById(R.id.addEntries);
			addEnt.setVisibility(View.VISIBLE);
		} else if (keyVal.getRosterQuizBool()) {
			setContentView(R.layout.attendance_roster);
			attBut = (Button) findViewById(R.id.start_attendance_button);
			quizBut = (Button) findViewById(R.id.chooseQSet);
			if (keyVal.getAttendanceBool()) {
				String attRoster = keyVal.getAttendanceRoster();
				if (currentRosterFile.matches(attRoster)) {
					quizBut.setVisibility(View.VISIBLE);
					attBut.setVisibility(View.GONE);
				} else {
					quizBut.setVisibility(View.GONE);
					attBut.setVisibility(View.VISIBLE);
				}
			}
			addEnt = (ImageButton) findViewById(R.id.addEntries);
		}else{
			setContentView(R.layout.roster_recycle);

			attBut = (Button) findViewById(R.id.start_attendance_button);
		}

		curRost = (TextView) findViewById(R.id.currentNav);

		rObject = (RecyclerView) findViewById(R.id.recycler_view);

		llm = new LinearLayoutManager(this);
		//RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		

		if (!currentRoster().matches("null")) {
			//Set the roster value in Shared Preferences 
			/*SharedPreferences pref = getApplicationContext().getSharedPreferences("globals", 0);
			Editor prefEditor = pref.edit();
			prefEditor.putString("roster_current", currentRoster());
			prefEditor.putBoolean("roster_bool", true);
			prefEditor.commit();
			*/
			curRost.setText("Current Roster: "
					+ RosterList.removeExt(currentRoster()));

		} else {
			if(keyVal.getRosterBool() == false){
			curRost.setText("No roster selected");
			}else{
				curRost.setText(RosterList.removeExt(keyVal.getRoster()));
			}

		}
		
		
		if(keyVal.getRosterQuizBool()){
			curRost.setText(rosterQuizStep2);
		}
		// fileName = "sanyam";
		BufferedReader br = null;
		// AssetManager assetManager = getResources().getAssets();
		// InputStream inputStream = null;
		cardList = new ArrayList<MyCards>();
		// performFileSearch();
		try {
			/*
			 * inputStream = assetManager.open("roster_store/" + fileName); if
			 * (inputStream != null) Log.d(TAG, "It worked!"); InputStreamReader
			 * isr = new InputStreamReader(inputStream); br = new
			 * BufferedReader(isr);
			 */
			// ReadCSV cr = new ReadCSV(br,2,",");
			br = rosterReader(fileName);
			ReadRoster loadRoster = new ReadRoster(br, 2, ",");

			try {
				cardList = loadRoster.getCards();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// deleteLocation will be used to decide the color of the cards in
		// MyAdapter

		adapter = new MyAdapter(Roster.this, cardList, getApplicationContext());
		rObject.setAdapter(adapter);
		llm = new LinearLayoutManager(this);
		//RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rObject.setLayoutManager(llm);
		
	}
	
	public void onBackPressed(){
		super.onBackPressed();
		Intent i = new Intent(this,RosterList.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
								Intent i = new Intent(context,RosterList.class);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
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
	
	public static BufferedReader rosterReader(String file_name)
			throws FileNotFoundException {
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");
			String filePath = appDirectory + "/" + file_name;

			File rosterFile = new File(filePath);
			FileInputStream fis = new FileInputStream(rosterFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
		}
		return bufRead;
	}

	private String readTextFromUri(Uri uri) throws IOException {
		InputStream inputStream = getContentResolver().openInputStream(uri);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		int elements = 2;
		ReadCSV read1 = new ReadCSV(reader, elements, ",");

		abc = read1.readLines();
		return abc;
	}

	public void onResume() {
		super.onResume();
		if (test == false) {

			Log.e(Tag, "I am resuming");

		}

	}

	private static String currentRoster() {
		return currentRosterFile;
	}

	public void chooseQSet(View view) {
		Intent i = new Intent(this, QSetList.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
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
				test = false;
				uri = resultData.getData();
				ur1 = uri;
				String fName = fileNameFromUri(ur1);
				try {

					String msg = readTextFromUri(uri);
					copyToLocalFile(msg, fName);
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
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, file_name);
				fos = new FileOutputStream(saveIt);
				fos.write(fileContent.getBytes());
				fos.close();
				return true;
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}
		return false;
	}

	/*
	 * Attendance starts on button click. before starting attendance, we need to
	 * save any changes that the user might have made to the loaded roster. The
	 * user gets an option of overwriting the existing roster or save the
	 * modified roster as a new one. These options appear as a Dialog. if the
	 * user chooses to save as a new Roster then another dialog appears that
	 * asks the user to enter the new rostername
	 */
	public void onButtonClick(View view) {
		if (isRosterModified() || isEntryAdded) {
			// does the user want to overwrite the existing roster or save as a
			// new one
			final Intent startAttendanceIntent = new Intent(this,
					TakeCameraAttendance.class);
			startAttendanceIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			Dialog dialog = new Dialog(view.getContext());
			// dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			dialog.setContentView(R.layout.overwrite_save_roster);
			dialog.setTitle("Save Changes!");

			Button saveNew = (Button) dialog.findViewById(R.id.saveButton);
			Button saveOld = (Button) dialog.findViewById(R.id.overwriteButton);
			// saveNew lets user to save Roster as a new roster
			saveNew.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					saveAsNew(v, startAttendanceIntent, "Enter Roster Name");
				}
			});

			// saveOld overwrites the existing roster
			saveOld.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						saveModified();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentRosterFile = fileName;
					if (refresh()) {
						RosterListAdapter.fromRosterList = false;
						startActivity(startAttendanceIntent);
					}
				}
			});
			dialog.show();
		} else {
			currentRosterFile = fileName;
			Intent i = new Intent(this, TakeCameraAttendance.class);
			if (refresh()) {
				i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				RosterListAdapter.fromRosterList = false;
				startActivity(i);
			}
		}

	}

	public static void setModifyRoster() {
		rosterModified = true;
	}

	public static void unsetModifyRoster() {
		rosterModified = false;
	}

	public static boolean isRosterModified() {
		if (rosterModified == true) {
			return true;
		}
		return false;
	}

	public void saveModified() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cardList.size(); i++) {
			sb.append(cardList.get(i).getName());
			sb.append(",");
			sb.append(cardList.get(i).getId());
			sb.append("\n");
		}
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Rosters/" + fileName);
		if (filePath.delete()) {
			saveNewRosterFile(cardList, fileName);
			currentRosterFile = fileName;
			if (refresh()) {
				RosterListAdapter.fromRosterList = false;
			}
		}
		Log.e(TAG, "it will write");
	}

	public void saveAsNew(View v, final Intent i, String title) {
		final Dialog dial = new Dialog(v.getContext());
		dial.setContentView(R.layout.new_roster_name);
		dial.setTitle(title);
		Button sButton = (Button) dial.findViewById(R.id.createButton);
		final EditText fName = (EditText) dial.findViewById(R.id.newRosterName);
		sButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				if (!ifFileExists(fName.getText().toString() + ".csv", v)) {
					// saveModified();
					try {
						saveNewRosterFile(cardList, fName.getText().toString()
								+ ".csv");
						currentRosterFile = fName.getText().toString() + ".csv";
						if (refresh()) {
							RosterListAdapter.fromRosterList = false;
							startActivity(i);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					saveAsNew(v, i, "File Name already used!");
				}
			}
		});
		dial.show();
	}

	public void saveAsNew(View v, String title) {
		if (isEntryAdded == true || isRosterModified() == true) {
			final Dialog dial = new Dialog(v.getContext());
			dial.setContentView(R.layout.new_roster_name);
			dial.setTitle(title);
			Button sButton = (Button) dial.findViewById(R.id.createButton);
			final EditText fName = (EditText) dial
					.findViewById(R.id.newRosterName);
			sButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// onDialogButton(v);
					if (!ifFileExists(fName.getText().toString(), v)) {
						// saveModified();
						try {

							saveNewRosterFile(cardList, fName.getText()
									.toString() + ".csv");
							isEntryAdded = false;
							currentRosterFile = fName.getText().toString()
									+ ".csv";
							RosterListAdapter.fromRosterList = false;
							refresh();
							unsetModifyRoster();

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
		} else {
			Dialog noChange = new Dialog(v.getContext());
			noChange.setContentView(R.layout.no_change_layout);
			noChange.show();
		}
	}

	public void saveAsNew(View view) {
		String title = "Save as new roster";
		saveAsNew(view, title);
	}

	public static boolean ifFileExists(String file_name, View v) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {
			Dialog dial = new Dialog(v.getContext());
			dial.setContentView(R.layout.sd_warning);
			dial.setTitle("Warning!");
			TextView warn = (TextView) dial.findViewById(R.id.warning);
			warn.setText("SD Card not found. Please check that Memory card is inserted properly.");
		//	warn.setTextColor(Color.WHITE);
		//	warn.setBackgroundColor(Color.RED);
		//	CardView warnCard = (CardView) dial.findViewById(R.id.card_view);
		//	warnCard.setCardBackgroundColor(Color.RED);
			dial.show();
		}
		File appDirectory;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Rosters");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean ifFileExists(String file_name) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);

		File appDirectory;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean ifFileExists(String file_name, String parent) {
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);

		File appDirectory;
		FileOutputStream fos;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards");
		if (parent.equals("RosterList")) {
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards");
		}

		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
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
				putInFileBuilder.append("\n");
			}

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/Rosters");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, fileName);
				fos = new FileOutputStream(saveIt);
				fos.write(putInFileBuilder.toString().getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
				rosterModified = false;
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}
	}

	public void addEntries(View view) {
		final Dialog dialAdd = new Dialog(view.getContext());
		dialAdd.setContentView(R.layout.add_roster_entry);
		dialAdd.setTitle("Add Entries");
		final EditText name = (EditText) dialAdd.findViewById(R.id.sName);
		final EditText id = (EditText) dialAdd.findViewById(R.id.sId);
		Button sBut = (Button) dialAdd.findViewById(R.id.saveChanges);
		Button aBut = (Button) dialAdd.findViewById(R.id.addMore);
		aBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyCards newCard = new MyCards();
				newCard.setName(name.getText().toString());
				newCard.setId(id.getText().toString());

				if (newCard.getName().isEmpty() || newCard.getId().isEmpty()) {
					isEntryAdded = false;
				} else {
					if (isDuplicate(newCard)) {
						isEntryAdded = false;
						addEntries(v);
						showDuplicateAlert();
					} else {
						// isEntryAdded = true;
						cardList.add(newCard);
						addEntries(v);
						try {
							//colorIndices.add(newCard.getId());
							saveModified();
							Toast.makeText(v.getContext(), "Entry Added and Changes Saved", Toast.LENGTH_SHORT).show();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				dialAdd.dismiss();
				/*
				 * Toast prompt = Toast.makeText( v.getContext(),
				 * "Entries added successfully! Scroll down to see changes.",
				 * Toast.LENGTH_SHORT); prompt.show();
				 */
			}
		});

		sBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				MyCards newCard = new MyCards();
				newCard.setName(name.getText().toString());
				newCard.setId(id.getText().toString());

				if (newCard.getName().isEmpty() || newCard.getId().isEmpty()) {
					isEntryAdded = false;
				} else {
					if (isDuplicate(newCard)) {
						isEntryAdded = false;
						addEntries(v);
						showDuplicateAlert();
					} else {
						// isEntryAdded = true;
						cardList.add(newCard);
						//addEntries(v);
						try {
							//colorIndices.add(newCard.getId());
							saveModified();					
							Toast.makeText(v.getContext(), "Any changes that you made, were saved automatically!", Toast.LENGTH_SHORT).show();
							dialAdd.dismiss();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				/*
				 * Toast prompt = Toast.makeText(v.getContext(),
				 * "Successfully made changes to existing entries!" ,
				 * Toast.LENGTH_SHORT); prompt.show();
				 */
				dialAdd.dismiss();
			}
		});
		dialAdd.show();
	}

	protected void onStop() {
		super.onStop(); // Always call the superclass method first

		if (isEntryAdded || isRosterModified()) {
			saveAsNew("Save Changes!");
		}
	}

	public void saveAsNew(String title) {
		final Dialog dial = new Dialog(context);
		dial.setContentView(R.layout.new_roster_name);
		dial.setTitle(title);
		Button sButton = (Button) dial.findViewById(R.id.createButton);
		final EditText fName = (EditText) dial.findViewById(R.id.newRosterName);
		sButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				if (!ifFileExists(fName.getText().toString(), v)) {
					// saveModified();
					try {
						saveNewRosterFile(cardList, fName.getText().toString()
								+ ".csv");
						isEntryAdded = false;
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

	public boolean refresh() {

		fileName = currentRosterFile;

		// fileName = "sanyam";
		BufferedReader br = null;
		// AssetManager assetManager = getResources().getAssets();
		// InputStream inputStream = null;
		cardList = new ArrayList<MyCards>();
		try {
			/*
			 * inputStream = assetManager.open("roster_store/" + fileName); if
			 * (inputStream != null) Log.d(TAG, "It worked!"); InputStreamReader
			 * isr = new InputStreamReader(inputStream); br = new
			 * BufferedReader(isr);
			 */
			// ReadCSV cr = new ReadCSV(br,2,",");
			br = rosterReader(fileName);
			if (br != null) {
				ReadRoster loadRoster = new ReadRoster(br, 2, ",");

				try {
					cardList = loadRoster.getCards();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else{
				/*AlertDialog.Builder emptyAlert = new AlertDialog.Builder(this);
				emptyAlert.setMessage("The selected roster is empty!\nPlease choose a different ");
				emptyAlert.setTitle("Warning!");*/
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		adapter = new MyAdapter(Roster.this, cardList, getApplicationContext());
		rObject.setAdapter(adapter);
		rObject.setLayoutManager(llm);
		return true;
	}

	public void showDuplicateAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.id_exists_message).setTitle(
				R.string.id_exists_title);
		AlertDialog alert = builder.create();

		alert.show();
	}

	public boolean isDuplicate(MyCards newCard) {
		ArrayList<String> entriesId = new ArrayList<String>();
		Iterator<MyCards> it = cardList.iterator();
		while (it.hasNext()) {
			MyCards card = it.next();
			entriesId.add(card.getId());
		}
		if (entriesId.contains(newCard.getId())) {

			return true;
		} else {
			return false;
		}
	}
	
	public static boolean unsetRoster(){
		currentRosterFile = "null";
		fileName = null;
		return true;
	}
	/*
	 * public static void editDialog(){ SaveNewRosterDialog dial = new
	 * SaveNewRosterDialog(thisAct); dial.show(); }
	 */
}
