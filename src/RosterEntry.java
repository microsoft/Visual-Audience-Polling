package com.abhinav.qcards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
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

public class RosterEntry extends ActionBarActivity {

	private static final String TAG = null;
	private RecyclerView rObject;
	private RosterEntryAdapter adapter;
	private EditText name;
	private EditText id;
	private TextView rosterLabel;
	protected boolean isEntryAdded;
	AlertDialog aDial = null;
	public static List<RosterEntryCards> rosterEntries;
	KeyValues keyVal;
	Menu menu;
	AlertDialog dialBox = null;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		// AttendanceResults.attendance_results = false;
		keyVal = new KeyValues(getApplicationContext(),"globals");
		setContentView(R.layout.jump_in_attendance_new);
		rObject = (RecyclerView) findViewById(R.id.recycler_view);
		rosterLabel = (TextView) findViewById(R.id.rosterNameLabel);
		rosterLabel.setText(CreateNewRoster.newRosterName);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rosterEntries = new ArrayList<RosterEntryCards>();
		rObject.setLayoutManager(llm);
		adapter = new RosterEntryAdapter(RosterEntry.this, rosterEntries);
		rObject.setAdapter(adapter);
		int duration = Toast.LENGTH_SHORT;
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

	public void addEntry(View view) throws IOException {

		name = (EditText) findViewById(R.id.enterName);
		id = (EditText) findViewById(R.id.enterId);
		String nameText = name.getText().toString();
		String idText = id.getText().toString();
		if (!nameText.matches("") && !idText.matches("")) {
		/*	rosterEntries.add(makeCards(nameText, idText));*/
			
			RosterEntryCards newCard = new RosterEntryCards();
			newCard.setName(name.getText().toString());
			newCard.setId(id.getText().toString());
			int size = rosterEntries.size();

			if (newCard.getName().isEmpty() || newCard.getId().isEmpty()) {
				isEntryAdded = false;
			} else {
				if (isDuplicate(newCard)) {
					isEntryAdded = false;
					showDuplicateAlert();
				} else {
					// isEntryAdded = true;
					rosterEntries.add(newCard);
					try {
						
						saveData(rosterEntries);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			id.setText("");
			name.setText("");
			name.requestFocus();
			
		} else {
			String warningForm = "Name and Id are required fields";
			Toast.makeText(getApplicationContext(), warningForm,
					Toast.LENGTH_LONG).show();

		}
		// saveData();
	}

	public RosterEntryCards makeCards(String nameText, String idText) {
		RosterEntryCards newCard = new RosterEntryCards();
		newCard.setId(idText);
		newCard.setName(nameText);

		return newCard;
	}

	public void onBackPressed() {
		/*
		 * SaveNewRosterDialog dial = new SaveNewRosterDialog(this);
		 * dial.show();
		 */
		super.onBackPressed();
		Intent i = new Intent(this,RosterList.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	public static void saveData(List<RosterEntryCards> entries)
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
				RosterEntryCards entry = entries.get(i);
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
				File saveIt = new File(appDirectory,
						CreateNewRoster.newRosterName + ".csv");
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

	public boolean isDuplicate(RosterEntryCards newCard) {
		ArrayList<String> entriesId = new ArrayList<String>();
		Iterator<RosterEntryCards> it = rosterEntries.iterator();
		while (it.hasNext()) {
			RosterEntryCards card = it.next();
			entriesId.add(card.getId());
		}
		if (entriesId.contains(newCard.getId())) {

			return true;
		} else {
			return false;
		}
	}

	public void showAddDialog(View view) {
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
				RosterEntryCards newCard = new RosterEntryCards();
				newCard.setName(name.getText().toString());
				newCard.setId(id.getText().toString());
				int size = rosterEntries.size();

				if (newCard.getName().isEmpty() || newCard.getId().isEmpty()) {
					isEntryAdded = false;
				} else {
					if (isDuplicate(newCard)) {
						isEntryAdded = false;
						showAddDialog(v);
						showDuplicateAlert();
					} else {
						// isEntryAdded = true;
						rosterEntries.add(newCard);
						showAddDialog(v);
						try {
							
							saveData(rosterEntries);
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

				RosterEntryCards newCard = new RosterEntryCards();
				newCard.setName(name.getText().toString());
				newCard.setId(id.getText().toString());

				int size = rosterEntries.size();

				if (newCard.getName().isEmpty() || newCard.getId().isEmpty()) {
					isEntryAdded = false;
				} else {
					if (isDuplicate(newCard)) {
						isEntryAdded = false;
						showAddDialog(v);
						showDuplicateAlert();
					} else {
						// isEntryAdded = true;
						rosterEntries.add(newCard);
						try {
							
							saveData(rosterEntries);
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

	public void showDuplicateAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.id_exists_message).setTitle(
				R.string.id_exists_title);
		AlertDialog alert = builder.create();

		alert.show();
	}

	public void callDialog(View view) {
		//SaveNewRosterDialog dial = new SaveNewRosterDialog(this);
		AlertDialog.Builder dialBuilder = new AlertDialog.Builder(view.getContext());
		dialBuilder.setMessage("Your changes have been saved automatically. Do you want to add more entries or exit?")
		.setPositiveButton(R.string.exit_add_entry, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context,JumpInMain.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);finish();
				aDial.dismiss();
			}
		})
		.setNegativeButton(R.string.add_roster_entry, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				aDial.dismiss();
			}
		});
		 aDial = dialBuilder.create();
		aDial.requestWindowFeature(Window.FEATURE_NO_TITLE);
		aDial.show();
	}
	
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

}
