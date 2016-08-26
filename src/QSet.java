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
import android.widget.TextView;

public class QSet extends ActionBarActivity {

	private static final String TAG = null;
	private static final String Tag = "URI";
	private RecyclerView rObject;
	private MyQAdapter adapter;
	public boolean saveAsNew = false;
	private static boolean fromQSet = true;
	private String file_Name;
	private static String qSetName;
	private static boolean isQSetAvailable = false;
	public static String qSetNameGlobal;
	Uri ur1;
	Button attBut;
	final Context context = this;
	private int READ_REQUEST_CODE = 42;
	boolean test = true;
	String abc = null;
	public static List<QSetCards> qSetList;
	private static boolean qSetModified = false;
	
	String rosterQuizStep6 = "Step 6: Start quiz with this question set";
	String woRosterQuizStep2 = "Step 2: Start quiz with this question set";

	AlertDialog dialBox = null;
	Menu menu;
	KeyValues keyVal;
	TextView curRost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// AttendanceResults.attendance_results = false;
		setContentView(R.layout.qset_recycler);
		keyVal = new KeyValues(getApplicationContext(),"globals");
		attBut = (Button) findViewById(R.id.start_attendance_button);
		CardView navCard = (CardView) findViewById(R.id.show_current);
		curRost = (TextView) findViewById(R.id.currentNav);
		rObject = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rObject.setLayoutManager(llm);
		Intent intent = getIntent();
		
		file_Name = intent.getExtras().getString("setName");
		qSetNameGlobal = RosterList.removeExt(file_Name);

	//	file_Name = qSetNameGlobal + ".csv";
		if (file_Name.matches("null")) {
			//file_Name = currentQSet() + ".csv";
			file_Name = keyVal.getQSet();
		} else {
			setQSet();
			/*isQSetAvailable = true;*/
			SharedPreferences pref = getApplicationContext().getSharedPreferences("globals", 0);
			Editor editor = pref.edit();
			editor.putBoolean("qset_bool", true);
			editor.commit();
		}
		// qSetName = file_Name;
		// file_Name = "sanyam";
		
