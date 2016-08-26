/**
 * 
 */
package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

/**
 * @author abhinav
 * 
 */
public class LibTrial extends Activity implements CvCameraViewListener2 {
	private static final String TAG = null;
	private Hashtable<String, ArrayList<Integer>> tempAns = new Hashtable<String, ArrayList<Integer>>();
	int frameCount = 0;
	Hashtable<String, Integer> refinedAnswers;
	CameraBridgeViewBase mOpenCvCameraView;
	ArrayList<String> presentList;
	ArrayList<String> missingList;
	ArrayList<Integer> lastStats;
	ArrayList<String> quesSequence;
	private Hashtable<String, ArrayList<String>> finalAns = new Hashtable<String, ArrayList<String>>();
	private boolean polling = false;
	List<QSetCards> questCards;
	Integer quizQuestCount = 0; 
	KeyValues keyVal;
	String questionFile;
	String sessionName;
	String studentStat;
	String pollStat;
	Dialog dial;
	boolean loadFirstQuest = true;
	// Quiz variables
	TextView missingEntriesView;
	TextView optA;
	TextView optB;
	TextView optC;
	TextView optD;
	Button next;
	Button finish;
	Button analyse;

	// ///////////////
	

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// setContentView(R.layout.take_camera_attendance);
		setRosterQuizLayout();
		keyVal = new KeyValues(getApplicationContext(),"globals");
		if(keyVal.getQSetBool()){
			questionFile = keyVal.getQSet();
		}else{
			questionFile = null;
		}
		try {
			sessionName = getPollName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		quesSequence = new ArrayList<String>();
		questCards = createQuestionList();
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.display_java_surface_view);
		mOpenCvCameraView.setMaxFrameSize(2000, 2000);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setMinimumWidth(900);
		mOpenCvCameraView.setCvCameraViewListener(this);
		// showQuesDialog(mOpenCvCameraView);
	}

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				mOpenCvCameraView.enableView();
			}
				break;

			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mOpenCVCallBack);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		QCardsLibrary qCardsLib = new QCardsLibrary(inputFrame.rgba(), tempAns);
		Mat drawIt = qCardsLib.processIt(inputFrame.gray(), inputFrame.rgba());
		tempAns = qCardsLib.getTempAnswers();
		ArrayList<QCard> qCardList = qCardsLib.getQCardsList(tempAns);
		//Log.d(TAG, "Check for QCards object here");
		if (frameCount % 60 == 0) {
		//	prepareFinalAnswerTable();
		}
		frameCount++;
		return drawIt;
	}
	
	public void prepareFinalAnswerTable(){
		
		QCardsLibrary qCardsLib = new QCardsLibrary(tempAns);
		refinedAnswers = qCardsLib.getRefinedAnswers(tempAns);
		try {
			// presentList = qCardsLib.getPresentList();
			if(refinedAnswers!=null && finalAns!=null){
			finalAns = qCardsLib.getQuizFinalAnswers(refinedAnswers);
			}
			presentList = FileOperations.getPresentList(this);
			lastStats = qCardsLib.getLastQuestionStats();
			Hashtable<String, ArrayList<String>> namedRecords = qCardsLib.getNamedRecords();
			///FileOperations.saveNamedRecords(namedRecords);
			tempAns.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void startStopPoll(View view){
		if(!polling){
			/*if(loadFirstQuest){
				showQuesDialog(view);
				loadFirstQuest = false;
			}*/
			showQuesDialog(view);
			//polling= true;
		//	tempAns.clear();
			next.setText("Stop poll and view results");
		}else{
			prepareFinalAnswerTable();
			//studentStat = createStudentLogs(finalAns);
			//pollStat = createPollLogs(lastStats);
			drawChart(view);
			polling=false;
		}
		
	}
	
	

	public void drawChart(View view){
		final BarChart chart;
		// //////////////////////////////////////////////
		final View v = view;
		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		View dialogView = inflater.inflate(R.layout.chart_dialog, null);
		builder.setView(dialogView)
				// Add action buttons
				.setPositiveButton(R.string.show_next_quiz_ques,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								quizQuestCount++;
								if (quizQuestCount < questCards.size()) {
									showQuesDialog(v);
									//polling = false;
								} else {
									quizFinishedDialog(v);
								}
							}
						})
				.setNegativeButton(R.string.finish_poll_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								//finishPoll(v);
							}
						})
				.setNeutralButton(R.string.ask_again,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								polling = false;
								showQuesDialog(v);
							}
						});

		chart = (BarChart) dialogView.findViewById(R.id.chart);
		// //////////////////////////
		chart.setTouchEnabled(true);
		chart.setPinchZoom(true);
		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		int x = 0;
		if(!lastStats.isEmpty()) {
			 a = lastStats.get(0);
			 b = lastStats.get(1);
			 c = lastStats.get(2);
			 d = lastStats.get(3);
			 x = lastStats.get(4);
		}
		

		ArrayList<BarEntry> valsComp1 = new ArrayList<BarEntry>();
		if (!lastStats.isEmpty()) {
			int labelInt = 0;
			BarEntry c1e1 = new BarEntry(a, 0);
			BarEntry c1e2 = new BarEntry(b, 1);
			BarEntry c1e3 = new BarEntry(c, 2);
			BarEntry c1e4 = new BarEntry(d, 3);
			BarEntry c1e5 = new BarEntry(x, 4);

			if (a != 0 || b != 0 || c != 0 || d != 0) {

				labelInt = 1;
			}
			valsComp1.add(c1e1);
			valsComp1.add(c1e2);
			valsComp1.add(c1e3);
			valsComp1.add(c1e4);
			valsComp1.add(c1e5);

			BarDataSet setComp1 = new BarDataSet(valsComp1, "Responses");
			setComp1.setColors(new int[] { R.color.chart_a, R.color.chart_b,
					R.color.chart_c, R.color.chart_d, R.color.chart_x },
					view.getContext());
			ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
			dataSets.add(setComp1);

			ArrayList<String> xVals = new ArrayList<String>();
			xVals.add("A");
			xVals.add("B");
			xVals.add("C");
			xVals.add("D");
			xVals.add("None");

			BarData data = new BarData(xVals, dataSets);

			chart.setData(data);
			XAxis xAxis = chart.getXAxis();

			if (labelInt == 1) {
				xAxis.setDrawLabels(true);
			} else {
				xAxis.setDrawLabels(false);
			}

			xAxis.setDrawAxisLine(true);
			chart.setDescription("");
			chart.invalidate(); // refresh
		}
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	
	private void showQuesDialog(View view) {
		dial = new Dialog(view.getContext());
		dial.setContentView(R.layout.quiz_dialog);

		Integer seq = quizQuestCount + 1;
		quesSequence.add(seq.toString());
		dial.setTitle("Question(" + seq.toString() + " of " + questCards.size()
				+ ")");
		// TextView numView = (TextView) dial.findViewById(R.id.quesNum);
		TextView aView = (TextView) dial.findViewById(R.id.opA);
		TextView bView = (TextView) dial.findViewById(R.id.opB);
		TextView cView = (TextView) dial.findViewById(R.id.opC);
		TextView dView = (TextView) dial.findViewById(R.id.opD);
		TextView qView = (TextView) dial.findViewById(R.id.quest);
		Button pollButton = (Button) dial.findViewById(R.id.eachPoll);
		if (quizQuestCount <= questCards.size()) {
			// numView.setText("Question");
			aView.setText(questCards.get(quizQuestCount).getA());
			bView.setText(questCards.get(quizQuestCount).getB());
			cView.setText(questCards.get(quizQuestCount).getC());
			dView.setText(questCards.get(quizQuestCount).getD());
			qView.setText(questCards.get(quizQuestCount).getQuestion());
		}
		// Increment the question number

		// final EditText fName = (EditText)
		// dial.findViewById(R.id.newQSetName);
		pollButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tempAns.clear();
				polling = true;
			//	startStopPoll(v);

				dial.dismiss();

			}
		});
		dial.show();
	}
	
	public String getPollName() throws IOException {
		String qn1 = keyVal.getQSet();

		String qn2 = null;
		if (keyVal.getRosterQuizBool()) {
			qn2 = keyVal.getAttendanceRoster();
		} else {
			qn2 = "null";
		}

		Calendar c = Calendar.getInstance();
		Integer daySuf = c.get(Calendar.DAY_OF_MONTH);
		Integer monthSuf = c.get(Calendar.MONTH);
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
				+ "/QCards/PollResults/");
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/PollResults/" + quizName + ".csv");
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
	
	public void callQuizSave(View view, String content, String studentStats) {
		final String studentLogs = studentStats;
		final String pollLogs = content;
		final View v = view;
		
		
			try {
				saveNewPollFile(studentLogs, pollLogs, sessionName);
				Intent i = new Intent(v.getContext(), JumpInMain.class);
				// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(i);
				finish();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void saveNewPollFile(String studentData, String content,
			String fileName) throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		File studentDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/PollResults");
			studentDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/StudentStats");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					
				}
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				fos.close();
			} else {
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				fos.close();
			}

			if (!studentDirectory.exists()) {
				if (studentDirectory.mkdirs()) {
				}
				File saveIt = new File(studentDirectory, fileName + "csv");
				fos = new FileOutputStream(saveIt);
				fos.write(studentData.getBytes());
				fos.close();
			} else {
				File saveIt = new File(studentDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(studentData.getBytes());
				fos.close();
			}
		} else {
		}
	}

	private void quizFinishedDialog(View view) {
		
		final Dialog finishDialog = new Dialog(view.getContext());
		finishDialog.setContentView(R.layout.quiz_finished_dialog);
		finishDialog.setTitle("Questions Over");
		TextView finText = (TextView) finishDialog
				.findViewById(R.id.finishView);
		finText.setText("You have asked all the questions, do you want to end quiz and save results?");
		Button finBut = (Button) finishDialog.findViewById(R.id.finishButton);
		finBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//finishPoll(v);
				// finish();
				next.setVisibility(View.GONE);
				finishDialog.dismiss();
				finish();
			}
		});
		finishDialog.show();

	}
	
	// This method fetches questions from the current question set
	public ArrayList<QSetCards> createQuestionList() {
		ArrayList<QSetCards> questionList = new ArrayList<QSetCards>();

		try {
			BufferedReader br = qsetReader(questionFile);
			ReadRoster loadRoster = new ReadRoster(br, 4, ",");

			try {
				questionList = loadRoster.getQuestionCards();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionList;

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
			FileInputStream fis = new FileInputStream(qsetFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
		}
		return bufRead;
	}
	
	
	private void setRosterAttendanceLayout() {
		setContentView(R.layout.take_camera_attendance);
	}

	private void setGeneralAttendanceLayout() {
		setContentView(R.layout.attendance_without_roster);
	}

	private void setGeneralQuickPollLayout() {
		setContentView(R.layout.quick_poll);
		optA = (TextView) findViewById(R.id.optionA);
		optB = (TextView) findViewById(R.id.optionB);
		optC = (TextView) findViewById(R.id.optionC);
		optD = (TextView) findViewById(R.id.optionD);
		next = (Button) findViewById(R.id.next_button);
		finish = (Button) findViewById(R.id.finish_button);
		analyse = (Button) findViewById(R.id.analyse_button);
		missingEntriesView = (TextView) findViewById(R.id.missingEntries);
		analyse.setVisibility(View.GONE);
		missingEntriesView.setVisibility(View.GONE);
	}

	private void setRosterQuickPollLayout() {

	}

	private void setRosterQuizLayout() {
		setContentView(R.layout.quick_poll);
		missingEntriesView = (TextView) findViewById(R.id.missingEntries);
		optA = (TextView) findViewById(R.id.optionA);
		optB = (TextView) findViewById(R.id.optionB);
		optC = (TextView) findViewById(R.id.optionC);
		optD = (TextView) findViewById(R.id.optionD);
		next = (Button) findViewById(R.id.next_button);
		finish = (Button) findViewById(R.id.finish_button);
		analyse = (Button) findViewById(R.id.analyse_button);
		analyse.setVisibility(View.GONE);
		finish.setVisibility(View.VISIBLE);
		// next.setVisibility(View.GONE);
		next.setText("Ask First Question");
	}

	private void setGeneralQuizLayout() {
		setContentView(R.layout.quick_poll);
		missingEntriesView = (TextView) findViewById(R.id.missingEntries);
		optA = (TextView) findViewById(R.id.optionA);
		optB = (TextView) findViewById(R.id.optionB);
		optC = (TextView) findViewById(R.id.optionC);
		optD = (TextView) findViewById(R.id.optionD);
		next = (Button) findViewById(R.id.next_button);
		finish = (Button) findViewById(R.id.finish_button);
		analyse = (Button) findViewById(R.id.analyse_button);
		analyse.setVisibility(View.GONE);
		finish.setVisibility(View.VISIBLE);
		// next.setVisibility(View.GONE);
		next.setText("Start Quiz");
	}

	public void setLayout() {

	}

}
