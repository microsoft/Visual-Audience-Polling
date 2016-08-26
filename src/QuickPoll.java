 package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
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
public class QuickPoll extends Activity implements CvCameraViewListener2 {
	private static final String TAG = null;
	private CameraBridgeViewBase mOpenCvCameraView;
	TextView details;
	Mat mRgba;
	long cdst = 0;
	long cdet = 0;
	int mHeight;
	int mWidth;
	ImageView pic;
	boolean adaptiveBool = false;
	
	KeyValues keyVal;
	
	ArrayList<String> presentList = new ArrayList<String>();

	Dialog dial = null;
	
	public static boolean fromPoll = false;

	public boolean dialogOn = false;
	boolean pollFinished = false;

	TextView missing;
	TextView optA;
	TextView optB;
	TextView optC;
	TextView optD;
	Button next;
	Button finish;
	Button analyse;
	

	int toggleResult = 0;

	private ArrayList<Integer> cumulativeOptions = new ArrayList<Integer>();

	// Activity Context
	final Context context = this;
	
	//Settings boolean
	public boolean highlightCardBool = true;
	public boolean showMissingBool = false;
	public boolean showBinarizeBool = false;
	public boolean applyRosterBool = true;
	public boolean applyQsetBool = true;

	// Question set
	//List<QSetCards> questCards = QSet.qSetList;
	Integer quizQuesCount = 0;

	// Do not clear
	int thresh_otsu = CameraSettings.get_otsu();
	int thresh_inv = CameraSettings.get_inv();
	boolean blur = CameraSettings.get_blur();

	// Do not clear
	private static ArrayList<String> pList = new ArrayList<String>();
	private static ArrayList<String> aList = new ArrayList<String>();
	private static ArrayList<String> eList = new ArrayList<String>();

	// Do not clear
	Hashtable<Integer, ArrayList<Integer>> studentRecords = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> questionStats = new Hashtable<Integer, ArrayList<Integer>>();
	ArrayList<Integer> quesOrder = new ArrayList<Integer>();
	int qid = 0;