		// AssetManager assetManager = getResources().getAssets();
		// InputStream inputStream = null;
		qSetList = new ArrayList<QSetCards>();
		// performFileSearch();
		ArrayList<String> testIds = new ArrayList<String>();
		try {
			/*
			 * inputStream = assetManager.open("roster_store/" + file_Name); if
			 * (inputStream != null) Log.d(TAG, "It worked!"); InputStreamReader
			 * isr = new InputStreamReader(inputStream); br = new
			 * BufferedReader(isr);
			 */
			// ReadCSV cr = new ReadCSV(br,2,",");
			if(file_Name.endsWith(".csv")){
			BufferedReader br = qsetReader(file_Name);
			ReadRoster loadRoster = new ReadRoster(br, 5, ",");
			try {
				qSetList = loadRoster.getQuestionCards();
				//br.close();
				// testIds = loadRoster.getIds();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}else if(file_Name.endsWith(".txt")){
				BufferedReader br = qsetReader(file_Name);
				
				TextReader tr = new TextReader(br);
				qSetList = tr.getQuestionCards();
				//br.close();
				// testIds = loadRoster.getIds();
			}

			

		} catch (IOException e) {
			e.printStackTrace();
		}
		if(keyVal.getRosterQuizBool()){
			navCard.setVisibility(View.VISIBLE);
			curRost.setText(rosterQuizStep6);
		}else{
			navCard.setVisibility(View.VISIBLE);
			curRost.setText(woRosterQuizStep2);
		}

		adapter = new MyQAdapter(QSet.this, qSetList);
		rObject.setAdapter(adapter);

		/*
		 * Toast toast = Toast.makeText(this,
		 * "click any card to start live attendance", duration); toast.show();
		 */
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
	
	public static BufferedReader qsetReader(String file_Name)
			throws FileNotFoundException {
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/QuestionSet");
			String filePath = appDirectory + "/" + file_Name;

			File qsetFile = new File(filePath);
			// if(qsetFile.exists()){
			FileInputStream fis = new FileInputStream(qsetFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			// }
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

	private void setQSet() {
		qSetName = file_Name;
		keyVal.setQSet(qSetName);
	}

	private static String currentQSet() {
		return qSetName;
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
				String fName = file_NameFromUri(ur1);
				try {

					String msg = readTextFromUri(uri);
					copyToLocalFile(msg, fName);
					// TextView textView = new TextView(this);
					// textView.setText(msg);
					// setContentView(textView);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "Uri: " + uri.toString());

			}

		}
	}

	public static String file_NameFromUri(Uri uri) {
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

	public boolean copyToLocalFile(String fileContent, String file_Name)
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
					+ "/QCards/QSet");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, file_Name);
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
		if (isqSetModified()) {
			// does the user want to overwrite the existing roster or save as a
			// new one
			final View v1;
			final Intent startAttendanceIntent = new Intent(this,
					TakeCameraAttendance.class);
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
					saveModified();
					startActivity(startAttendanceIntent);
				}
			});
			dialog.show();
		} else {
			Intent i = new Intent(this, TakeCameraAttendance.class);
			startActivity(i);
		}

	}

	public void startQuizAttendance(View view) {
		setQSet();
		Intent i = new Intent(this, RosterList.class);
		setParentQSet();
		startActivity(i);
	}

	public static boolean isParentQSet() {
		if (fromQSet) {
			return true;
		}
		return false;
	}

	private void setParentQSet() {
		fromQSet = true;
	}

	public static void modifyQset() {
		qSetModified = true;
	}

	public static boolean isqSetModified() {
		if (qSetModified == true) {
			return true;
		}
		return false;
	}

	public void saveModified() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < qSetList.size(); i++) {
			sb.append(qSetList.get(i).getQuestion());
			sb.append(",");
			sb.append(qSetList.get(i).getA());
			sb.append(",");
			sb.append(qSetList.get(i).getB());
			sb.append(",");
			sb.append(qSetList.get(i).getC());
			sb.append(",");
			sb.append(qSetList.get(i).getD());
			sb.append("\n");
		}
		String writeToFile = sb.toString();
		Log.e(TAG, "it will write");
	}

	public void saveAsNew(View v, final Intent i, String title) {
		final Dialog dial = new Dialog(v.getContext());
		dial.setContentView(R.layout.new_qset_name);
		dial.setTitle(title);
		Button sButton = (Button) dial.findViewById(R.id.createButton);
		final EditText fName = (EditText) dial.findViewById(R.id.newQSetName);
		sButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				if (!ifFileExists(fName.getText().toString(), v)) {
					// saveModified();
					try {
						saveNewRosterFile(qSetList, fName.getText().toString()
								+ ".csv");
						startActivity(i);
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
			warn.setTextColor(Color.WHITE);
			warn.setBackgroundColor(Color.RED);
			CardView warnCard = (CardView) dial.findViewById(R.id.card_view);
			warnCard.setBackgroundColor(Color.RED);
			dial.show();
		}
		File appDirectory;
		FileOutputStream fos;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/QuestionSet");
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
		FileOutputStream fos;
		appDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/QuestionSet");
		if (new File(appDirectory + "/" + file_name).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void saveNewRosterFile(List<QSetCards> entries, String file_Name)
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
				QSetCards entry = entries.get(i);
				putInFileBuilder.append(entry.getQuestion());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.getA());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.getB());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.getC());
				putInFileBuilder.append(",");
				putInFileBuilder.append(entry.getD());
				putInFileBuilder.append("\n");
			}

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/QuestionSet");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
			} else {
				File saveIt = new File(appDirectory, file_Name);
				fos = new FileOutputStream(saveIt);
				fos.write(putInFileBuilder.toString().getBytes());
				fos.close();
				String[] fileList = appDirectory.list();
				Log.e(TAG, "Directory Created");
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}

	}

	/*
	 * public static void editDialog(){ SaveNewRosterDialog dial = new
	 * SaveNewRosterDialog(thisAct); dial.show(); }
	 */

	public void startQuiz(View view) {
		if (keyVal.getRosterQuizBool()) {
			Intent i = new Intent(this, Quiz.class);
			i.putExtra("qFile", file_Name);
			startActivity(i);
		} else {
			Intent i = new Intent(this, QuizWithoutRoster.class);
			i.putExtra("qFile", file_Name);
			startActivity(i);
		}

	}
}
