/*
 * 
 */
package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

@SuppressLint("NewApi")
public class Quiz extends Activity implements CvCameraViewListener2 {
	private static final String TAG = null;
	private CameraBridgeViewBase mOpenCvCameraView;

	long entryTime = 0;
	long startTime = 0;
	Long elapsedFromStart = (long) 0;
	StringBuilder timeLog = new StringBuilder();

	StringBuilder tempPollData = new StringBuilder();
	StringBuilder tempStudentData = new StringBuilder();

	TextView details;
	Mat mRgba;
	long cdet = 0;
	int mHeight;
	int mWidth;
	ImageView pic;
	private String sessionName = null;
	Hashtable<String, String> pNameId = new Hashtable<String, String>();

	KeyValues keyVal;

	String questionFile = null;

	ArrayList<Integer> finalFound = new ArrayList<Integer>();

	Dialog dial = null;

	public boolean dialogOn = false;
	boolean pollFinished = false;

	int validPoll = 0;
	int prePoll = 0;

	//TextViews and Buttons
	TextView missingEntriesView;
	TextView optA;
	TextView optB;
	TextView optC;
	TextView optD;
	Button next;
	Button finish;
	Button analyse;
	Button logButton;

	// Activity Context
	final Context context = this;

	// Settings boolean
	public boolean highlightCardBool = true;
	public boolean showMissingBool = true;
	public boolean showBinarizeBool = false;
	public boolean applyRosterBool = true;
	public boolean applyQsetBool = true;
	public boolean adaptiveBool = false;

	// Question set
	List<QSetCards> questCards;

	Integer quizQuesCount = 0;

	private boolean isSaved = false;

	private ArrayList<String> foundCards = new ArrayList<String>();
	private ArrayList<Integer> cumulativeOptions = new ArrayList<Integer>();

	// Do not clear
	public ArrayList<String> presentList;

	// Do not clear
	Hashtable<Integer, ArrayList<Integer>> studentRecords = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<String, ArrayList<Integer>> namedRecords = new Hashtable<String, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> questionStats = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<String, ArrayList<Integer>> correctStats = new Hashtable<String,ArrayList<Integer>>();
	ArrayList<Integer> quesOrder = new ArrayList<Integer>();
	Integer correctAns;
	Integer correctCount;
	Integer incorrectCount;
	
	int qid = 0;
	int qDup = 0;

	//essential parameters for contour detection
	List<Double> areas = new ArrayList<Double>();
	List<Double> per = new ArrayList<Double>();
	List<Double> ang = new ArrayList<Double>();
	List<Double> aprat = new ArrayList<Double>();
	List<Point> centres = new ArrayList<Point>();
	List<Double> rectHeight = new ArrayList<Double>();
	List<Double> rectWidth = new ArrayList<Double>();
	List<Size> rectSize = new ArrayList<Size>();
	List<Double> xList = new ArrayList<Double>();
	List<Double> yList = new ArrayList<Double>();
	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	Hashtable<Integer, ArrayList<Integer>> cardsTable = new Hashtable<Integer, ArrayList<Integer>>();
	List<Double> idX = new ArrayList<Double>();
	List<Double> idY = new ArrayList<Double>();
	List<Point> holes = new ArrayList<Point>();
	
	Hashtable<Integer, Integer> idDecode = new Hashtable<Integer, Integer>();
	Hashtable<Integer,ArrayList<Integer>> ansMapping = new Hashtable<Integer,ArrayList<Integer>>();
	
