package com.abhinav.qcards;

import java.io.IOException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class JumpInMain extends ActionBarActivity {
	MenuItem itemRoster;
	AlertDialog dialBox = null;
	Menu menu;
	KeyValues keyVal;
	public static boolean fromJumpIn = false;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*try{
			int[] arr = new int[1];.
			int a = arr[1];
			}catch(Exception e){
			    			    
				try {
					PrintWriter pw =  new PrintWriter(new FileOutputStream(Environment.getExternalStorageDirectory()
							+ "/QCards/TempResults/PollResults/Log"));
					e.printStackTrace(pw);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}     
			    
			}*/
		IDMap map = new IDMap(this);
		try {
			map.populateIdMap();
			map.populateSampleQSet();
			map.populateSampleRosters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		keyVal = new KeyValues(getApplicationContext(), "globals");
		setContentView(R.layout.jump_in);

		setFromJumpIn(true);
	}

	private void setFromJumpIn(Boolean bool) {
		fromJumpIn = bool;
	}

	public static Boolean isFromJumpIn() {
		return fromJumpIn;
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

	// /////////////////////////////////////////////////////////////////////////
	/*
	 * / /* All methods below are for various button clicks. /
	 */
	// ////////////////////////////////////////////////////////////////////////
	public void startQuickPoll(View view) {
		Toast.makeText(view.getContext(),"This feature is currently disabled. Try Quiz",Toast.LENGTH_SHORT).show();
		//throw new RuntimeException("This is a crash");
		//Intent cam = new Intent(this, QuickPoll.class);
		//startActivity(cam);
	}

	public void startQuiz(View view) {
		if (keyVal.getAttendanceBool()) {
			preQuizDialog(view);
		} else {
			Intent i = new Intent(view.getContext(), QuizChoice.class);
			// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
		}

	}

	public void startAttendance(View view) {
		if (keyVal.getAttendanceRoster() != null) {
			if (keyVal.getRosterAttendanceBool()) {
				preAttDialog(view);
			} else {
				Intent cam = new Intent(view.getContext(),
						AttendanceChoice.class);
				// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(cam);
			}
		} else {
			Intent cam = new Intent(view.getContext(), AttendanceChoice.class);
			// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(cam);
		}

		setFromJumpIn(true);
	}

	public void manageRoster(View view) {
		keyVal.setManageBool(true);
		keyVal.setRosterQuizBool(false);
		keyVal.setRosterAttendanceBool(false);
		Intent i = new Intent(this, RosterList.class);
		startActivity(i);
	}

	public void analysis(View view) {
		Toast.makeText(view.getContext(),"This feature is currently disabled. Try Quiz",Toast.LENGTH_SHORT).show();
	}

	public void onBackPressed(){
		super.onBackPressed();
		Intent i = new Intent(this,QCardsActivity.class);
		fromJumpIn = true;
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	public void preAttDialog(View view) {
		final View v = view;

		AlertDialog preAttendanceDialog = null;
		if (keyVal.getRoster() != null) {

			AlertDialog.Builder preAtt = new AlertDialog.Builder(
					view.getContext());
			preAtt.setMessage(
					getString(R.string.pre_att_start) + " "
							+ keyVal.getAttendanceRoster()
							+ getString(R.string.pre_att_end))
					.setPositiveButton(R.string.pre_att_pos,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									Intent startAttendanceIntent = new Intent(v
											.getContext(),
											TakeCameraAttendance.class);
									/*
									 * startAttendanceIntent
									 * .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
									 * );
									 */
									startActivity(startAttendanceIntent);

								}
							})
					.setNegativeButton(R.string.pre_att_neg,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent cam = new Intent(v.getContext(),
											AttendanceChoice.class);
									// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									startActivity(cam);
								}
							});

			preAttendanceDialog = preAtt.create();
			preAttendanceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			preAttendanceDialog.setCancelable(true);
			preAttendanceDialog.show();
		}

		else {
			Intent cam = new Intent(v.getContext(), AttendanceChoice.class);
			// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(cam);
		}
	}

	public void preQuizDialog(View view) {
		final View v = view;
		AlertDialog preAttendanceDialog = null;
		if (keyVal.getAttendanceRoster() != null) {

			AlertDialog.Builder preAtt = new AlertDialog.Builder(
					view.getContext());
			preAtt.setMessage(
					getString(R.string.pre_att_start)
							+ keyVal.getAttendanceRoster()
							+ getString(R.string.pre_att_end))
					.setPositiveButton(R.string.pre_att_pos,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									/*
									 * String forActivity = "Quiz"; Intent cam =
									 * new Intent(this, QuizChoice.class);
									 * cam.putExtra("targetActivity",
									 * forActivity); startActivity(cam);
									 */
									keyVal.setRosterQuizBool(true);

									Intent i = new Intent(v.getContext(),
											QSetList.class);
									// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									startActivity(i);

								}
							})
					.setNegativeButton(R.string.pre_att_neg,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									keyVal.setAttendanceRoster(null);
									keyVal.setAttendanceBool(false);
									Intent i = new Intent(v.getContext(),
											QuizChoice.class);
									// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									startActivity(i);
								}
							});

			preAttendanceDialog = preAtt.create();
			preAttendanceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			preAttendanceDialog.setCancelable(true);
			preAttendanceDialog.show();
		}

		else {
			Intent cam = new Intent(v.getContext(), AttendanceChoice.class);
			// cam.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(cam);
		}
	}
}