	List<Double> areas = new ArrayList<Double>();
	List<Double> per = new ArrayList<Double>();
	List<Double> ang = new ArrayList<Double>();
	List<Double> aprat = new ArrayList<Double>();
	List<Point> centres = new ArrayList<Point>();
	List<Double> rectHeight = new ArrayList<Double>();
	List<Double> rectWidth = new ArrayList<Double>();
	List<Size> rectSize = new ArrayList<Size>();
	Point[] points;
	ArrayList<Point> holeList = new ArrayList<Point>();
	ArrayList<Integer> idsList = new ArrayList<Integer>();
	Integer[] optionCount = new Integer[5];
	int[] parentIds;
	double[] childIds;
	int sizeMax;
	long timeT;
	long cardsTime;
	List<Double> xList = new ArrayList<Double>();
	List<Double> yList = new ArrayList<Double>();
	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	List<MatOfPoint> contoursWhite = new ArrayList<MatOfPoint>();
	Hashtable<Integer, ArrayList<Integer>> cardTable = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> cardsTable = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> sameCenterTable = new Hashtable<Integer, ArrayList<Integer>>();
	List<Integer> idList = new ArrayList<Integer>();
	List<Double> idX = new ArrayList<Double>();
	List<Double> idY = new ArrayList<Double>();
	List<Point> holes = new ArrayList<Point>();
	List<Point> idPoint = new ArrayList<Point>();
	Hashtable<Integer, Integer> answers = new Hashtable<Integer, Integer>();
	List<Integer> frame1 = new ArrayList<Integer>();
	List<Integer> frame2 = new ArrayList<Integer>();
	List<Integer> frame3 = new ArrayList<Integer>();
	Hashtable<Integer, ArrayList<Integer>> frameStore = new Hashtable<Integer, ArrayList<Integer>>();
	int checkNow = 0;
	Integer detectedNum = 0;
	byte[] matBuff;
	Hashtable<Integer, ArrayList<Integer>> idStore = new Hashtable<Integer, ArrayList<Integer>>();
	private Integer frameCount = 1;
	Hashtable<Integer, ArrayList<Integer>> idFrequency = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> tempAns = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, Integer> finalAns = new Hashtable<Integer, Integer>();
	List<Integer> checkCont = new ArrayList<Integer>();
	// getting the path to External Storage Gallery
	final String galleryPath = Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES).toString();
	// The line commented below causes error in MotoG
	// final String photoPath = galleryPath+"/Second Sight/card1.png";
	final String photoPath = "storage/sdcard1/Pictures/Second Sight/25per1.png";
	int frameCounter = 0;
	/*Rect roi;
	Mat grayMatrix;
	Mat colorM;*/
	
	long entryTime;
	long startTime;
	Long elapsedFromStart;
	StringBuilder timeLog;

	// This is standard OpenCV call and has to be included
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				// DO YOUR WORK/STUFF HERE
				/*
				 * runOnUiThread(new Runnable(){
				 * 
				 * @Override public void run(){
				 * Toast.makeText(MainActivity.this,"Loaded",
				 * Toast.LENGTH_LONG).show(); } });
				 */
				mOpenCvCameraView.enableView();
				// mOpenCvCameraView.enableFpsMeter();
				// processIt();
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

	public void onCameraViewStarted(int width, int height) {

		// mRgba = new Mat();
		mHeight = height;
		mWidth = width;
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mOpenCVCallBack);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		pList.clear();
		aList.clear();
		eList.clear();
		keyVal = new KeyValues(getApplicationContext(),"globals");

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// setContentView(R.layout.take_camera_attendance);
		setContentView(R.layout.quick_poll);

		optA = (TextView) findViewById(R.id.optionA);
		optB = (TextView) findViewById(R.id.optionB);
		optC = (TextView) findViewById(R.id.optionC);
		optD = (TextView) findViewById(R.id.optionD);
		next = (Button) findViewById(R.id.next_button);
		finish = (Button) findViewById(R.id.finish_button);
		analyse = (Button) findViewById(R.id.analyse_button);
		missing = (TextView) findViewById(R.id.missingEntries);
		analyse.setVisibility(View.GONE);
		missing.setVisibility(View.GONE);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.display_java_surface_view);
		mOpenCvCameraView.setMaxFrameSize(2000, 2000);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		// showQuesDialog(mOpenCvCameraView);
	}
	
	public void logAll(View view) throws IOException{
		
					File logDir = new File(
							Environment.getExternalStorageDirectory()
									+ "/QCards/Logs");
					if (!logDir.exists()) {
						if (logDir.mkdirs()) {
							Log.e(TAG, "Directory Created");
						}
						Long time = System.currentTimeMillis();
						File saveIt = new File(logDir, time.toString()+ ".csv");
						FileOutputStream fos = new FileOutputStream(saveIt);
						String content = timeLog.toString();
						fos.write(content.getBytes());
					//	fos.close();
					//	Log.e(TAG, "Directory Created");

					}
	}

	// This is where all those methods are called that are required for
	// processing the image
	// First the image is fetched from the SD Card, Converted to a Matrix and
	// then processed
	
	protected Mat processIt(Mat grayMat, Mat rgbaMat) {
		//grayMatrix.release();
		Mat grayMatrix = grayMat;
		Mat colorM = rgbaMat;
		// This gives us a 'grey' matrix holding greyscale values
		// Imgproc.cvtColor(tmp, grey, CvType.CV_8UC1);
		// Mat grey = new Mat(mWidth,mHeight,CvType.CV_8UC1);

		// Imgproc.cvtColor(tmp, grey, Imgproc.COLOR_RGB2GRAY);
		// Get a Filtered(Otsu_Threshhold followed by Binary Inversion) image
	//	Mat filtered = filterIt(grayM);
		
		//Mat zoomed = new Mat(2*colorM.cols(),2*colorM.rows(),colorM.type());
	//	Imgproc.resize(colorM, zoomed, zoomed.size(),2.0,2.0,Imgproc.INTER_CUBIC);
		// detectBlack method detects the contours and calculates the parameters
	//	Mat detectResults = detectBlack(filtered, colorM, grayM);
		//Imgproc.resize(detectResults, colorM, colorM.size());
		//Imgproc.resize(filtered, colorM, colorM.size());
		/*
		 * Mat returnColor = new Mat(filtered.cols(), filtered.rows(),
		 * CvType.CV_8UC4);
		 * 
		 * Imgproc.cvtColor(filtered, returnColor, Imgproc.COLOR_GRAY2RGBA);
		 */
		//return detectResults;
	/*	Rect roi = new Rect(0, 0, grayMatrix.cols()/2, grayMatrix.rows()/2);
		
		Rect roiBig = new Rect(0,0,grayMatrix.cols(),grayMatrix.rows());
		
		Mat roiMat = grayMatrix.submat(roi);
		Mat roiMatBig = grayMatrix.submat(roiBig);
		Mat zoomed = new Mat(grayMatrix.cols(),grayMatrix.rows(),grayMatrix.type());
		Imgproc.resize(roiMat, roiMatBig, roiMatBig.size());
		roiMatBig.copyTo(grayMat.submat(roiBig));*/
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Mat roiMat = colorM.submat(roi);
				Mat roiMatBlack = grayMatrix.submat(roi);
				Imgproc.cvtColor(roiMat, roiMatBlack, Imgproc.COLOR_GRAY2RGBA);
				Mat filtered = filterIt(roiMatBlack);
				Mat colorMat = new Mat();
				//Imgproc.cvtColor(grayMatrix,colorMat, Imgproc.COLOR_GRAY2RGBA);
				Mat detected = detectBlack(filtered, colorM, grayMatrix);
				//Imgproc.cvtColor(filtered, filtered, Imgproc.COLOR_GRAY2RGBA);
				detected.copyTo(roiMat);
			}
		});*/
		
	//	Mat zoomed = new Mat(grayM.cols(),grayM.rows(),grayM.type());
	//	Imgproc.resize(filtered, zoomed, zoomed.size(),1.5,1.5,Imgproc.INTER_CUBIC);
	//	zoomed.copyTo(grayM.submat(new Rect(0, 0, grayM.rows(), grayM.cols())));
		
		
		/////////////////////////////// Zoom Code //////////////////////////////////
		/*Mat zoomed = new Mat();
		Imgproc.resize(grayMatrix, zoomed, new Size(),3,3,Imgproc.INTER_LINEAR);
		Mat filtered = filterIt(zoomed);
		Mat zoomedCol = new Mat();
		Imgproc.resize(colorM, zoomedCol, new Size(),3,3,Imgproc.INTER_LINEAR);
		Mat detected = detectBlack(filtered, zoomedCol, zoomed);
		
		Mat down = new Mat();
		Imgproc.resize(detected, down, new Size(),0.333,0.333,Imgproc.INTER_AREA);
		return down;
		*/
		entryTime = System.nanoTime();
		Mat filtered = filterIt(grayMatrix);
		Mat detected = detectBlack(filtered,colorM,grayMatrix);
		return detected;
		
	}

	private Mat detectBlack(Mat filtered, Mat colorMat, Mat greyMat) {

		Integer totContours = 0;
		Integer finContours = 0;

		cdst = System.nanoTime();
		Mat hierarchy = new Mat();

		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> tempContours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_NONE);
		totContours = mContours.size();
		double[] data = new double[hierarchy.rows() * hierarchy.cols()
				* hierarchy.channels()];
		// Checking if individual contours can be retrieved

		for (int i = 0; i < mContours.size(); i++) {
			double[] hVal = hierarchy.get(0, i);
			if (hVal[2] > -1) {
				double pArea = Imgproc.contourArea(mContours.get(i));
				double cArea = Imgproc
						.contourArea(mContours.get((int) hVal[2]));
			//	if (pArea > 7 * cArea ) {
					if (!tempContours.contains(mContours.get(i))) {
						tempContours.add(mContours.get(i));
					}
					if (!tempContours.contains(mContours.get((int) hVal[2]))) {
						tempContours.add(mContours.get((int) hVal[2]));
					}
			//	}
			}
		}

		// Iterator<MatOfPoint> each1 = mContours.iterator();
		Iterator<MatOfPoint> each1 = tempContours.iterator();
		// DO NOT forget to clear the contours list
		// mContours.clear();
		contours.clear();
		while (each1.hasNext()) {
			MatOfPoint wrapper = each1.next();
			if (Imgproc.contourArea(wrapper) < 86400
					&& Imgproc.contourArea(wrapper) >= 9) {
				MatOfPoint2f forrotatedrect = new MatOfPoint2f();
				MatOfPoint wrapRotate = wrapper;
				wrapRotate.convertTo(forrotatedrect, CvType.CV_32FC2);
				RotatedRect rotated = Imgproc.minAreaRect(forrotatedrect);
				// Calculate important parameters for contours
				Size sizeRect = rotated.size;
				if ((sizeRect.height / sizeRect.width) > 0.7
						&& (sizeRect.height / sizeRect.width) < 1.3) {
					if ((sizeRect.height * sizeRect.width) < 1.4 * Imgproc
							.contourArea(wrapper)) {
						// contours.add(wrapper);
						double moment00 = Imgproc.moments(wrapper).get_m00();
						double moment01 = Imgproc.moments(wrapper).get_m01();
						double moment10 = Imgproc.moments(wrapper).get_m10();
						double centerX = moment10 / moment00;
						double centerY = moment01 / moment00;
						if (filtered.get(properCastY(centerY),
								properCastX(centerX))[0] == 0) {
							contours.add(wrapper);
							double peri = Imgproc.arcLength(forrotatedrect,
									true);
							per.add(peri);
							double area = Imgproc.contourArea(wrapper);
							areas.add(area);
							xList.add(centerX);
							yList.add(centerY);
							centres.add(new Point(centerX, centerY));
						}
						// rectSize.add(sizeRect);
						// mContours.add(wrapper);
					}
				}
			}
			finContours = contours.size();

		}

		// Mat mat = new Mat(filtered.cols(), filtered.rows(), CvType.CV_8UC4);
		// Imgproc.cvtColor(filtered, mat, Imgproc.COLOR_GRAY2RGB);
		Mat mat = colorMat;
		
		/*
		 * if (highlightCardBool) { Imgproc.drawContours(mat, contours, -1, new
		 * Scalar(0, 255, 0), -1); }
		 */
		//Imgproc.dr
		// cdet = System.nanoTime();
		// Time taken for contour detection
		// Long cd = cdet - cdst;
		Long grs = System.nanoTime();
		findPadosi(mat);
		// Time taken for grouping
		// Long gr = System.nanoTime() - grs;

		// Long drs = System.nanoTime();
		mat = chitraBanao(mat, colorMat, filtered, greyMat);
		Imgproc.drawContours(colorMat, contours, -1, new
				  Scalar(0, 255, 0), -1);
		// Long dr = System.nanoTime() - drs;
		Integer totCards = cardsTable.size();
		if (mat != null) {
			return mat;
		} else {
			return colorMat;
		}

		// return filtered;
	}
	
	
	public int properCastX(Point p) {
		double check = Math.ceil(p.x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	public int properCastY(Point p) {
		double check2 = Math.ceil(p.y);
		int checkY = (int) (check2 * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

	public int properCastX(double x) {
		double check = Math.ceil(x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	public int properCastY(double y) {
		double check = Math.ceil(y);
		int checkY = (int) (check * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

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

	// Trying out canny edge detection. Applying Canny before detecting contours
	// can increase efficiency

	// Blur and dilation
	public Mat filterIt(Mat mat) {
		Mat grey = mat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		//Mat zoomed = new Mat(2*tmp1.cols(),2*tmp1.rows(), tmp1.type());
	//	Mat zoomed1 = new Mat(2*tmp1.cols(),2*tmp1.rows(), tmp1.type());
	//	Imgproc.resize(tmp1, zoomed, zoomed.size(), 2.0, 2.0, Imgproc.INTER_CUBIC);
		Mat dilated = new Mat();
		if (blur == true) {
			Imgproc.GaussianBlur(grey, tmp2, new Size(5, 5), 0);
		}
		
		//int erosion_type = Imgproc.MORPH_RECT;
	//	SimpleBlobDetector d = SimpleBlobDetector();
	//	FeatureDetector blobDetector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
	//	MatOfKeyPoint keypoints = new MatOfKeyPoint();
	//	blobDetector.detect(tmp1, keypoints);
	//	Imgproc.Canny(tmp1, tmp2, 100, 200);
	//	List<MatOfPoint> contList = new ArrayList<MatOfPoint>();
		//Mat h = new Mat();
		//Imgproc.findContours(tmp2, contours, h, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		// Mat mat1 = new Mat(tmp2.cols(), tmp2.rows(), CvType.CV_8UC4);
		// Imgproc.cvtColor(tmp2, mat1, Imgproc.COLOR_GRAY2RGB);
		// Imgproc.drawContours(mat1, contours, -1, new Scalar(0, 255, 0), -1);
		//Imgproc.blur(tmp1, tmp2, new Size(3,3));
		//Imgproc.dilate(tmp1, tmp2, kernel)
		//Imgproc.threshold(tmp2, tmp1, 120, 255, Imgproc.THRESH_BINARY_INV);
	//	Imgproc.threshold(tmp1, tmp1, 40, 255,Imgproc.THRESH_BINARY_INV);
		/*Imgproc.erode(tmp1, tmp2, new Mat());
		Imgproc.dilate(tmp2, tmp1, new Mat());
		Imgproc.threshold(tmp1, tmp2, 120, 255, Imgproc.THRESH_BINARY_INV);*/
		//Imgproc.erode(tmp1, tmp2, new Mat());
		//Imgproc.dilate(tmp2, tmp1, new Mat());
		//Imgproc.threshold(tmp1, tmp2, 120, 255, Imgproc.THRESH_BINARY_INV);
		//Core.inRange(tmp1, new Scalar(0, 0, 0), new Scalar(51, 56, 34), tmp2);
		
		//Imgproc.adaptiveThreshold(tmp1, tmp2, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
		//		Imgproc.adaptiveThreshold(tmp2, tmp1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
		if(adaptiveBool){
		Imgproc.adaptiveThreshold(tmp1, tmp1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
		}else{
			Imgproc.threshold(tmp2, tmp2, 120, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
			Imgproc.threshold(tmp2, tmp1, 0, 255,Imgproc.THRESH_BINARY_INV);
		}		
			if (tmp1 != null) {
				return tmp1;
			} else {
				return grey;
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
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	
	private void findNeighboursPro(Mat bnw){
		Point[] arr = new Point[centres.size()];
		Long ss = System.nanoTime();
		centres.toArray(arr);
		double[] arrArea = new double[areas.size()];
		Arrays.sort(arrArea);
		// Sort the array based on the x coordinate of the centres
	/*	Arrays.sort(arr, new Comparator<Point>() {
			public int compare(Point a, Point b) {
				int xComp = Double.compare(a.x, b.x);
				 if (xComp == 0)
				 return Double.compare(a.y, b.y);
				 else
				return xComp;
			}
		});*/
		// get the ids in the sorted order
		int[] ids = new int[centres.size()];
		for (int i = 0; i < centres.size(); i++) {
			ids[i] = centres.indexOf(arr[i]);
		}
		int counter = 0;
		
		
		//}
	}
	
	private boolean inRange(Point p1, Point p2, double scale){
		double diffx = p1.x - p2.x;
		double diffy = p1.y - p2.y;
		if(diffx<scale && diffy<scale)
			return true;
		else
			return false;
	}
	
	private double getDistance(Point p1, Point p2){
		double distance = 0;
		double diffx = p1.x*p1.x - p2.x*p2.x;
		double diffy = p1.y*p1.y - p2.y*p2.y;
		distance = Math.sqrt(diffx+diffy);
		return distance;
	}

	private void findPadosi(Mat mat) {

		Point[] arr = new Point[centres.size()];
		// String fileName = "BBC.txt";
		Long ss = System.nanoTime();
		centres.toArray(arr);
		// Sort the array based on the x coordinate of the centres
		Arrays.sort(arr, new Comparator<Point>() {
			public int compare(Point a, Point b) {
				int xComp = Double.compare(a.x, b.x);
				// if (xComp == 0)
				// return Double.compare(a.y, b.y);
				// else
				return xComp;
			}
		});
		// get the ids in the sorted order
		int[] ids = new int[centres.size()];
		for (int i = 0; i < centres.size(); i++) {
			ids[i] = centres.indexOf(arr[i]);
		}
		// Long se = System.nanoTime();
		// Long sd = se - ss;
		// String logs = ss.toString() + "," + se.toString() + "," +
		// sd.toString();
		// writeToFile(fileName,logs);
		int counter = 0;

		ArrayList<Integer> rem = new ArrayList<Integer>();
		for (int i = 0; i < centres.size(); i++) {
			long ps = System.nanoTime();
			ArrayList<Integer> cardList = new ArrayList<Integer>();
			// cardList.add(0);
			ArrayList<Point> tempList = new ArrayList<Point>();
			ArrayList<Integer> tempId = new ArrayList<Integer>();
			double scale = per.get(ids[i]) / 4;
			if (!rem.contains(ids[i])) {
				for (int j = i; j < centres.size()
						&& Math.abs(arr[i].x - arr[j].x) < 1.6 * scale; j++) {
					double iArea = areas.get(ids[i]);
					double jArea = areas.get(ids[j]);
					// Area constraint important to check
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}
				}
				for (int j = i - 1; j > 0
						&& Math.abs(arr[i].x - arr[j].x) < 1.6 * scale; j--) {
					double iArea = areas.get(i);
					double jArea = areas.get(j);
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}

				}

				for (Integer j = 0; j < tempList.size(); j++) {

					if (Math.abs(arr[i].y - tempList.get(j).y) < 1.6 * scale) {
						cardList.add(centres.indexOf(tempList.get(j)));
						rem.add(centres.indexOf(tempList.get(j)));

					}
				}

				if (cardList.size() > 2) {
					cardsTable.put(counter, cardList);
					long pe = System.nanoTime();
					Long cardTime = pe - ps;
					counter++;
				}
				// cardList.clear();
			}

		}

		Log.d("Hello", "");
		// return mat;
	}

	private Mat chitraBanao(Mat mat, Mat tmp, Mat bnw) {
		Integer option = 0;
		double idcx = 0;
		double idcy = 0;
		float[] radius = new float[1];
		// eliminate4();
		for (Integer i = 0; i < cardsTable.size(); i++) {
			// ArrayList<Integer> tmpId = new ArrayList<Integer>();
			int cIndex = cardsTable.get(i).get(0);

			Point[] arr = new Point[3];
			Point p1 = centres.get(cardsTable.get(i).get(0));

			p1 = centerFix(p1, tmp);
			arr[0] = p1;

			Point p2 = centres.get(cardsTable.get(i).get(1));
			// / Scalar(255,0,0),2);
			p2 = centerFix(p2, tmp);
			arr[1] = p2;

			Point p3 = centres.get(cardsTable.get(i).get(2));

			p3 = centerFix(p3, tmp);
			arr[2] = p3;

			double cx = (p1.x + p2.x + p3.x) / 3;
			double cy = (p1.y + p2.y + p3.y) / 3;

			// Rounding cx and cy
			double c1 = bnw.get((int) p1.y, (int) p1.x)[0];
			double c2 = bnw.get((int) p2.y, (int) p2.x)[0];
			double c3 = bnw.get((int) p3.y, (int) p3.x)[0];
			if (c1 == 0.0 && c2 == 0.0 && c3 == 0.0) {

				MatOfPoint2f pMat = new MatOfPoint2f(arr);

				Point hole = new Point();
				int piv = 0;
				Imgproc.minEnclosingCircle(pMat, hole, radius);

				double valX = cx - hole.x;
				double valY = cy - hole.y;
				boolean right = false;
				Point pivot = new Point();
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

					// hole will be the mid point of the id centre and pivot
					// centre
					Point idc = new Point(2 * hole.x - pivot.x, 2 * hole.y
							- pivot.y);
					// Point idNew = centerFixID
					Double angle = (double) 0;
					Double theta = (double) 0;
					if (valX > 0 && valY > 0) {
						option = 4;
						// optionCount[3]++;
						// swapping p1 and p3 to ensure that p3 is the left most
						// anchor
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX > 0 && valY < 0) {
						option = 1;
						// optionCount[0]++;
						if (p1p < p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY > 0) {
						option = 3;
						// optionCount[2]++;
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY < 0) {
						option = 2;

					} else {
						option = 0;
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
					// double[] tempCol1 = tmp.get(945, 1313);
					a0.x = (id2.x + idAvg.x) / 2;
					a0.y = (id2.y + idAvg.y) / 2;
					// tempCol = (int) bnw.get((int)a0.y, (int)a0.x)[0];
					tempId.add(tempCol);

					/*a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a1.y, (int) a1.x)[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a2.y, (int) a2.x)[0];
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

					tempCol = (int) tmp.get((int) a3.y, (int) a3.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a4.y, (int) a4.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a5.y, (int) a5.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a6.y, (int) a6.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a7.y, (int) a7.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);*/
					
					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) bnw.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) bnw.get((int) a1.y, (int) a1.x)[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) bnw.get((int) a2.y, (int) a2.x)[0];
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

					tempCol = (int) bnw.get((int) a3.y, (int) a3.x)[0];
					tempId.add(tempCol);

					tempCol = (int) bnw.get((int) a4.y, (int) a4.x)[0];
					tempId.add(tempCol);

					tempCol = (int) bnw.get((int) a5.y, (int) a5.x)[0];
					tempId.add(tempCol);

					tempCol = (int) bnw.get((int) a6.y, (int) a6.x)[0];
					tempId.add(tempCol);

					tempCol = (int) bnw.get((int) a7.y, (int) a7.x)[0];
					tempId.add(tempCol);

					tempCol = (int) bnw.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);
					
					Integer idVal = bin2dec(tempId);
					
					tempId.clear();

					Long elapsedTime = System.nanoTime() - entryTime;
					//elapsedFromStart = System.nanoTime() - startTime;
					
					//Integer cards = cardsTable.size();
					timeLog.append(i.toString());
					timeLog.append("      ");
					timeLog.append(idVal.toString());
					timeLog.append("      ");
					timeLog.append(elapsedTime.toString());
					timeLog.append('\n');

					// Log everything
					if (option > 0) {
						if (!tempAns.isEmpty()) {
							if (tempAns.containsKey(idVal)) {
								ArrayList<Integer> ansList = tempAns.get(idVal);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal, ansList);

								Core.putText(mat, idVal.toString(), hole,
										Core.FONT_HERSHEY_SIMPLEX, 1,
										new Scalar(255, 0, 0), 3);
							} else {
								ArrayList<Integer> ansList = new ArrayList<Integer>();
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal, ansList);

								Core.putText(mat, idVal.toString(), hole,
										Core.FONT_HERSHEY_SIMPLEX, 1,
										new Scalar(255, 0, 0), 3);
							}
						} else {
							ArrayList<Integer> ansList = new ArrayList<Integer>();
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.set(option - 1, ansList.get(option - 1) + 1);
							tempAns.put(idVal, ansList);

							Core.putText(mat, idVal.toString(), hole,
									Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(
											255, 0, 0), 3);
						}
					}

					idX.add(idcx);
					idY.add(idcy);
					holes.add(hole);
				}

			}

		}
		
		Long elapsedTime = System.nanoTime() - entryTime;
		elapsedFromStart = System.nanoTime() - startTime;
		
		Integer cards = cardsTable.size();
		timeLog.append(cards.toString());
		timeLog.append("      ");
		timeLog.append(elapsedTime.toString());

		return mat;
	}

	private Mat chitraBanao(Mat mat, Mat tmp, Mat bnw, Mat grey) {
		Integer option = 0;
		double idcx = 0;
		double idcy = 0;
		float[] radius = new float[1];
		// eliminate4();
		for (Integer i = 0; i < cardsTable.size(); i++) {
			// ArrayList<Integer> tmpId = new ArrayList<Integer>();
			int cIndex = cardsTable.get(i).get(0);

			Point[] arr = new Point[3];
			Point p1 = centres.get(cardsTable.get(i).get(0));

			p1 = centerFix(p1, tmp);
			arr[0] = p1;

			Point p2 = centres.get(cardsTable.get(i).get(1));
			// / Scalar(255,0,0),2);
			p2 = centerFix(p2, tmp);
			arr[1] = p2;

			Point p3 = centres.get(cardsTable.get(i).get(2));

			p3 = centerFix(p3, tmp);
			arr[2] = p3;

			double cx = (p1.x + p2.x + p3.x) / 3;
			double cy = (p1.y + p2.y + p3.y) / 3;

			// Rounding cx and cy
			double c1 = grey.get((int) p1.y, (int) p1.x)[0];
			double c2 = grey.get((int) p2.y, (int) p2.x)[0];
			double c3 = grey.get((int) p3.y, (int) p3.x)[0];
			if (c1 == 0.0 && c2 == 0.0 && c3 == 0.0) {

				MatOfPoint2f pMat = new MatOfPoint2f(arr);

				Point hole = new Point();
				int piv = 0;
				Imgproc.minEnclosingCircle(pMat, hole, radius);

				double valX = cx - hole.x;
				double valY = cy - hole.y;
				boolean right = false;
				Point pivot = new Point();
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

					// hole will be the mid point of the id centre and pivot
					// centre
					Point idc = new Point(2 * hole.x - pivot.x, 2 * hole.y
							- pivot.y);
					// Point idNew = centerFixID
					Double angle = (double) 0;
					Double theta = (double) 0;
					if (valX > 0 && valY > 0) {
						option = 4;
						// optionCount[3]++;
						// swapping p1 and p3 to ensure that p3 is the left most
						// anchor
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX > 0 && valY < 0) {
						option = 1;
						// optionCount[0]++;
						if (p1p < p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY > 0) {
						option = 3;
						// optionCount[2]++;
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}

					} else if (valX < 0 && valY < 0) {
						option = 2;

					} else {
						option = 0;
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
					// double[] tempCol1 = tmp.get(945, 1313);
					a0.x = (id2.x + idAvg.x) / 2;
					a0.y = (id2.y + idAvg.y) / 2;
					// tempCol = (int) grey.get((int)a0.y, (int)a0.x)[0];
					//tempId.add(tempCol);

					/*a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a1.y, (int) a1.x)[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) tmp.get((int) a2.y, (int) a2.x)[0];
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

					tempCol = (int) tmp.get((int) a3.y, (int) a3.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a4.y, (int) a4.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a5.y, (int) a5.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a6.y, (int) a6.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) a7.y, (int) a7.x)[0];
					tempId.add(tempCol);

					tempCol = (int) tmp.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);*/
					
					/*a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;*/
					tempCol = (int) grey.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) grey.get((int) a1.y, (int) a1.x)[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) grey.get((int) a2.y, (int) a2.x)[0];
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

					tempCol = (int) grey.get((int) a3.y, (int) a3.x)[0];
					tempId.add(tempCol);

					tempCol = (int) grey.get((int) a4.y, (int) a4.x)[0];
					tempId.add(tempCol);

					tempCol = (int) grey.get((int) a5.y, (int) a5.x)[0];
					tempId.add(tempCol);

					tempCol = (int) grey.get((int) a6.y, (int) a6.x)[0];
					tempId.add(tempCol);

					tempCol = (int) grey.get((int) a7.y, (int) a7.x)[0];
					tempId.add(tempCol);

					tempCol = (int) grey.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);
					
					Core.putText(mat, "o", idAvg,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "1", a0,
							Core.FONT_HERSHEY_SIMPLEX,0.3,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a1,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a2,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a3,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a4,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a5,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "o", a6,
							Core.FONT_HERSHEY_SIMPLEX, 0.2,
							new Scalar(255, 0, 0), 1);
					Core.putText(mat, "8", a7,
							Core.FONT_HERSHEY_SIMPLEX, 0.3,
							new Scalar(255, 0, 0), 1);
					
					
					Integer idVal = bin2dec(tempId);
					tempId.clear();

					// Log everything
					if (option > 0) {
						if (!tempAns.isEmpty()) {
							if (tempAns.containsKey(idVal)) {
								ArrayList<Integer> ansList = tempAns.get(idVal);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal, ansList);

								Core.putText(mat, idVal.toString(), hole,
										Core.FONT_HERSHEY_SIMPLEX, 1,
										new Scalar(255, 0, 0), 3);
							} else {
								ArrayList<Integer> ansList = new ArrayList<Integer>();
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal, ansList);

								Core.putText(mat, idVal.toString(), hole,
										Core.FONT_HERSHEY_SIMPLEX, 1,
										new Scalar(255, 0, 0), 3);
							}
						} else {
							ArrayList<Integer> ansList = new ArrayList<Integer>();
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.set(option - 1, ansList.get(option - 1) + 1);
							tempAns.put(idVal, ansList);

							Core.putText(mat, idVal.toString(), hole,
									Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(
											255, 0, 0), 3);
						}
					}

					idX.add(idcx);
					idY.add(idcy);
					holes.add(hole);
				}

			}

		}

		return mat;
	}

	
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

	private boolean checkWhite(Point p1, Mat tmp) {
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
		int colX = 0;
		int colY = 0;
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

		check = Math.ceil(p1.x);
		checkX = (int) (check * 10);
		checkedX = checkX / 10;
		tempX = checkedX;
		check2 = Math.ceil(p1.y);
		checkY = (int) (check2 * 10);
		checkedY = checkY / 10;

		if (tmp.get(checkedY, checkedX)[0] == 0) {
			return true;
		} else {
			return false;
		}
	}

	private int bin2dec(ArrayList<Integer> tmpId) {
		int sum = 0;
		for (int i = 0; i < tmpId.size(); i++) {
			int curBit = tmpId.get(tmpId.size()-i-1);
			if (curBit == 1) {
				sum += Math.pow(2, i);
			}

		}
		return sum;
	}

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

	public void cleanTheMess() {
		//frameCount++;
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
		contoursWhite.clear();
		cardTable.clear();
		cardsTable.clear();
		sameCenterTable.clear();
		idList.clear();
		idX.clear();
		idY.clear();
		holes.clear();
		idPoint.clear();
		// pList.clear();
		// aList.clear();
		// eList.clear();
		studentRecords.clear();
		questionStats.clear();
		tempAns.clear();
		finalAns.clear();
		idStore.clear();
	//	grayMatrix.release();
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		frameCounter++;
		if(frameCounter%1 == 0){
		// TODO Auto-generated method stub
if(toggleResult == 0){
	cleanTheMess();
}
		mRgba = inputFrame.rgba();
		Mat resMat = inputFrame.gray();
		/*if (mRgba != null) {
			resMat = processIt(mRgba);
		}*/
		Mat grayMat = inputFrame.gray();
		if (mRgba != null && grayMat != null) {

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
			contoursWhite.clear();
			cardTable.clear();
			cardsTable.clear();
			sameCenterTable.clear();
			idList.clear();
			idX.clear();
			idY.clear();
			holes.clear();
			idPoint.clear();

			idStore.clear();
		}
		if (!showBinarizeBool) {

			Mat resultMat = processIt(grayMat, mRgba);
			timeLog.append("      ");
			timeLog.append(elapsedFromStart.toString());
			timeLog.append("\n");
			Mat newMat = new Mat(mRgba.width(), mRgba.height(),
					CvType.CV_8UC4);

			Imgproc.cvtColor(grayMat, newMat, Imgproc.COLOR_GRAY2RGBA);
			return resultMat;
		} else {
			Mat filtered = mRgba;
			filtered = filterIt(grayMat);
			Mat newMat = new Mat(mRgba.width(), mRgba.height(),
					CvType.CV_8UC4);
			Imgproc.cvtColor(filtered, newMat, Imgproc.COLOR_GRAY2RGBA);
			return newMat;
		}
		}else{
			return inputFrame.gray();
		}
		//return resMat;
		// return mRgba;
	}

	private void listDetails(ArrayList<String> tempPre,
			ArrayList<String> tempAbs, ArrayList<String> tempExt) {
		String tvText = "Present: ";

		Iterator<String> it = tempPre.iterator();
		while (it.hasNext()) {
			tvText += it.next() + ", ";
		}

		tvText += "\n Absent";
		it = tempAbs.iterator();
		while (it.hasNext()) {
			tvText += it.next() + ", ";
		}

		tvText += "\n Extras";
		it = tempExt.iterator();
		while (it.hasNext()) {
			tvText += it.next() + ", ";
		}

		tvText += "\n Still Processing. These are intermediate results. When you have scanned through the whole class room, you'll have the true results";
		final String data = tvText;
		details = (TextView) findViewById(R.id.textView1);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				details.setText(data);
			}
		});
	}

	private void mapAnswers() throws IOException {
		
		
			getPresentList();
		
		Set<Integer> keyAns = tempAns.keySet();
		Iterator<Integer> itAns = keyAns.iterator();
		while (itAns.hasNext()) {
			Integer each = itAns.next();
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
			if (keyVal.getAttendanceBool()) {
				if (presentList.contains(each.toString())) {
					
					//finalAns - key=id maxIndex=answer
					finalAns.put(each, maxIndex);
				}
			} else {
				finalAns.put(each, maxIndex);
			}
		}

	}

	public void startStopPoll(View view) {
		if (toggleResult == 0) {
			toggleResult = 1;
			cumulativeOptions.clear();
			cleanTheMess();
			next.setText("Stop poll and View Results");
		} else {
			toggleResult = 0;
			loadNext(view);
			callChart(view);
			cumulativeOptions.clear();
			next.setText("Start poll");
		}
	}

	public void loadNext(View view) {

		// if (quizQuesCount < questCards.size()) {
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

			}
			if (studentRecords.containsKey(each)) {
				ArrayList<Integer> recList = studentRecords.get(each);
				int i = 0;
				// Necessary to ensure that answers are logged in correct order.
				// If a card is not present in some frame, put 5 as the response
				while (recList.size() < qid ) {
					recList.add(recList.size() - 1 + i, 5);
					i++;
				}
				recList.add(ans);
				studentRecords.put(each, recList);
			} else {
				ArrayList<Integer> recList = new ArrayList<Integer>();
				int i = 0;
				while (recList.size() < qid ) {
					recList.add(i, 5);
					i++;
				}
				recList.add(ans);
				studentRecords.put(each, recList);
			}
		}
		final ArrayList<Integer> aOptions = new ArrayList<Integer>();
		aOptions.add(a);
		aOptions.add(b);
		aOptions.add(c);
		aOptions.add(d);

		if (cumulativeOptions.isEmpty()) {
			cumulativeOptions.add(a);
			cumulativeOptions.add(b);
			cumulativeOptions.add(c);
			cumulativeOptions.add(d);

		} else {

			// This section is same as the Quiz part but in quick poll we do not
			// want cumulative responses so the below section is commented out

			/*
			 * a += cumulativeOptions.get(0); b += cumulativeOptions.get(1); c
			 * += cumulativeOptions.get(2); d += cumulativeOptions.get(3);
			 */

			cumulativeOptions.add(0, a);
			cumulativeOptions.add(1, b);
			cumulativeOptions.add(2, c);
			cumulativeOptions.add(3, d);
		}

		qid++;
		questionStats.put(qid, aOptions);
		quesOrder.add(qid);

		

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
		contoursWhite.clear();
		cardTable.clear();
		cardsTable.clear();
		sameCenterTable.clear();
		idList.clear();
		idX.clear();
		idY.clear();
		holes.clear();
		idPoint.clear();
		tempAns.clear();
		finalAns.clear();
		idStore.clear();

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
				finishPoll(v);
				next.setVisibility(View.GONE);
				finishDialog.dismiss();
			}
		});
		finishDialog.show();
	}

	public void finishPoll(View view) {
		callChart(view);
		/*
		 * sb will store the data related to each question. each line in the
		 * string is of the form [qid, num of A, num of B, num of C, num of D]
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("qid, As, Bs, Cs, Ds");
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
			sb.append("\n");
		}

		StringBuilder qb = new StringBuilder();
		Set<Integer> recKeys = studentRecords.keySet();
		Iterator<Integer> it = recKeys.iterator();
		while (it.hasNext()) {
			Integer each = it.next();
			ArrayList<Integer> opt = studentRecords.get(each);
			qb.append(each);
			// qb.append(",");
			for (int i = 0; i < opt.size(); i++) {
				qb.append(",");
				qb.append(opt.get(i));

			}
			qb.append("\n");
		}
		saveAsNew(view, "Save Results", sb.toString(), qb.toString());
		pollFinished = true;
		int keygen = 0;
		/* generate a key and save the question and student record file names */
	}

	public void saveAsNew(View v, String title, String content,
			String studentStats) {
		final String fileContent = content;
		final String studentData = studentStats;
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
						saveNewPollFile(studentData, fileContent, fName
								.getText().toString());
						dial.dismiss();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					saveAsNew(v, "File Name already used!", fileContent,
							studentData);
				}
			}
		});
		dial.show();
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
				if (appDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");

			} else {
				File saveIt = new File(appDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(content.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");

			}

			if (!studentDirectory.exists()) {
				if (studentDirectory.mkdir()) {
					Log.e(TAG, "Directory Created");
				}
				File saveIt = new File(studentDirectory, fileName + "csv");
				fos = new FileOutputStream(saveIt);
				fos.write(studentData.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
			} else {
				File saveIt = new File(studentDirectory, fileName + ".csv");
				fos = new FileOutputStream(saveIt);
				fos.write(studentData.getBytes());
				fos.close();
				Log.e(TAG, "Directory Created");
			}
		} else {
			Log.e(TAG, "SD Card unavailable");
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
				+ "/QCards/PollResults");
		if (new File(appDirectory + "/" + file_name + ".csv").exists()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * private void showQuesDialog(View view) { dial = new
	 * Dialog(view.getContext()); dial.setContentView(R.layout.quiz_dialog);
	 * dial.setTitle("Question"); TextView numView = (TextView)
	 * dial.findViewById(R.id.quesNum); TextView aView = (TextView)
	 * dial.findViewById(R.id.opA); TextView bView = (TextView)
	 * dial.findViewById(R.id.opB); TextView cView = (TextView)
	 * dial.findViewById(R.id.opC); TextView dView = (TextView)
	 * dial.findViewById(R.id.opD); TextView qView = (TextView)
	 * dial.findViewById(R.id.quest); Button pollButton = (Button)
	 * dial.findViewById(R.id.eachPoll);
	 * 
	 * Integer seq = quizQuesCount+1; numView.setText(seq.toString());
	 * aView.setText(questCards.get(quizQuesCount).getA());
	 * bView.setText(questCards.get(quizQuesCount).getB());
	 * cView.setText(questCards.get(quizQuesCount).getC());
	 * dView.setText(questCards.get(quizQuesCount).getD());
	 * qView.setText(questCards.get(quizQuesCount).getQuestion());
	 * 
	 * // Increment the question number quizQuesCount++;
	 * 
	 * // final EditText fName = (EditText) //
	 * dial.findViewById(R.id.newQSetName); pollButton.setOnClickListener(new
	 * View.OnClickListener() { public void onClick(View v) { //
	 * onDialogButton(v); dial.dismiss();
	 * 
	 * } }); dialogOn = true; dial.show(); }
	 */
	public void callChart(View view) {
		/*
		 * Dialog d = new Dialog(view.getContext());
		 * d.setContentView(R.layout.chart_dialog);
		 */
		final BarChart chart;
		// //////////////////////////////////////////////

		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
		// Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		final View v = view;
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		View dialogView = inflater.inflate(R.layout.chart_dialog, null);
		builder.setView(dialogView)
				// Add action buttons
				.setPositiveButton(R.string.start_new_poll,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								cleanTheMess();
							}
						})
				.setNegativeButton(R.string.quit_poll,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent homeIntent = new Intent(v.getContext(),
										JumpInMain.class);
								startActivity(homeIntent);
							}
						});

		chart = (BarChart) dialogView.findViewById(R.id.chart);
		// //////////////////////////
		chart.setTouchEnabled(true);
		chart.setPinchZoom(true);

		ArrayList<BarEntry> valsComp1 = new ArrayList<BarEntry>();
		if (!cumulativeOptions.isEmpty()) {
			BarEntry c1e1 = new BarEntry(cumulativeOptions.get(0), 0); // 0 ==
																		// quarter
																		// 1
			valsComp1.add(c1e1);
			BarEntry c1e2 = new BarEntry(cumulativeOptions.get(1), 1); // 1 ==
																		// quarter
																		// 2
																		// ...
			BarEntry c1e3 = new BarEntry(cumulativeOptions.get(2), 2); // 1 ==
																		// quarter
																		// 2
																		// ...
			BarEntry c1e4 = new BarEntry(cumulativeOptions.get(3), 3); // 1 ==
			// quarter
			//BarEntry c1e5 = new BarEntry(cumulativeOptions.get(4), 4); // 1 ==
			// quarter
															// 2
																		// ...
			valsComp1.add(c1e2);
			valsComp1.add(c1e3);
			valsComp1.add(c1e4);
			//valsComp1.add(c1e5);

			BarDataSet setComp1 = new BarDataSet(valsComp1, "Responses");
			ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
			dataSets.add(setComp1);

			ArrayList<String> xVals = new ArrayList<String>();
			xVals.add("A");
			xVals.add("B");
			xVals.add("C");
			xVals.add("D");
		//	xVals.add("None");

			BarData data = new BarData(xVals, dataSets);
		
			chart.setData(data);
			chart.setDescription("");
			XAxis xAxis = chart.getXAxis();
			if(cumulativeOptions.size() == 0){
				xAxis.setDrawLabels(false);
				
			}else{
				xAxis.setDrawLabels(true);
			}
			xAxis.setAdjustXLabels(true);
			
			xAxis.setDrawAxisLine(true);
			chart.invalidate(); // refresh
		}
		AlertDialog alertDialog = builder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		// d.show();
	}
	
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
		final Switch apply_adaptive = (Switch) dialog.findViewById(R.id.apply_adaptive);

		// set states to display in the dialog
		highlight_cards.setChecked(highlightCardBool);
		hide_missing.setChecked(!showMissingBool);
		show_binarize.setChecked(showBinarizeBool);
		apply_roster.setChecked(!applyRosterBool);
		apply_qset.setChecked(!applyQsetBool);
		apply_adaptive.setChecked(adaptiveBool);
		
		if(!keyVal.getRosterBool()){
			apply_roster.setVisibility(View.GONE);
		}

		
		apply_qset.setVisibility(View.GONE);
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

	public void highlightCards(View view) {
		String toastText = null;
		if(highlightCardBool){
			toastText = "Highlighting cards now";
		}else{
			toastText = "Switching off cards highlighting";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}
	
	public void applyAdaptive(View view) {
		String toastText = null;
		if(adaptiveBool){
			toastText = "Now using Adaptive Threshold. This will slow down" +
					" the app but might give better results in varying lighting conditions.";
		}else{
			toastText = "Switching to normal thresholding from Adaptive thresholding.";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	public void hideMissing(View view) {
		String toastText = null;
		if(highlightCardBool){
			toastText = "Highlighting cards now";
		}else{
			toastText = "Switching off cards highlighting";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	public void showBinarize(View view) {
		String toastText = null;
		if(showBinarizeBool){
			toastText = "Now showing the binarized view";
		}else{
			toastText = "Switching to normal color mode";
		}
		Toast settingToast = Toast.makeText(view.getContext(), toastText,
				Toast.LENGTH_SHORT);
		settingToast.show();
	}

	public void applyRoster(View view) {
		keyVal.setManageBool(false);
		keyVal.setRosterAttendanceBool(true);
		setFromPoll();
		Intent intent = new Intent(this,RosterList.class);
		startActivity(intent);
		finish();
	}

	public void applyQset(View view) {
		Intent i = new Intent(view.getContext(), QSetList.class);
		startActivity(i);
		finish();
	}
	
	public static void setFromPoll(){
		fromPoll = true;
	}


public static void unsetFromPoll(){
	fromPoll = false;
}


public void getPresentList() throws IOException {
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
		while((line = bufRead.readLine())!=null){
			presentList.add(line);
		}
	}
	Log.e(TAG,"view list");
}
}