	private Integer frameCount = 1;
	Hashtable<Integer, ArrayList<Integer>> tempAns = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, Integer> finalAns = new Hashtable<Integer, Integer>();
	
	
	// This is standard OpenCV call and has to be included
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
	protected void onCreate(Bundle savedInstanceState) {
		//keyVal object is used to access and modify SharedPreferences
		//the preference name is "globals"
		keyVal = new KeyValues(getApplicationContext(), "globals");
		
		Intent intent = getIntent();
		
		// get current Question Set file name passed from the last activity
		questionFile = intent.getExtras().getString("qFile");

		// Get questions from the question set file by calling createQuestionList method
		questCards = createQuestionList();
		try {
			//populate presentList with the IDs of the present students
			getPresentList();
			//populate pNameId list with the name and ids of the present students
			getPresentNameId();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		super.onCreate(savedInstanceState);

		//set flag to keep the screen on while quiz is going on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//set the quiz layout as defined in the quick_poll layout file
		setContentView(R.layout.quick_poll);

		// get session name. All files related to this session are stored with
		// this name
		try {
			sessionName = getTempSessionName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Initialising layout components
		missingEntriesView = (TextView) findViewById(R.id.missingEntries);
		logButton = (Button) findViewById(R.id.logButton);
		optA = (TextView) findViewById(R.id.optionA);
		optB = (TextView) findViewById(R.id.optionB);
		optC = (TextView) findViewById(R.id.optionC);
		optD = (TextView) findViewById(R.id.optionD);
		next = (Button) findViewById(R.id.next_button);
		finish = (Button) findViewById(R.id.finish_button);
		analyse = (Button) findViewById(R.id.analyse_button);
		analyse.setVisibility(View.GONE);
		finish.setVisibility(View.VISIBLE);
		next.setText("Start Quiz");

		//OpenCV java surface view settings
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.display_java_surface_view);
		mOpenCvCameraView.setMaxFrameSize(2000, 2000);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		try {
		//load ID map from the QCards folder using loadMapping method in MappingReader class
		Hashtable<Integer,ArrayList<Integer>> idMapping = MappingReader.loadMapping();
		
		//prepare Id and Ans mapping to be used later for internal card-id and answer decoding
		prepareAnsMap(idMapping);
		prepareIdMap(idMapping);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Orientations w,x,y and z code for different options for different cards
	 * This method loads the mapping of w,x,y,z to option number 1,2,3,4 for
	 * all cards as specified in the idMapping table populated by loadMapping method
	 * called in the onCreate method.
	 * 
	 * Populates ansMapping table to be used during the quiz
	 *
	 * @param idMapping the idMapping Hashtable
	 */
	public void prepareAnsMap(Hashtable<Integer,ArrayList<Integer>> idMapping){
		Set<Integer> idMapKeys = idMapping.keySet();
		Iterator<Integer> it = idMapKeys.iterator();
		while(it.hasNext()){
			ArrayList<Integer> al = new ArrayList<Integer>();
			ArrayList<Integer> ansml = new ArrayList<Integer>();
			al = idMapping.get(it.next());
			Integer mapKey = al.get(0);
			for(int i =1; i<al.size();i++){
				ansml.add(al.get(i));
			}
			ansMapping.put(mapKey, ansml);
		}
	}
	
	/**
	 * The 9-bit decoded ID is mapped to a different card id
	 * 
	 * This method populates the idDecode table to be used later
	 */
	public void prepareIdMap(Hashtable<Integer,ArrayList<Integer>> idMapping){
		Set<Integer> idMapKeys = idMapping.keySet();
		Iterator<Integer> it = idMapKeys.iterator();
		while(it.hasNext()){
			Integer key = it.next();
			Integer value = idMapping.get(key).get(0);
			idDecode.put(key, value);
		}
	}
	
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

	public void onCameraViewStarted(int width, int height) {

	}

	@Override
	public void onCameraViewStopped() {
		if (dialogOn == true) {
			dial.dismiss();
		}
		if (mRgba != null) {
			mRgba.release();
		}
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mOpenCVCallBack);
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		Mat rgbaMat = inputFrame.rgba();
		// Mat newMat = new Mat(rgbaMat.cols(),rgbaMat.rows(), CvType.CV_16UC4);
		Mat grayMat = inputFrame.gray();
		if (rgbaMat != null && grayMat != null) {
			frameCount++;
			areas.clear();
			per.clear();
			ang.clear();
			aprat.clear();
			centres.clear();
			rectHeight.clear();
			rectWidth.clear();
			rectSize.clear();
			xList.clear();
			yList.clear();
			contours.clear();
			cardsTable.clear();
			idX.clear();
			idY.clear();
			holes.clear();

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showMissing();
				}
			});

			if (!showBinarizeBool) {

				Mat resultMat = processIt(grayMat, rgbaMat);
				return resultMat;
			} else {
				Mat filtered = rgbaMat;
				filtered = filterIt(grayMat);
				Mat newMat = new Mat(rgbaMat.width(), rgbaMat.height(),
						CvType.CV_8UC4);
				Imgproc.cvtColor(filtered, newMat, Imgproc.COLOR_GRAY2RGBA);
				return newMat;
			}
		} else {
			return inputFrame.rgba();
		}
	}

	/**
	 * Show missing.
	 */
	public void showMissing() {
		ArrayList<String> missingList = new ArrayList<String>();
		if (showMissingBool) {
			Iterator<String> it = presentList.iterator();
			while(it.hasNext()){
				String pId = it.next();
				if(tempAns.containsKey(Integer.parseInt(pId)) || finalAns.containsKey(Integer.parseInt(pId))){
					
				}else{
					missingList.add(pId);
				}
			}
			
			StringBuilder missingBuilder = new StringBuilder();
			Iterator<String> p2Iterator = missingList.iterator();
			while (p2Iterator.hasNext()) {
				String aId = p2Iterator.next();
				missingBuilder.append(aId);
				missingBuilder.append(",");
			}

			String missText = missingBuilder.toString();
			missingEntriesView.setText(missText);
		} else {
			missingEntriesView.setText("Not showing missing entries");
		}
	}
	
	/**
	 * Load question list from the current question set.
	 * Use getQuestionCards method from ReadRoster class if reading a csv file
	 * Use getQuestionCards method from TextReader class if reading a txt file
	 *
	 * @return the array list containing question card objects
	 */
	public ArrayList<QSetCards> createQuestionList() {
		ArrayList<QSetCards> questionList = new ArrayList<QSetCards>();

		try {
			BufferedReader br = qsetReader(questionFile);
			if (questionFile.endsWith(".csv")) {
				ReadRoster loadRoster = new ReadRoster(br, 5, ",");

				try {
					questionList = loadRoster.getQuestionCards();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (questionFile.endsWith(".txt")) {
				TextReader tr = new TextReader(br);
				questionList = tr.getQuestionCards();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionList;
	}

	/**
	 * Read the specified questionSet file and return BufferedReader object of the .
	 * contents for further processing
	 *
	 * @param file_Name the name of the file to be read
	 * @return the buffered reader
	 * @throws FileNotFoundException the file not found exception
	 */
	public static BufferedReader qsetReader(String file_Name)
			throws FileNotFoundException {
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {
			Log.d(TAG,"External Storage not available");
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

	/**
	 * Log all.
	 * This method is just for performance estimates and not used in the app
	 *
	 * @param view the view
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void logAll(View view) throws IOException {

		File logDir = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/Logs");
		if (!logDir.exists()) {
			if (logDir.mkdirs()) {
				Log.e(TAG, "Directory Created");
			}

			// fos.close();
			// Log.e(TAG, "Directory Created");

		}
		Long time = System.currentTimeMillis();
		File saveIt = new File(logDir, time.toString() + ".csv");
		FileOutputStream fos = new FileOutputStream(saveIt);
		String content = timeLog.toString();
		fos.write(content.getBytes());
		logButton.setText("Logged");

	}

	
	/**
	 * processIt method.
	 * This is where all those methods are called that are required for
	 * processing the image. First the image is filtered by calling filterIt
	 * method and then contours are detected and results are drawn using
	 * detectBlack method
	 * 
	 * @param grayMat the grayscale matrix of the camera feed
	 * @param rgbaMat the rgba matrix of the camera feed
	 * @return the rgba matrix with additional option and highlight for each card
	 */
	protected Mat processIt(Mat grayMat, Mat rgbaMat) {

		Mat grayM = grayMat;
		Mat colorM = rgbaMat;

		// Get a Filtered image(Otsu_Threshhold followed by Binary Inversion)
		Mat filtered = filterIt(grayM);

		// detectBlack method detects the contours and calculates the required parameters
		Mat detectResults = detectBlack(filtered, colorM);

		//do not return a null matrix, the app will crash
		if (detectResults != null) {
			return detectResults;
		} else {
			return colorM;
		}

	}

	/**
	 * filterIt method filters the input matrix.
	 *
	 * @param grayMat the gray mat
	 * @return the mat
	 * 
	 */
	public Mat filterIt(Mat grayMat) {
		Mat grey = grayMat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		/*
		 * if Adaptive threshold option is selected, apply adaptive threshold every
		 * third frame. On other frames, use normal Binary and Otsu
		 *  
		 * If Adaptive threshold is not selected, try variable threshold values for 
		 * successive frames. This will improve detection efficiency without slowing 
		 * down the app unlike the adaptive threshold
		*/
		if (adaptiveBool) {
			if(frameCount%3 == 0){
			Imgproc.adaptiveThreshold(tmp1, tmp1, 255,
					Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV,
					11, 2);
			}else{
				Imgproc.threshold(tmp2, tmp2, 120, 255, Imgproc.THRESH_BINARY
						+ Imgproc.THRESH_OTSU);
				Imgproc.threshold(tmp2, tmp1, 0, 255, Imgproc.THRESH_BINARY_INV);
			}
		} else {
			if(frameCount%3==0){
				Imgproc.threshold(tmp2, tmp2, 120, 255, Imgproc.THRESH_BINARY
						+ Imgproc.THRESH_OTSU);
			}else if(frameCount%3==1){
				Imgproc.threshold(tmp2, tmp2, 80, 255, Imgproc.THRESH_BINARY
						+ Imgproc.THRESH_OTSU);
			}else if(frameCount%3==2){
				Imgproc.threshold(tmp2, tmp2, 40, 255, Imgproc.THRESH_BINARY
						+ Imgproc.THRESH_OTSU);
			}
			Imgproc.threshold(tmp2, tmp1, 0, 255, Imgproc.THRESH_BINARY_INV);
		}

		//Do not return a null matrix, app will crash
		if (tmp1 != null) {
			return tmp1;
		} else {
			return grey;
		}

	}

	
	/**
	 * Detect black.
	 *
	 * @param filtered the filtered
	 * @param colorMat the color mat
	 * @return the mat
	 */
	private Mat detectBlack(Mat filtered, Mat colorMat) {
	
		Mat hierarchy = new Mat();

		// mContours list will hold all the detected contours
		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		
		// tempContours will hold the list of useful contours
		List<MatOfPoint> tempContours = new ArrayList<MatOfPoint>();

		// finding contours
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);
		
		for (int i = 0; i < mContours.size(); i++) {
			// still not clear how hierarchy worked
			double[] hVal = hierarchy.get(0, i);
			// 3rd entry in hierarchy is child contour -
			// http://docs.opencv.org/master/d9/d8b/tutorial_py_contours_hierarchy.html
			int childId = properCastX(hVal[2]);
			
			/*
			 * if the contour contains a child contour, its of our interest
			 * add both the parent and the child contour to tempContours in such case
			*/
			if (childId > -1) {
				// Add this contour to tempContours list if not present
				if (!tempContours.contains(mContours.get(i))) {
					tempContours.add(mContours.get(i));
				}
				// Add child to tempContours list if not present
				if (!tempContours.contains(mContours.get(childId))) {
					tempContours.add(mContours.get(childId));
				}
			}
		}

		Iterator<MatOfPoint> each1 = tempContours.iterator();
		// DO NOT forget to clear the useless contours list to free up memory
		mContours.clear();
		contours.clear();
		while (each1.hasNext()) {
			MatOfPoint wrapper = each1.next();
			MatOfPoint2f forrotatedrect = new MatOfPoint2f();
			MatOfPoint wrapRotate = wrapper;
			wrapRotate.convertTo(forrotatedrect, CvType.CV_32FC2);
			RotatedRect rotated = Imgproc.minAreaRect(forrotatedrect);
			
			// Calculate important parameters for contours

			// size contains width and height of the rotated rectangle
			Size sizeRect = rotated.size;
			if ((sizeRect.height / sizeRect.width) > 0.7
					&& (sizeRect.height / sizeRect.width) < 1.3) {
				if ((sizeRect.height * sizeRect.width) < 1.4 * Imgproc
						.contourArea(wrapper)) {
					double moment00 = Imgproc.moments(wrapper).get_m00();
					double moment01 = Imgproc.moments(wrapper).get_m01();
					double moment10 = Imgproc.moments(wrapper).get_m10();
					double centerX = moment10 / moment00;
					double centerY = moment01 / moment00;
					if (filtered
							.get(properCastY(centerY), properCastX(centerX))[0] == 0) {
						contours.add(wrapper);
						double peri = Imgproc.arcLength(forrotatedrect, true);
						per.add(peri);
						double area = Imgproc.contourArea(wrapper);
						areas.add(area);
						xList.add(centerX);
						yList.add(centerY);
						centres.add(new Point(centerX, centerY));
					}
				}
			}
		}

		// Mat mat = new Mat(filtered.cols(), filtered.rows(), CvType.CV_8UC4);
		// Imgproc.cvtColor(filtered, mat, Imgproc.COLOR_GRAY2RGB);
		Mat mat = colorMat;

		// finding the potential neighbours for each contour
		findNeighbors(mat);

		// Group the neighbours, identify options and draw contours
		try{
		mat = drawOnContours(mat, colorMat, filtered);
		}catch(NullPointerException e){
			System.out.println("Exception" +e);
		}
		if (mat != null) {
			return mat;
		} else {
			return colorMat;
		}
	}


	/**
	 * findNeighbors method groups the contours together.
	 * It iterates through the "centres" list that contain
	 * the center coordinates of the useful contours.
	 * 
	 * First the centers are sorted according to their x coordinate
	 * and then get a list of potential neighbors based on their proximity
	 * in the x direction as well as similarity in size.
	 * From the list of the potential neighbors, choose the neighbors based
	 * on the proximity in the y direction and populate the cardList
	 * Finally, for each cardList with more than two entries, add it
	 *  to the cardstable
	 *
	 * @param mat the mat
	 */
	private void findNeighbors(Mat mat) {

		Point[] arr = new Point[centres.size()];

		Long ss = System.nanoTime();
		centres.toArray(arr);
		// Sort the array based on the x coordinate of the centres
		// Since points contain both x and y values, we need comparator to sort
		// them according to x values
		Arrays.sort(arr, new Comparator<Point>() {
			public int compare(Point a, Point b) {
				int xComp = Double.compare(a.x, b.x);
				if (xComp == 0)
					return Double.compare(a.y, b.y);
				// try removing y comparison and check effects
				else
					return xComp;
			}
		});
		// get the ids of the contours in the sorted order
		int[] ids = new int[centres.size()];
		for (int i = 0; i < centres.size(); i++) {
			ids[i] = centres.indexOf(arr[i]);
		}

		int counter = 0;

		// rem will contain the ids that will be needed to be removed later
		ArrayList<Integer> rem = new ArrayList<Integer>();
		for (int i = 0; i < centres.size(); i++) {
			ArrayList<Integer> cardList = new ArrayList<Integer>();
			ArrayList<Point> tempList = new ArrayList<Point>();
			ArrayList<Integer> tempId = new ArrayList<Integer>();

			// scale = length of a side
			double scale = per.get(ids[i]) / 4;
			if (!rem.contains(ids[i])) {

				// Finding potential neighbor centers in x+ direction
				for (int j = i; j < centres.size()
						&& Math.abs(arr[i].x - arr[j].x) < 2 * scale; j++) {
					double iArea = areas.get(ids[i]);
					double jArea = areas.get(ids[j]);

					/*
					 * Area constraint important to check. It will eliminate too
					 * small or too big contours If area constraint is met, add
					 * the contour id to tempId and add the center to tempList
					 */
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}
				}

				// Finding potential neighbor centers in x- direction
				for (int j = i - 1; j > 0
						&& Math.abs(arr[i].x - arr[j].x) < 2 * scale; j--) {
					double iArea = areas.get(i);
					double jArea = areas.get(j);
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}

				}

				/*
				 * After the above two loops, we get a set of all potential
				 * neighboring contours for contour with id 'i' based  on the
				 * value of their x-coordinate. It can be imagined as a column in
				 * the image
				 */

				/*
				 * The 'for' loop below searches for the potential neighbors in
				 * the column obtained in the last two 'for' loops.
				 */

				for (Integer j = 0; j < tempList.size(); j++) {

					/*
					 * Checking in the column, if the y-coordinates are in
					 * permissible range then add the index of the contour to the
					 * cardList and also add it to the rem list so that we do not
					 * process it again
					 */
					if (Math.abs(arr[i].y - tempList.get(j).y) < 2 * scale) {
						cardList.add(centres.indexOf(tempList.get(j)));
						//After adding to the cardList, do not process the contour again
						rem.add(centres.indexOf(tempList.get(j)));
					}
				}

				/*
				 * Maintain a cardsTable to hold the cardLists. cardsTable holds
				 * only those cardLists which have more than 2 elements as we
				 * require at least three squares for a valid pattern.
				 */
				if (cardList.size() > 2) {
					cardsTable.put(counter, cardList);
					counter++;
				}
			}

		}
	}

	/**
	 * drawOnContours 
	 *
	 * @param mat the mat
	 * @param tmp the tmp is the color Matrix
	 * @param bnw the bnw is the filtered Matrix
	 * @return the mat
	 */
	private Mat drawOnContours(Mat mat, Mat tmp, Mat bnw) {
		Mat cardMat = tmp;
		Integer option = 0;
		double idcx = 0;
		double idcy = 0;
		float[] radius = new float[1];
		// eliminate4();
		if(cardMat!=null){
		for (Integer i = 0; i < cardsTable.size(); i++) {

			Point[] arr = new Point[3];

			Point p1 = centres.get(cardsTable.get(i).get(0));
			p1 = centerFix(p1, cardMat);
			arr[0] = p1;

			Point p2 = centres.get(cardsTable.get(i).get(1));
			p2 = centerFix(p2, cardMat);
			arr[1] = p2;

			Point p3 = centres.get(cardsTable.get(i).get(2));
			p3 = centerFix(p3, cardMat);
			arr[2] = p3;

			/*
			 * Find the centroid of the triangle formed by the p1,p2 and p3
			 */

			double cx = (p1.x + p2.x + p3.x) / 3;
			double cy = (p1.y + p2.y + p3.y) / 3;

			/*
			 * Find the intensity at p1, p2 and p3
			 */
			double c1 = bnw.get(properCastY(p1), properCastX(p1))[0];
			double c2 = bnw.get(properCastY(p2), properCastX(p2))[0];
			double c3 = bnw.get(properCastY(p3), properCastX(p3))[0];

			if (c1 == 0.0 && c2 == 0.0 && c3 == 0.0) {

				/*
				 * Draw a circle passing through all three points and get its
				 * center. Note that the center will be the midpoint of the
				 * hypotenuse of the triangle.
				 */
				MatOfPoint2f pMat = new MatOfPoint2f(arr);
				Point hole = new Point();
				Imgproc.minEnclosingCircle(pMat, hole, radius);

				int piv = 0;

				/*
				 * The combination of valX and valY will later be used to
				 * determine the option. Depending upon whether valX and valY
				 * are positive or negative, we get to know the relative
				 * position of the centroid with respect to the midpoint of the
				 * hypotenuse
				 */
				double valX = cx - hole.x;
				double valY = cy - hole.y;

				boolean right = false;
				Point pivot = new Point();

				/*
				 * Finding the pivot and checking whether the three points are
				 * at right angle or not not
				 */
				if ((hole.x - 2) < (p1.x + p3.x) / 2
						&& (p1.x + p3.x) / 2 < (hole.x + 2)
						&& (hole.y - 2) < (p1.y + p3.y) / 2
						&& (p1.y + p3.y) / 2 < (hole.y + 2)) {
					pivot = p2;
					piv = 2;
					hole.x = (p1.x + p3.x) / 2;
					hole.y = (p1.y + p3.y) / 2;

					right = findDot(pivot, p3, p1);
				}
				if ((hole.x - 2) < (p2.x + p3.x) / 2
						&& (p2.x + p3.x) / 2 < (hole.x + 2)
						&& (hole.y - 2) < (p2.y + p3.y) / 2
						&& (p2.y + p3.y) / 2 < (hole.y + 2)) {
					pivot = p1;
					piv = 1;
					hole.x = (p2.x + p3.x) / 2;
					hole.y = (p2.y + p3.y) / 2;
					p1 = p2;
					p2 = pivot;

					right = findDot(pivot, p1, p3);
				}
				if ((hole.x - 2) < (p2.x + p1.x) / 2
						&& (p2.x + p1.x) / 2 < (hole.x + 2)
						&& (hole.y - 2) < (p2.y + p1.y) / 2
						&& (p2.y + p1.y) / 2 < (hole.y + 2)) {
					pivot = p3;
					piv = 3;
					hole.x = (p2.x + p1.x) / 2;
					hole.y = (p2.y + p1.y) / 2;
					p3 = p2;
					p2 = pivot;
					right = findDot(pivot, p1, p3);

				}

				/*
				 * Calculating the x-distance between pivot-p1 and pivot-p3
				 */
				int p1p = (int) (pivot.x - p1.x);
				int p3p = (int) (pivot.x - p3.x);

				if (right) {

					Point i1 = new Point();
					Point i2 = new Point();
					Point i3 = new Point();
					Point i4 = new Point();
					Point i5 = new Point();
					Point i6 = new Point();
					Point i7 = new Point();
					Point i8 = new Point();

					/*
					 * hole will be the mid point of the id centre and pivot
					 * centre
					 */
					Point idc = new Point(2 * hole.x - pivot.x, 2 * hole.y
							- pivot.y);
					// Point idNew = centerFixID
					Double angle = (double) 0;
					Double theta = (double) 0;
					if (valX > 0 && valY > 0) {
						//option = 4;
						option = 1;
						/*
						 * swapping p1 and p3 to ensure that p3 is the left most
						 * anchor. This is important to ensure that ID is
						 * calculated correctly irrespective of rotation
						 */
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX > 0 && valY < 0) {
						//option = 1;
						option = 4;
						// optionCount[0]++;
						if (p1p < p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY > 0) {
						option = 2;
						//option = 4;
						// optionCount[2]++;
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY < 0) {
						//option = 2;
						option = 3;

					} else {
						option = 5;
					}

					Point m1p = new Point();
					Point m2p = new Point();
					int tempCol = 0;
					ArrayList<Integer> tempId = new ArrayList<Integer>();
					m1p.x = (pivot.x + p1.x) / 2;
					m1p.y = (pivot.y + p1.y) / 2;
					m2p.x = (pivot.x + p3.x) / 2;
					m2p.y = (pivot.y + p3.y) / 2;

					Point id1 = new Point((2 * hole.x - m1p.x),
							(2 * hole.y - m1p.y));
					Point id2 = new Point((2 * hole.x - m2p.x),
							(2 * hole.y - m2p.y));
					Point idc1 = new Point((2 * id2.x - p1.x),
							(2 * id2.y - p1.y));
					Point idc2 = new Point((2 * id1.x - p3.x),
							(2 * id1.y - p3.y));
					Point idAvg = new Point();

					idAvg.x = (idc.x + idc1.x + idc2.x) / 3;
					idAvg.y = (idc.y + idc1.y + idc2.y) / 3;

					Point a0 = new Point();
					Point a1 = new Point();
					Point a2 = new Point();
					Point a3 = new Point();
					Point a4 = new Point();
					Point a5 = new Point();
					Point a6 = new Point();
					Point a7 = new Point();
					// double[] tempCol1 = cardMat.get(945, 1313);
					a0.x = (id2.x + idAvg.x) / 2;
					a0.y = (id2.y + idAvg.y) / 2;
					// tempCol = (int) bnw.get((int)a0.y, (int)a0.x)[0];
					//tempId.add(tempCol);
					Integer idVal = 0;
					if(cardMat != null){
					tempCol = (int) cardMat.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);
					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					/*tempCol = (int) cardMat.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);*/

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) cardMat.get((int) a1.y, (int) a1.x)[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) cardMat.get((int) a2.y, (int) a2.x)[0];
					tempId.add(tempCol);

					a4.x = (2 * idAvg.x - a0.x);
					a4.y = (2 * idAvg.y - a0.y);

					a5.x = (2 * idAvg.x - a1.x);
					a5.y = (2 * idAvg.y - a1.y);

					a6.x = (2 * idAvg.x - a2.x);
					a6.y = (2 * idAvg.y - a2.y);

					a7.x = (2 * a6.x - a5.x);
					a7.y = (2 * a6.y - a5.y);

					a3.x = (2 * idAvg.x - a7.x);
					a3.y = (2 * idAvg.y - a7.y);

					tempCol = (int) cardMat.get((int) a3.y, (int) a3.x)[0];
					tempId.add(tempCol);

					tempCol = (int) cardMat.get((int) a4.y, (int) a4.x)[0];
					tempId.add(tempCol);

					tempCol = (int) cardMat.get((int) a5.y, (int) a5.x)[0];
					tempId.add(tempCol);

					tempCol = (int) cardMat.get((int) a6.y, (int) a6.x)[0];
					tempId.add(tempCol);

					tempCol = (int) cardMat.get((int) a7.y, (int) a7.x)[0];
					tempId.add(tempCol);
					
					tempCol = (int) cardMat.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);
				/*	tempCol = (int) cardMat.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);*/
					idVal = bin2dec(tempId);
					}
					tempId.clear();
					Integer mappedOption = 10;
					if(ansMapping.containsKey(idVal)){
					 mappedOption = ansMapping.get(idVal).get(option-1);
					}
					//option number to literal
					if (mappedOption >= 0) {
						String ansDisplay = null;
						switch (mappedOption) {
						case 0:
							ansDisplay = "A";
							break;
						case 1:
							ansDisplay = "B";
							break;
						case 2:
							ansDisplay = "C";
							break;
						case 3:
							ansDisplay = "D";
							break;
							default:
								ansDisplay = "X";
								mappedOption = 0;
								break;
						}

						/*
						 * logging the option detected for a particular id in
						 * each frame tempAns takes ID as key and an arraylist
						 * containing the count of options corresponding to that
						 * ID
						 */
						if (presentList.contains(idVal.toString())) {
							if (!tempAns.isEmpty()) {
								if (tempAns.containsKey(idVal)) {
									ArrayList<Integer> ansList = tempAns
											.get(idVal);
									ansList.set(mappedOption,
											ansList.get(mappedOption) + 1);
									tempAns.put(idVal, ansList);

									if (highlightCardBool) {
										Core.circle(mat, hole, (int) radius[0],
												new Scalar(50, 158, 33), -1);

										Core.putText(mat, ansDisplay, hole,
												Core.FONT_HERSHEY_SIMPLEX, 2,
												new Scalar(255, 255, 255), 3);

										Core.putText(mat, idVal.toString(),
												idAvg,
												Core.FONT_HERSHEY_SIMPLEX, 1,
												new Scalar(255, 0, 0), 2);
									}
								} else {
									/*
									 * If HashTable doesn't contain an entry for
									 * ID, then create an entry
									 */
									ArrayList<Integer> ansList = new ArrayList<Integer>();
									ansList.add(0);
									ansList.add(0);
									ansList.add(0);
									ansList.add(0);
									ansList.set(mappedOption,
											ansList.get(mappedOption) + 1);
									tempAns.put(idVal, ansList);

									if (highlightCardBool) {
										Core.circle(mat, hole, (int) radius[0],
												new Scalar(50, 158, 33), -1);

										Core.putText(mat, ansDisplay, hole,
												Core.FONT_HERSHEY_SIMPLEX, 1.5,
												new Scalar(255, 255, 255), 2);

										Core.putText(mat, idVal.toString(),
												idAvg,
												Core.FONT_HERSHEY_SIMPLEX, 1,
												new Scalar(255, 0, 0), 2);
									}
								}
							} else {
								ArrayList<Integer> ansList = new ArrayList<Integer>();
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.set(mappedOption,
										ansList.get(mappedOption) + 1);
								tempAns.put(idVal, ansList);

								if (highlightCardBool) {

									Core.circle(mat, hole, (int) radius[0],
											new Scalar(50, 158, 33), -1);
									Core.putText(mat, ansDisplay, hole,
											Core.FONT_HERSHEY_SIMPLEX, 2,
											new Scalar(255, 255, 255), 3);

									Core.putText(mat, idVal.toString(), idAvg,
											Core.FONT_HERSHEY_SIMPLEX, 1,
											new Scalar(255, 0, 0), 2);
								}
							}
							Long elapsedTime = System.nanoTime() - entryTime;
							// elapsedFromStart = System.nanoTime() - startTime;

							// Integer cards = cardsTable.size();
							timeLog.append(i.toString());
							timeLog.append(",");
							timeLog.append(idVal.toString());
							timeLog.append(",");
							timeLog.append(elapsedTime.toString());
							timeLog.append('\n');
						}
					}

					idX.add(idcx);
					idY.add(idcy);
					holes.add(hole);
				}

			}

		}
		/*Long elapsedTime = System.nanoTime() - entryTime;
		elapsedFromStart = System.nanoTime() - startTime;

		Integer cards = cardsTable.size();
		timeLog.append(cards.toString());
		timeLog.append(",");
		timeLog.append(elapsedTime.toString());*/
		}
		return mat;
	}

	/**
	 * Proper cast x.
	 *
	 * @param p the p
	 * @return the int
	 */
	public int properCastX(Point p) {
		double check = Math.ceil(p.x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	/**
	 * Proper cast y.
	 *
	 * @param p the p
	 * @return the int
	 */
	public int properCastY(Point p) {
		double check2 = Math.ceil(p.y);
		int checkY = (int) (check2 * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

	/**
	 * Proper cast x.
	 *
	 * @param x the x
	 * @return the int
	 */
	public int properCastX(double x) {
		double check = Math.ceil(x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	/**
	 * Proper cast y.
	 *
	 * @param y the y
	 * @return the int
	 */
	public int properCastY(double y) {
		double check = Math.ceil(y);
		int checkY = (int) (check * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

	/**
	 * Proper cast.
	 *
	 * @param p the p
	 * @return the point
	 */
	public Point properCast(Point p) {
		double check = Math.ceil(p.x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;
		double check2 = Math.ceil(p.y);
		int checkY = (int) (check2 * 10);
		int checkedY = checkY / 10;

		Point casted = new Point(checkedX, checkedY);
		return casted;
	}

	/**
	 * Find dot.
	 *
	 * @param pivot the pivot
	 * @param p1 the p1
	 * @param p2 the p2
	 * @return true, if successful
	 */
	private boolean findDot(Point pivot, Point p1, Point p2) {
		// TODO Auto-generated method stub
		double dP1PivotX = (p1.x - pivot.x) * (p1.x - pivot.x);
		double dP1PivotY = (p1.y - pivot.y) * (p1.y - pivot.y);
		double dP2PivotX = (p2.x - pivot.x) * (p2.x - pivot.x);
		double dP2PivotY = (p2.y - pivot.y) * (p2.y - pivot.y);
		Double dist1 = Math.sqrt(dP1PivotX + dP1PivotY);
		Double dist2 = Math.sqrt(dP2PivotX + dP2PivotY);
		double ang = Math.abs(Math.atan2(dist2, dist1) / 0.0174444444);
		if (ang >= 40 && ang <= 50) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * centerFix method fixes the center of detected contours to ensure that 
	 * it is equidistant from each side of the contour
	 *
	 * @param p1 the center 
	 * @param tmp the color matrix
	 * @return the point
	 */
	private Point centerFix(Point p1, Mat tmp) {
		double check = Math.ceil(p1.x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;
		int tempX = checkedX;
		double check2 = Math.ceil(p1.y);
		int checkY = (int) (check2 * 10);
		int checkedY = checkY / 10;
		int tempY = checkedY;
		int left = 0;
		int right = 0;
		int up = 0;
		int down = 0;
		int checkVal = 0;
		if (tmp.get(checkedY, checkedX)[0] == 0) {
			checkVal = 0;
		} else {
			checkVal = 1;
		}
		while (tmp.get(checkedY, checkedX)[0] == checkVal) {
			left++;
			checkedX -= 1;
		}
		checkedX = tempX;

		while (tmp.get(checkedY, checkedX)[0] == checkVal) {
			right++;
			checkedX += 1;
		}
		if (Math.abs(right - left) > 1) {
			checkedX = tempX;
			int pw = tempX + ((right - left) / 2);
			p1.x = pw;
		}

		while (tmp.get(checkedY, checkedX)[0] == checkVal) {
			up++;
			checkedY -= 1;
		}
		checkedY = tempY;

		while (tmp.get(checkedY, checkedX)[0] == checkVal) {
			down++;
			checkedY += 1;
		}
		if (Math.abs(up - down) > 1) {
			checkedY = tempY;
			int pw = tempY + ((down - up) / 2);
			p1.y = pw;
		}

		Point p = new Point(p1.x, p1.y);
		return p;
	}

	private int bin2dec(ArrayList<Integer> tmpId) {
		int sum = 0;
		for (int i = 0; i < tmpId.size(); i++) {
			int curBit = tmpId.get(tmpId.size() - i - 1);
			//change this threshold now
			if (curBit < 100) {
				sum += Math.pow(2, i);
			}

		}
		if(idDecode.containsKey(sum)){
			int decodedId = idDecode.get(sum);
			return decodedId;
			}else{
				return 0;
			}
	}
	
	

	// ////////// Wrote this method but never used it////////////
	public boolean goodId(Point idAvg, Point pivot, Point p1) {
		double pivx = pivot.x;
		double pivy = pivot.y;
		double idx = idAvg.x;
		double idy = idAvg.y;
		double p1x = p1.x;
		double p1y = p1.y;
		double sidex = (pivx - p1x) * (pivx - p1x);
		double sidey = (pivy - p1y) * (pivy - p1y);
		double hypx = (pivx - idx) * (pivx - idx);
		double hypy = (pivy - idy) * (pivy - idy);
		double modh = Math.sqrt(hypx + hypy);
		double mods = Math.sqrt(sidex + sidey);
		if (modh <= 1.5 * mods && modh >= 1.3 * mods) {
			return true;
		} else {
			return false;
		}
	}

	// //////////////No need to use this method /////////////////
	public void eliminate4() {
		// double min = Double.MAX_VALUE;

		for (int i = 0; i < cardsTable.size(); i++) {
			int count = cardsTable.get(i).size();
			if (count > 3) {
				double[] areaArr = new double[count];
				for (int j = 0; j < count; j++) {
					areaArr[j] = areas.get(cardsTable.get(i).get(j));
				}
				Arrays.sort(areaArr);
				List<Integer> remInd = new ArrayList<Integer>();
				for (int j = 0; j < count - 3; j++) {
					remInd.add(areas.indexOf(areaArr[j]));
				}

				for (int j = 0; j < remInd.size(); j++) {

					cardsTable.get(i).remove(remInd.get(j));

				}
			}
			Log.d("Tag1", "Hello");
		}

	}

	// ////////////No need to use this method////////////////////
	public Mat findPehchaan(Mat mat) {
		ang.clear();
		for (int i = 0; i < cardsTable.size(); i++) {
			int cIndex = cardsTable.get(i).get(0);
			MatOfPoint wrapper = contours.get(cIndex);
			MatOfPoint2f forrotatedrect = new MatOfPoint2f();
			MatOfPoint wrapRotate = wrapper;
			wrapRotate.convertTo(forrotatedrect, CvType.CV_32FC2);
			RotatedRect rotated = Imgproc.minAreaRect(forrotatedrect);
			double per = Imgproc.arcLength(forrotatedrect, true);
			Double ang = rotated.angle;
			double holex = holes.get(i).x;
			double holey = holes.get(i).y;
			double diffx = holex - idX.get(i);
			double diffy = holey - idY.get(i);
			double rad = Math.sqrt(diffx * diffx + diffy * diffy);
			double theta = 45;
			double rot = (135 + Math.abs(ang)) * 0.0174532925;
			// first rotation about the hole i.e about the midpoint of the
			// hypotenuse of the detected anchors
			double newX = Math.round(holex + rad * (Math.cos(rot)));
			double newY = Math.round(holey + rad * (Math.sin(rot)));

			Core.putText(mat, "o", new Point(holex, holey),
					Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(0, 255, 0), 2);

		}
		return mat;

	}


	/**
	 * Clean the mess.
	 */
	public void cleanTheMess() {
		correctCount = 0;
		incorrectCount = 0;
		frameCount = 1;
		areas.clear();
		per.clear();
		ang.clear();
		aprat.clear();
		centres.clear();
		rectHeight.clear();
		rectWidth.clear();
		rectSize.clear();
		xList.clear();
		yList.clear();
		contours.clear();
		cardsTable.clear();
		idX.clear();
		idY.clear();
		holes.clear();
		studentRecords.clear();
		tempAns.clear();
		finalAns.clear();
		cumulativeOptions.clear();
	}

	

	/**
	 * Map answers.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void mapAnswers() throws IOException {

		Set<Integer> keyAns = tempAns.keySet();
		Iterator<Integer> itAns = keyAns.iterator();
		if (keyAns.size() == 0) {
			finalAns.clear();
		}

		ArrayList<String> ansKeys = new ArrayList<String>();

		while (itAns.hasNext()) {
			Integer each = itAns.next();
			ansKeys.add(each.toString());
			ArrayList<Integer> ansList = tempAns.get(each);
			int max = 0;
			int maxIndex = 0;
			int sumOptions = 0;
			for (int j = 0; j < 4; j++) {
				if (ansList.get(j) >= max) {
					max = ansList.get(j);
					maxIndex = j;
				}
			}
			if (keyVal.getRosterQuizBool()) {
				getPresentList();
				if (presentList.contains(each.toString())) {

					String sName = pNameId.get(each.toString())+"/"+each.toString();
					finalAns.put(each, maxIndex);
					ArrayList<Integer> studentAnsList = new ArrayList<Integer>();
					if (namedRecords.containsKey(sName)) {
						studentAnsList = namedRecords.get(sName);
					}
					studentAnsList.add(maxIndex);
					namedRecords.put(sName, studentAnsList);
					
					ArrayList<Integer> correctAnsList = new ArrayList<Integer>();
					if(correctStats.containsKey(sName)){
						correctAnsList = correctStats.get(sName);
					}
					
					if(maxIndex == correctAns){
						correctAnsList.add(1);
						correctCount++;
					}else{
						correctAnsList.add(-1);
						incorrectCount++;
					}
					correctStats.put(sName,correctAnsList);
				}
			} else {
				finalAns.put(each, maxIndex);
			}
		}
		Iterator<String> presentIt = presentList.iterator();
		while (presentIt.hasNext()) {
			String pId = presentIt.next();
			String sName = pNameId.get(pId)+"/"+pId;
			Integer id = Integer.parseInt(pId);
			if (!finalAns.containsKey(id)) {
				ArrayList<Integer> studentAnsList = new ArrayList<Integer>();
				if (namedRecords.containsKey(sName)) {
					studentAnsList = namedRecords.get(sName);
				}
				studentAnsList.add(5);
				namedRecords.put(sName, studentAnsList);
				
				ArrayList<Integer> correctAnsList = new ArrayList<Integer>();
				if(correctStats.containsKey(sName)){
					correctAnsList = correctStats.get(sName);
				}
				correctAnsList.add(0);
				correctStats.put(sName, correctAnsList);
				
				finalAns.put(id, 5);
			}
		}

	}

	/**
	 * Load next.
	 *
	 * @param view the view
	 */
	public void loadNext(View view) {

		if (quizQuesCount < questCards.size()) {
			try {
				mapAnswers();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;
			int x = 0;

			// Storing individual student's responses to questions in
			// studentRecords
			Set<Integer> keyAns = finalAns.keySet();
			Iterator<Integer> it = keyAns.iterator();
			while (it.hasNext()) {
				Integer each = it.next();
				Integer ans = finalAns.get(each);
				switch (ans) {
				case 0:
					a++;
					break;

				case 1:
					b++;
					break;

				case 2:
					c++;
					break;

				case 3:
					d++;
					break;

				default:
					x++;
					break;

				}

				if (studentRecords.containsKey(each)) {
					String studentName = pNameId.get(each.toString());
					ArrayList<Integer> recList = studentRecords.get(each);
					// Necessary to ensure that answers are logged in
					// correct order.
					// If a card is not present in some frame, put 5 as the
					// response
					/*
					 * int i = 0; while (recList.size() < qid) {
					 * 
					 * if (recList.size() > 0) { recList.add(recList.size() + i,
					 * 5); } else { recList.add(5); }
					 * 
					 * i++; }
					 */
					recList.add(ans);
					studentRecords.put(each, recList);
					if (studentName != null) {
						/*
						 * if (namedRecords.containsKey(studentName)) {
						 * namedRecords.put(studentName, recList); } else {
						 * namedRecords.put(studentName, recList); }
						 */
					}
				} else {
					String studentName = pNameId.get(each.toString());
					ArrayList<Integer> recList = new ArrayList<Integer>();
					// Necessary to ensure that answers are logged in
					// correct order.
					// If a card is not present in some frame, put 5 as the
					// response
					int i = 0;
					/*
					 * while (recList.size() < qid) {
					 * 
					 * if (recList.size() > 0) { recList.add(90);
					 * recList.add(recList.size() - 1, 5); } else {
					 * recList.add(5); } i++; }
					 */
					recList.add(ans);
					studentRecords.put(each, recList);
					if (studentName != null) {
						/*
						 * if (namedRecords.containsKey(studentName)) {
						 * namedRecords.put(studentName, recList); } else {
						 * namedRecords.put(studentName, recList); }
						 */
					}
				}
			}
			final ArrayList<Integer> aOptions = new ArrayList<Integer>();
			aOptions.add(a);
			aOptions.add(b);
			aOptions.add(c);
			aOptions.add(d);
			aOptions.add(x);
			cumulativeOptions.clear();
			if (cumulativeOptions.isEmpty()) {
				cumulativeOptions.add(a);
				cumulativeOptions.add(b);
				cumulativeOptions.add(c);
				cumulativeOptions.add(d);
				cumulativeOptions.add(x);

			} else {

				/*
				 * a += cumulativeOptions.get(0); b += cumulativeOptions.get(1);
				 * c += cumulativeOptions.get(2); d += cumulativeOptions.get(3);
				 */
				cumulativeOptions.remove(0);
				cumulativeOptions.add(0, a);
				cumulativeOptions.remove(1);
				cumulativeOptions.add(1, b);
				cumulativeOptions.remove(2);
				cumulativeOptions.add(2, c);
				cumulativeOptions.remove(3);
				cumulativeOptions.add(3, d);
				cumulativeOptions.remove(4);
				cumulativeOptions.add(4, x);
			}

			qid++;
			qDup++;
			questionStats.put(qid, aOptions);
			quesOrder.add(qDup);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					optA.setText("A: " + cumulativeOptions.get(0).toString());
					optB.setText("B: " + cumulativeOptions.get(1).toString());
					optC.setText("C: " + cumulativeOptions.get(2).toString());
					optD.setText("D: " + cumulativeOptions.get(3).toString());

				}
			});

			// Show Next Question in a Dialog Box
			// showQuesDialog(view);

			// Clear allocated resources
			areas.clear();
			per.clear();
			ang.clear();
			aprat.clear();
			centres.clear();
			rectHeight.clear();
			rectWidth.clear();
			rectSize.clear();
			xList.clear();
			yList.clear();
			contours.clear();
			cardsTable.clear();
			idX.clear();
			idY.clear();
			holes.clear();
			tempAns.clear();
			finalAns.clear();
		} else {
			if (!pollFinished) {
				try {
					mapAnswers();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int a = 0;
				int b = 0;
				int c = 0;
				int d = 0;
				int x = 0;

				// Storing individual student's responses to questions in
				// studentRecords
				Set<Integer> keyAns = finalAns.keySet();
				Iterator<Integer> it = keyAns.iterator();
				while (it.hasNext()) {
					Integer each = it.next();
					Integer ans = finalAns.get(each);
					switch (ans) {
					case 0:
						a++;
						break;

					case 1:
						b++;
						break;

					case 2:
						c++;
						break;

					case 3:
						d++;
						break;

					default:
						x++;
						break;

					}
					if (studentRecords.containsKey(each)) {
						String studentName = pNameId.get(each.toString());
						ArrayList<Integer> recList = studentRecords.get(each);
						// Necessary to ensure that answers are logged in
						// correct order.
						// If a card is not present in some frame, put 5 as the
						// response
						int i = 0;
						/*
						 * while (recList.size() < qid) { if (recList.size() >
						 * 0) { recList.add(90); recList.add(recList.size() - 1,
						 * 5); } else { recList.add(5); } i++; }
						 */
						recList.add(ans);
						studentRecords.put(each, recList);
						if (studentName != null) {
							/*
							 * if (namedRecords.containsKey(studentName)) {
							 * namedRecords.put(studentName, recList); } else {
							 * namedRecords.put(studentName, recList); }
							 */
						}
					} else {
						String studentName = pNameId.get(each.toString());
						ArrayList<Integer> recList = new ArrayList<Integer>();
						// Necessary to ensure that answers are logged in
						// correct order.
						// If a card is not present in some frame, put 5 as the
						// response
						int i = 0;
						/*
						 * while (recList.size() < qid) { if (recList.size() >
						 * 0) { recList.add(90); recList.add(recList.size() - 1,
						 * 5); } else { recList.add(5); } i++; }
						 */
						recList.add(ans);
						studentRecords.put(each, recList);
						if (studentName != null) {
							/*
							 * if (namedRecords.containsKey(studentName)) {
							 * namedRecords.put(studentName, recList); } else {
							 * namedRecords.put(studentName, recList); }
							 */
						}
					}

				}
				final ArrayList<Integer> aOptions = new ArrayList<Integer>();
				aOptions.add(a);
				aOptions.add(b);
				aOptions.add(c);
				aOptions.add(d);
				aOptions.add(x);
				cumulativeOptions.clear();
				if (cumulativeOptions.isEmpty()) {
					cumulativeOptions.add(a);
					cumulativeOptions.add(b);
					cumulativeOptions.add(c);
					cumulativeOptions.add(d);
					cumulativeOptions.add(x);

				} else {

					/*
					 * a += cumulativeOptions.get(0); b +=
					 * cumulativeOptions.get(1); c += cumulativeOptions.get(2);
					 * d += cumulativeOptions.get(3);
					 */

					cumulativeOptions.add(0, a);
					cumulativeOptions.add(1, b);
					cumulativeOptions.add(2, c);
					cumulativeOptions.add(3, d);
					cumulativeOptions.add(4, x);
				}

				qid++;
				qDup++;
				questionStats.put(qid, aOptions);
				quesOrder.add(qDup);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						optA.setText("A: "
								+ cumulativeOptions.get(0).toString());
						optB.setText("B: "
								+ cumulativeOptions.get(1).toString());
						optC.setText("C: "
								+ cumulativeOptions.get(2).toString());
						optD.setText("D: "
								+ cumulativeOptions.get(3).toString());

						/*
						 * optA.setText("A: " + aOptions.get(0).toString());
						 * optB.setText("B: " + aOptions.get(1).toString());
						 * optC.setText("C: " + aOptions.get(2).toString());
						 * optD.setText("D: " + aOptions.get(3).toString());
						 */
					}
				});

				// Clear allocated resources
				areas.clear();
				per.clear();
				ang.clear();
				aprat.clear();
				centres.clear();
				rectHeight.clear();
				rectWidth.clear();
				rectSize.clear();
				xList.clear();
				yList.clear();
				contours.clear();
				cardsTable.clear();
				idX.clear();
				idY.clear();
				holes.clear();
				tempAns.clear();
				finalAns.clear();
			}
			/* quizFinishedDialog(view); */
		}
		// cleanTheMess();
	}

	/**
	 * Quiz finished dialog.
	 *
	 * @param view the view
	 */
	private void quizFinishedDialog(View view) {
		// cleanTheMess();
		pollFinished = true;
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
				finishPoll(v);
				// finish();
				next.setVisibility(View.GONE);
				finishDialog.dismiss();
				finish();
			}
		});
		finishDialog.show();

	}

	/**
	 * Finish poll.
	 *
	 * @param view the view
	 */
	public void finishPoll(View view) {

		// if (pollFinished == false) {
		pollFinished = true;
		/*
		 * sb will store the data related to each question. each line in the
		 * string is of the form [qid, num of A, num of B, num of C, num of D]
		 */
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < questionStats.size(); i++) {
			sb.append(quesOrder.get(i).toString());
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(0));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(1));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(2));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(3));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(4));
			sb.append("\n");
		}

		/*
		 * qb will store the data related to each student. each line in the
		 * string is of the form [qid, num of A, num of B, num of C, num of D]
		 */

		StringBuilder qb = new StringBuilder();
		
		StringBuilder cb = new StringBuilder();
		cb.append("name,");
		for (int i = 0; i < questionStats.size(); i++) {
			cb.append(quesOrder.get(i).toString());
			cb.append(",");
		}
		cb.append("correct");
		cb.append("\n");
		Set<String> correctKeys = correctStats.keySet();
		Iterator<String> cIt = correctKeys.iterator();
		while(cIt.hasNext()){
			String each = cIt.next();
			ArrayList<Integer> ans = correctStats.get(each);
			cb.append(each);
		//	cb.append(",");
			Integer corrCount = 0;
			for(int i=0; i<ans.size();i++){
				cb.append(",");
				Integer ansInt = ans.get(i);
				if(ansInt == 1){
					corrCount++;
				}
				cb.append(ansInt.toString());
			}
			cb.append(","+corrCount.toString());
			cb.append('\n');
		}
		

		Set<String> recKeys = namedRecords.keySet();

		int firstIteration = 1;
		Iterator<String> it = recKeys.iterator();
		while (it.hasNext()) {
			String each = it.next();

			ArrayList<Integer> opt = namedRecords.get(each);

			if (firstIteration == 1) {
				qb.append("Student,");
				for (int i = 0; i < quesOrder.size(); i++) {
					qb.append(+quesOrder.get(i));
					qb.append(",");
				}
				qb.append("\n");
				firstIteration = 0;
			}

			qb.append(each);
			// qb.append(",");
			for (int i = 0; i < opt.size(); i++) {
				qb.append(",");
				switch (opt.get(i)) {
				case 0:
					qb.append("A");
					break;
				case 1:
					qb.append("B");
					break;
				case 2:
					qb.append("C");
					break;
				case 3:
					qb.append("D");
					break;
				case 5:
					qb.append("M");
					break;
				}
				// qb.append(opt.get(i));

			}
			qb.append("\n");
		}

		// Insert a dialog box here to ask whether he wants to save or not
		callQuizSave(view, sb.toString(), qb.toString(), cb.toString());
		// saveAsNew(view, "Save Results", sb.toString(), qb.toString());

		int keygen = 0;
		/*
		 * } else { Dialog noChange = new Dialog(view.getContext());
		 * noChange.setContentView(R.layout.no_change_layout); noChange.show();
		 * }
		 */
		/* generate a key and save the question and student record file names */
	}

	// called after each question
	/**
	 * Log results.
	 */
	public void logResults() {

		// if (pollFinished == false) {
		// pollFinished = true;
		/*
		 * sb will store the data related to each question. each line in the
		 * string is of the form [qid, num of A, num of B, num of C, num of D]
		 */
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < questionStats.size(); i++) {
			sb.append(quesOrder.get(i).toString());
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(0));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(1));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(2));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(3));
			sb.append(",");
			sb.append(questionStats.get(quesOrder.get(i)).get(4));
			sb.append("\n");
		}
		
		StringBuilder cb = new StringBuilder();
		cb.append("name,");
		for (int i = 0; i < questionStats.size(); i++) {
			cb.append(quesOrder.get(i).toString());
			cb.append(",");
		}
		cb.append("\n");
		Set<String> correctKeys = correctStats.keySet();
		Iterator<String> cIt = correctKeys.iterator();
		while(cIt.hasNext()){
			String each = cIt.next();
			ArrayList<Integer> ans = correctStats.get(each);
			cb.append(each);
			cb.append(",");
			for(int i=0; i<ans.size();i++){
				cb.append(ans.get(i).toString());
				cb.append(",");
			}
			cb.append('\n');
		}
		/*
		 * qb will store the data related to each student. each line in the
		 * string is of the form [qid, num of A, num of B, num of C, num of D]
		 */

		StringBuilder qb = new StringBuilder();

		Set<String> recKeys = namedRecords.keySet();

		
		Iterator<String> it = recKeys.iterator();
		while (it.hasNext()) {
			String each = it.next();

			ArrayList<Integer> opt = namedRecords.get(each);

			/*
			 * if (firstIteration == 1) { qb.append("Student,"); for (int i = 0;
			 * i < quesOrder.size(); i++) { qb.append(+(i + 1)); qb.append(",");
			 * } qb.append("\n"); firstIteration = 0; }
			 */

			qb.append(each);
			// qb.append(",");
			for (int i = 0; i < opt.size(); i++) {
				qb.append(",");
				switch (opt.get(i)) {
				case 0:
					qb.append("A");
					break;
				case 1:
					qb.append("B");
					break;
				case 2:
					qb.append("C");
					break;
				case 3:
					qb.append("D");
					break;
				case 5:
					qb.append("M");
					break;
				}
				// qb.append(opt.get(i));

			}
			qb.append("\n");
		}

		try {
			saveTempResults(qb.toString(), sb.toString(),cb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// saveAsNew(view, "Save Results", sb.toString(), qb.toString());

		int keygen = 0;
		/*
		 * } else { Dialog noChange = new Dialog(view.getContext());
		 * noChange.setContentView(R.layout.no_change_layout); noChange.show();
		 * }
		 */
		/* generate a key and save the question and student record file names */
	}

	public void saveDefault(View view, String content, String studentStats) {

	}

	// public void saveSessionResult

	/**
	 * Gets the poll name.
	 *
	 * @return the poll name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String getPollName() throws IOException {
		// String qn1 = QSet.qSetNameGlobal;
		String qn1 = keyVal.getQSet();
		String qn2 = null;
		if (keyVal.getRosterQuizBool()) {
			qn2 = keyVal.getAttendanceRoster();
		} else {
			qn2 = "null";
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

	/**
	 * Gets the temp session name.
	 *
	 * @return the temp session name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String getTempSessionName() throws IOException {
		// String qn1 = QSet.qSetNameGlobal;
		String qn1 = RosterList.removeExt(keyVal.getQSet());
		String qn2 = null;
		if (keyVal.getRosterQuizBool()) {
			qn2 = keyVal.getAttendanceRoster();
		} else {
			qn2 = "null";
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
				+ "/QCards/TempResults/PollResults/");
		if(!tempExists()){
			createTemp();
		}
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/TempResults/PollResults/" + quizName + ".csv");
		// String copyName = getCopyName(fileDirectory, fName);
		if (didAppCrash(qn1, qn2, fileDirectory)) {
			new AlertDialog.Builder(context)
					.setTitle("Continue Last Quiz?")
					.setMessage("Do you want to complete the unfinished quiz?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										resumeCrashedPoll();
										cleanTempFiles();
										validPoll = 0;
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										transferTempFiles();
										//cleanTempFiles();
										// pollFileName = quizName;
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.setCancelable(false)
					.show();
		}
	//	cleanTempFiles();
		// if (!filePath.exists()) {
		pollFileName = quizName;

		/*
		 * } else { pollFileName = getCopyName(fileDirectory, quizName);
		 * 
		 * }
		 */
		// cleanTempFiles();
		return pollFileName;
	}

	/**
	 * Did app crash.
	 *
	 * @param qn1 the qn1
	 * @param qn2 the qn2
	 * @param tempPollDirectory the temp poll directory
	 * @return true, if successful
	 */
	public boolean didAppCrash(String qn1, String qn2, File tempPollDirectory) {
		File[] files = tempPollDirectory.listFiles();
		
		StringBuilder sb = new StringBuilder();
		if (files.length > 0) {
			String fileName = files[0].getName();
			ArrayList<String> tokens = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(fileName, "_");
			while(st.hasMoreTokens()){
				tokens.add(st.nextToken());
			}
			for(int i = 0; i<tokens.size()-3; i++){
				sb.append(tokens.get(i));
				sb.append("_");
			}
			String nameMatch = qn1+"_"+qn2+"_";
			if (nameMatch.matches(sb.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * File copier.
	 *
	 * @param br the br
	 * @param srcFileName the src file name
	 * @param destFolder the dest folder
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void fileCopier(BufferedReader br, String srcFileName,
			File destFolder) throws IOException {
		File saveIt = new File(destFolder, srcFileName);
		FileOutputStream fos = new FileOutputStream(saveIt);
		StringBuilder sb = new StringBuilder();
		String str;
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		fos.write(sb.toString().getBytes());
	}

	public boolean tempExists(){
		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/PollResults");
		if(!tempPollDirectory.exists()){
			return false;
		}
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/StudentStats");
		if(!tempStudentDirectory.exists()){
			return false;
		}
		return true;
	}
	
	public void createTemp(){
		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/PollResults");
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/StudentStats");
		File tempAnalysisDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Analysis");
		
		tempPollDirectory.mkdirs();
		tempStudentDirectory.mkdirs();
		tempAnalysisDirectory.mkdirs();
	}
	
	/**
	 * Transfer temp files.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void transferTempFiles() throws IOException {
		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/PollResults");
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/StudentStats");
		File tempAnalysisDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Analysis");
		
		File pollDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/QCards/PollResults");
		File studentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/StudentStats");
		File analysisDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/Analysis");

		File[] pollFiles = tempPollDirectory.listFiles();

		File[] studentFiles = tempStudentDirectory.listFiles();

		File[] analysisFiles = tempAnalysisDirectory.listFiles();

		try {
			String pollFile;
			if (pollFiles.length > 0) {
				pollFile = pollFiles[0].getName();
				BufferedReader buff = new BufferedReader(new FileReader(
						pollFile));
				fileCopier(buff, pollFile, pollDirectory);
			}
			String studentFile;
			if (studentFiles.length > 0) {
				studentFile = studentFiles[0].getName();
			BufferedReader buff = new BufferedReader(new FileReader(studentFile));
			fileCopier(buff, studentFile, studentDirectory);
			}
			String analysisFile;
			if (analysisFiles.length > 0) {
				analysisFile = analysisFiles[0].getName();
			BufferedReader buff = new BufferedReader(new FileReader(analysisFile));
			fileCopier(buff, analysisFile, studentDirectory);
			}
			cleanTempFiles();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Clean temp files. deletes the temporary Poll and Student Stats files
	 */
	public void cleanTempFiles() {
		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/PollResults");
		// File[] files = fileDirectory.listFiles();
		for (File f : tempPollDirectory.listFiles()) {
			f.delete();
		}
		
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/StudentStats");
		// File[] files = fileDirectory.listFiles();
		for (File f : tempStudentDirectory.listFiles()) {
			f.delete();
		}
		
		File tempAnalysisDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Analysis");
		// File[] files = fileDirectory.listFiles();
		for (File f : tempAnalysisDirectory.listFiles()) {
			f.delete();
		}
	}

	/**
	 * Resume crashed poll.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void resumeCrashedPoll() throws IOException {
		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/PollResults");
		
		File tempAnalysisDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Analysis");
		
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/StudentStats");
		
		BufferedReader buff;
		for (File f : tempPollDirectory.listFiles()) {
			// File file = new File(tempPollDirectory+"/"+f);
			FileReader in = new FileReader(f);
			buff = new BufferedReader(in);
			String str;
			while ((str = buff.readLine()) != null) {
				// tempPollData.append(str);
				StringTokenizer tokens = new StringTokenizer(str, ",");
				int id = Integer.parseInt(tokens.nextToken());
				ArrayList<Integer> al = new ArrayList<Integer>();
				while (tokens.hasMoreTokens()) {
					al.add(Integer.parseInt(tokens.nextToken()));
				}
				quizQuesCount++;
				qid++;
				qDup++;
				quesOrder.add(qDup);
				questionStats.put(id, al);
			}
		}

		for (File f : tempStudentDirectory.listFiles()) {
			// File file = new File(tempPollDirectory+"/"+f);
			FileReader in = new FileReader(f);
			buff = new BufferedReader(in);
			String str;
			while ((str = buff.readLine()) != null) {
				//tempStudentData.append(str);
				StringTokenizer strToken = new StringTokenizer(str, ",");
				String sName = strToken.nextToken();
				ArrayList<Integer> studentAnsList = new ArrayList<Integer>();
				while (strToken.hasMoreTokens()) {
					if (namedRecords.containsKey(sName)) {
						studentAnsList = namedRecords.get(sName);
					}
					String option = strToken.nextToken();

					if (option.matches("A")) {
						studentAnsList.add(0);
					} else if (option.matches("B")) {
						studentAnsList.add(1);
					} else if (option.matches("C")) {
						studentAnsList.add(2);
					} else if (option.matches("D")) {
						studentAnsList.add(3);
					} else if (option.matches("M")) {
						studentAnsList.add(5);
					}
				}
				namedRecords.put(sName, studentAnsList);
			}
		}
		
		for (File f : tempAnalysisDirectory.listFiles()) {
			// File file = new File(tempPollDirectory+"/"+f);
			FileReader in = new FileReader(f);
			buff = new BufferedReader(in);
			String str;
			int count = 0;
			while ((str = buff.readLine()) != null) {
				//tempStudentData.append(str);
				if(count>0){
				StringTokenizer strToken = new StringTokenizer(str, ",");
				String sName = strToken.nextToken();
				ArrayList<Integer> studentAnsList = new ArrayList<Integer>();
				while (strToken.hasMoreTokens()) {
					studentAnsList.add(Integer.parseInt(strToken.nextToken()));
				correctStats.put(sName, studentAnsList);
			}
				}else{
					count =1;
				}
			}
		}
		// logResults();
	}

	/**
	 * Gets the copy name.
	 *
	 * @param fileDirectory the file directory
	 * @param fName the f name
	 * @return the copy name
	 */
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

	/**
	 * Call quiz save.
	 *
	 * @param view the view
	 * @param content the content
	 * @param studentStats the student stats
	 * @param correctData the correct data
	 */
	public void callQuizSave(View view, String content, String studentStats, String correctData) {
		String studentLogs = studentStats;
		String pollLogs = content;
		View v = view;

		if (pollFinished) {
			try {
				saveNewPollFile(studentLogs, pollLogs, getPollName(), correctData);
				cleanTempFiles();
				Intent i = new Intent(v.getContext(), JumpInMain.class);
				// i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(i);
				finish();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// endQuizDialog.setCancelable(false);
		} else {
			try {
				saveTempResults(studentLogs, pollLogs, correctData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// endQuizDialog.show();

	}

	/**
	 * Save as new.
	 *
	 * @param v the v
	 * @param title the title
	 * @param content the content
	 * @param studentStats the student stats
	 * @param correctData the correct data
	 */
	public void saveAsNew(View v, String title, String content,
			String studentStats, String correctData) {
		final String fileContent = content;
		final String studentData = studentStats;
		final String correct = correctData;
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
						saveNewPollFile(studentData, fileContent, correct, fName
								.getText().toString());
						dial.dismiss();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					saveAsNew(v, "File Name already used!", fileContent,
							studentData, correct);
				}
			}
		});
		dial.show();
	}

	/**
	 * Save new poll file.
	 *
	 * @param studentData the student data
	 * @param content the content
	 * @param fileName the file name
	 * @param correctData the correct data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveNewPollFile(String studentData, String content,	String fileName, String correctData) throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		File studentDirectory;
		File analysisDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/PollResults");
			studentDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/StudentStats");
			analysisDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/Analysis");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
				}
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				// fos.close();
				Log.e(TAG, "Directory Created");

			} else {
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				// fos.close();
				Log.e(TAG, "Directory Created");

			}

			if (!studentDirectory.exists()) {
				if (studentDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
				}
				File saveIt = new File(studentDirectory, fileName + ".csv");
				FileOutputStream fos1 = new FileOutputStream(saveIt);
				fos1.write(studentData.getBytes());
				// fos.close();
				Log.e(TAG, "Directory Created");
			} else {
				File saveIt = new File(studentDirectory, fileName + ".csv");
				FileOutputStream fos1 = new FileOutputStream(saveIt);
				fos1.write(studentData.getBytes());
				Log.e(TAG, "Directory Created");
			}
			if (!analysisDirectory.exists()) {
				if (analysisDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
				}
				File saveIt = new File(analysisDirectory, fileName + ".csv");
				FileOutputStream fos1 = new FileOutputStream(saveIt);
				fos1.write(correctData.getBytes());
				// fos.close();
				Log.e(TAG, "Directory Created");
			} else {
				File saveIt = new File(analysisDirectory, fileName + ".csv");
				FileOutputStream fos1 = new FileOutputStream(saveIt);
				fos1.write(correctData.getBytes());
				Log.e(TAG, "Directory Created");
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
		}
	}

	/**
	 * Save temp results.
	 *
	 * @param studentData the student data
	 * @param pollData the poll data
	 * @param correctData the correct data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveTempResults(String studentData, String pollData, String correctData)
			throws IOException {
		// checking if External Storage is available
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		File appDirectory;
		File studentDirectory;
		File analysisDirectory;
		FileOutputStream fos;
		// StringBuilder putInFileBuilder = new StringBuilder();
		if (isExternalAvailable) {

			// get path to QCards directory on External Storage
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/TempResults/PollResults");
			studentDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/TempResults/StudentStats");
			analysisDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/QCards/TempResults/Analysis");
			if (!appDirectory.exists()) {
				if (appDirectory.mkdirs()) {
					Log.e(TAG, "Temp Poll Results Created");
				}
			}
			File saveIt = new File(appDirectory, sessionName + ".csv");
			fos = new FileOutputStream(saveIt);
			fos.write(pollData.getBytes());

			if (!studentDirectory.exists()) {
				if (studentDirectory.mkdirs()) {
					Log.e(TAG, "Directory Created");
				}
			}
			saveIt = new File(studentDirectory, sessionName + ".csv");
			fos = new FileOutputStream(saveIt);
			fos.write(studentData.getBytes());

			
			if(!analysisDirectory.exists()){
				if(analysisDirectory.mkdirs()){
					Log.e(TAG, "Directory Created");					
				}
			}
			saveIt = new File(analysisDirectory, sessionName + ".csv");
			fos = new FileOutputStream(saveIt);
			fos.write(correctData.getBytes());
		} else {
			Log.e(TAG, "SD Card unavailable");
		}

	}

	/**
	 * If attendance file exists.
	 *
	 * @param file_name the file_name
	 * @param v the v
	 * @return true, if successful
	 */
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
				+ "/QCards/PollResults");
		if (new File(appDirectory + "/" + file_name + ".csv").exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Show ques dialog.
	 *
	 * @param view the view
	 */
	private void showQuesDialog(View view) {
		cleanTheMess();
		dial = new Dialog(view.getContext());
		dial.setContentView(R.layout.quiz_dialog);

		Integer seq = quizQuesCount + 1;
		dial.setTitle("Question(" + seq.toString() + " of " + questCards.size()
				+ ")");
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dial.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;

		// TextView numView = (TextView) dial.findViewById(R.id.quesNum);
		TextView aView = (TextView) dial.findViewById(R.id.opA);
		TextView bView = (TextView) dial.findViewById(R.id.opB);
		TextView cView = (TextView) dial.findViewById(R.id.opC);
		TextView dView = (TextView) dial.findViewById(R.id.opD);
		TextView qView = (TextView) dial.findViewById(R.id.quest);
		Button pollButton = (Button) dial.findViewById(R.id.eachPoll);
		if (quizQuesCount <= questCards.size()) {
			// numView.setText("Question");
			aView.setText(questCards.get(quizQuesCount).getA());
			bView.setText(questCards.get(quizQuesCount).getB());
			cView.setText(questCards.get(quizQuesCount).getC());
			dView.setText(questCards.get(quizQuesCount).getD());
			qView.setText(questCards.get(quizQuesCount).getQuestion());
			correctAns = questCards.get(quizQuesCount).getCorrect();
			
		}
		// Increment the question number

		// final EditText fName = (EditText)
		// dial.findViewById(R.id.newQSetName);
		pollButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				validPoll = 1;

				if (quizQuesCount == 0) {
					cleanTheMess();
				}

				dial.dismiss();

			}
		});

		dialogOn = true;
		dial.getWindow().setAttributes(lp);
		dial.show();
	}

	/**
	 * Show ques dialog.
	 *
	 * @param view the view
	 * @param arg the arg
	 */
	private void showQuesDialog(View view, int arg) {

		dial = new Dialog(view.getContext());
		dial.setContentView(R.layout.quiz_dialog);

		Integer seq = quizQuesCount + 1;
		dial.setTitle("Question(" + seq.toString() + " of " + questCards.size()
				+ ")");
		// TextView numView = (TextView) dial.findViewById(R.id.quesNum);
		TextView aView = (TextView) dial.findViewById(R.id.opA);
		TextView bView = (TextView) dial.findViewById(R.id.opB);
		TextView cView = (TextView) dial.findViewById(R.id.opC);
		TextView dView = (TextView) dial.findViewById(R.id.opD);
		TextView qView = (TextView) dial.findViewById(R.id.quest);
		Button pollButton = (Button) dial.findViewById(R.id.eachPoll);
		if (quizQuesCount <= questCards.size()) {
			// numView.setText("Question");
			aView.setText(questCards.get(quizQuesCount).getA());
			bView.setText(questCards.get(quizQuesCount).getB());
			cView.setText(questCards.get(quizQuesCount).getC());
			dView.setText(questCards.get(quizQuesCount).getD());
			qView.setText(questCards.get(quizQuesCount).getQuestion());
		}
		// Increment the question number

		// final EditText fName = (EditText)
		// dial.findViewById(R.id.newQSetName);
		pollButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// onDialogButton(v);
				validPoll = 1;

				if (quizQuesCount == 0) {
					cleanTheMess();
				}

				dial.dismiss();

			}
		});

		dialogOn = true;
		dial.show();
		dial.dismiss();
		showQuesDialog(view);
	}

	/**
	 * Start stop poll.
	 *
	 * @param view the view
	 */
	public void startStopPoll(View view) {
		if (prePoll > 0) {
			if (validPoll == 1) {
				loadNext(view);
				callChart(view);
				cleanTheMess();
				validPoll = 0;
			} else {
				if (quizQuesCount < questCards.size()) {
					cleanTheMess();
					showQuesDialog(view);
				} else {
					cleanTheMess();
					loadNext(view);
				}
			}
		} else {
			prePoll = 1;
			next.setText("View result and show next question");
			cleanTheMess();
			showQuesDialog(view);
		}
	}

	/**
	 * Call chart.
	 *
	 * @param view the view
	 */
	public void callChart(View view) {
		/*
		 * Dialog d = new Dialog(view.getContext());
		 * d.setContentView(R.layout.chart_dialog);
		 */
		logResults();
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
								quizQuesCount++;
								if (quizQuesCount < questCards.size()) {

									showQuesDialog(v);
								} else {
									quizFinishedDialog(v);
								}
							}
						})
				.setNegativeButton(R.string.finish_poll_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finishPoll(v);
							}
						})
				.setNeutralButton(R.string.ask_again,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								qDup--;
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
		int corr = correctCount;
		int incorr = incorrectCount;
		if (!cumulativeOptions.isEmpty()) {
			a = cumulativeOptions.get(0);
			b = cumulativeOptions.get(1);
			c = cumulativeOptions.get(2);
			d = cumulativeOptions.get(3);
			x = cumulativeOptions.get(4);
		}

		ArrayList<BarEntry> valsComp1 = new ArrayList<BarEntry>();
		if (!cumulativeOptions.isEmpty()) {
			int labelInt = 0;
			BarEntry c1e1 = new BarEntry(a, 0);
			BarEntry c1e2 = new BarEntry(b, 1);
			BarEntry c1e3 = new BarEntry(c, 2);
			BarEntry c1e4 = new BarEntry(d, 3);
			BarEntry c1e5 = new BarEntry(x, 4);
			BarEntry c1e6 = new BarEntry(corr, 5);
			BarEntry c1e7 = new BarEntry(incorr, 6);

			if (a != 0 || b != 0 || c != 0 || d != 0) {

				labelInt = 1;
			}
			valsComp1.add(c1e1);
			valsComp1.add(c1e2);
			valsComp1.add(c1e3);
			valsComp1.add(c1e4);
			valsComp1.add(c1e5);
			valsComp1.add(c1e6);
			valsComp1.add(c1e7);

			BarDataSet setComp1 = new BarDataSet(valsComp1, "Responses");
			setComp1.setColors(new int[] { R.color.chart_a, R.color.chart_b,
					R.color.chart_c, R.color.chart_d, R.color.chart_x, R.color.chart_corr, R.color.chart_incorr },
					view.getContext());
			ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
			dataSets.add(setComp1);

			ArrayList<String> xVals = new ArrayList<String>();
			xVals.add("A");
			xVals.add("B");
			xVals.add("C");
			xVals.add("D");
			xVals.add("No");
			xVals.add("Corr");
			xVals.add("Wrong");

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
		chart.invalidate();
		cumulativeOptions.clear();
		correctCount = 0;
		incorrectCount = 0;
		
	}

	/**
	 * Gear clicked.
	 *
	 * @param view the view
	 */
	public void gearClicked(View view) {
		final View mView = view;

		Dialog dialog = new Dialog(view.getContext());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.poll_settings);

		final Switch highlight_cards = (Switch) dialog
				.findViewById(R.id.highlight_cards);

		final Switch hide_missing = (Switch) dialog
				.findViewById(R.id.hide_missing);

		final Switch show_binarize = (Switch) dialog
				.findViewById(R.id.show_binarize);

		final Switch apply_roster = (Switch) dialog
				.findViewById(R.id.apply_roster);

		final Switch apply_qset = (Switch) dialog.findViewById(R.id.apply_qset);
		final Switch apply_adaptive = (Switch) dialog
				.findViewById(R.id.apply_adaptive);

		// set states to display in the dialog
		highlight_cards.setChecked(highlightCardBool);
		hide_missing.setChecked(!showMissingBool);
		show_binarize.setChecked(showBinarizeBool);
		apply_roster.setChecked(!applyRosterBool);
		apply_qset.setChecked(!applyQsetBool);
		apply_adaptive.setChecked(adaptiveBool);

		// Click listeners

		apply_adaptive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adaptiveBool = apply_adaptive.isChecked();
				applyAdaptive(mView);
			}
		});

		highlight_cards.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				highlightCardBool = highlight_cards.isChecked();

				highlightCards(mView);
			}
		});
		hide_missing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMissingBool = !(hide_missing.isChecked());
				hideMissing(mView);
			}
		});
		show_binarize.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showBinarizeBool = show_binarize.isChecked();
				showBinarize(mView);
			}
		});
		apply_roster.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				applyRosterBool = apply_roster.isChecked();
				applyRoster(mView);
			}
		});
		apply_qset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				applyQsetBool = apply_qset.isChecked();
				applyQset(mView);
			}
		});

		dialog.show();

	}

	/**
	 * Apply adaptive.
	 *
	 * @param view the view
	 */
	public void applyAdaptive(View view) {
		String toastText = null;
		if (adaptiveBool) {
			toastText = "Now using Adaptive Threshold. This will slow down"
					+ " the app but might give better results in varying lighting conditions.";
		} else {
			toastText = "Switching to normal thresholding from Adaptive thresholding.";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	/**
	 * Highlight cards.
	 *
	 * @param view the view
	 */
	public void highlightCards(View view) {
		String toastText = null;
		if (highlightCardBool) {
			toastText = "Highlighting cards now";
		} else {
			toastText = "Switching off cards highlighting";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	/**
	 * Hide missing.
	 *
	 * @param view the view
	 */
	public void hideMissing(View view) {
		String toastText = null;
		if (highlightCardBool) {
			toastText = "Hiding missing entries now";
		} else {
			toastText = "Showing missing entries now";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	/**
	 * Show binarize.
	 *
	 * @param view the view
	 */
	public void showBinarize(View view) {
		String toastText = null;
		if (showBinarizeBool) {
			toastText = "Now showing the binarized view";
		} else {
			toastText = "Switching to normal color mode";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	/**
	 * Apply roster.
	 *
	 * @param view the view
	 */
	public void applyRoster(View view) {
		keyVal.setManageBool(false);
		keyVal.setRosterAttendanceBool(true);
		Intent intent = new Intent(this, RosterList.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Apply qset.
	 *
	 * @param view the view
	 */
	public void applyQset(View view) {
		Intent i = new Intent(view.getContext(), QSetList.class);
		startActivity(i);
		finish();
	}

	/**
	 * Gets the present list.
	 *
	 * @return the present list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void getPresentList() throws IOException {
		presentList = new ArrayList<String>();
		StringBuilder listBuilder = new StringBuilder();
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/TempAttendanceResults");
			String filePath = appDirectory + "/pList.csv";

			File rosterFile = new File(filePath);
			FileInputStream fis = new FileInputStream(rosterFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = bufRead.readLine()) != null) {
				presentList.add(line);
			}
			bufRead.close();
		}
		Log.e(TAG, "view list");
	}

	/**
	 * Gets the present name id.
	 *
	 * @return the present name id
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void getPresentNameId() throws IOException {
		ArrayList<String> pIdList = new ArrayList<String>();
		ArrayList<String> pNameList = new ArrayList<String>();
		StringBuilder listBuilder = new StringBuilder();
		BufferedReader bufRead = null;
		Boolean isExternalAvailable = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!isExternalAvailable) {

		} else {
			File appDirectory;
			appDirectory = new File(Environment.getExternalStorageDirectory()
					+ "/QCards/TempAttendanceResults");
			String filePath1 = appDirectory + "/pIdList.csv";
			String filePath2 = appDirectory + "/pNameList.csv";

			File idFile = new File(filePath1);
			FileInputStream fis = new FileInputStream(idFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = bufRead.readLine()) != null) {
				pIdList.add(line);
			}
			bufRead.close();

			File nameFile = new File(filePath2);
			fis = new FileInputStream(nameFile);
			bufRead = new BufferedReader(new InputStreamReader(fis));
			line = null;
			while ((line = bufRead.readLine()) != null) {
				pNameList.add(line);
			}
			bufRead.close();

			for (int i = 0; i < pNameList.size(); i++) {
				pNameId.put(pIdList.get(i), pNameList.get(i));
			}
		}
		Log.e(TAG, "view list");
	}

	/**
	 * Adds the to result file.
	 *
	 * @param pollData the poll data
	 * @param studentData the student data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void addToResultFile(String pollData, String studentData)
			throws IOException {

		File tempPollDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Poll");
		File tempStudentDirectory = new File(
				Environment.getExternalStorageDirectory()
						+ "/QCards/TempResults/Students");

		String pollPath = tempPollDirectory + "/" + sessionName;
		String studentPath = tempStudentDirectory + "/" + sessionName;

		FileWriter sWriter = new FileWriter(studentPath, true);
		PrintWriter sOut = new PrintWriter(sWriter, true);

		FileWriter pWriter = new FileWriter(pollPath, true);
		PrintWriter pOut = new PrintWriter(pWriter, true);
	}
}
