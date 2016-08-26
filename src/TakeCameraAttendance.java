package com.abhinav.qcards;

import java.io.IOException;
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
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class TakeCameraAttendance extends Activity implements
		CvCameraViewListener2 {
	private CameraBridgeViewBase mOpenCvCameraView;
	TextView details;
	Mat mRgba;
	long cdst = 0;
	long cdet = 0;
	int mHeight;
	int mWidth;
	ImageView pic;

	int thresh_otsu = CameraSettings.get_otsu();
	int thresh_inv = CameraSettings.get_inv();
	boolean blur = CameraSettings.get_blur();
	boolean adaptive = false;

	private static ArrayList<String> pList = new ArrayList<String>();
	private static ArrayList<String> aList = new ArrayList<String>();
	private static ArrayList<String> eList = new ArrayList<String>();

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
	int a = 0;
	int b = 0;
	int c = 0;
	int d = 0;
	Hashtable<Integer, ArrayList<Integer>> idFrequency = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, ArrayList<Integer>> tempAns = new Hashtable<Integer, ArrayList<Integer>>();
	Hashtable<Integer, Integer> finalAns = new Hashtable<Integer, Integer>();
	Hashtable<Integer,ArrayList<Integer>> idMapping = new Hashtable<Integer,ArrayList<Integer>>();
	Hashtable<Integer, Integer> idDecode = new Hashtable<Integer, Integer>();
	Hashtable<Integer,ArrayList<Integer>> ansMapping = new Hashtable<Integer,ArrayList<Integer>>();
	List<Integer> checkCont = new ArrayList<Integer>();
	// getting the path to External Storage Gallery
	//final String galleryPath = Environment.getExternalStoragePublicDirectory(
	//		Environment.DIRECTORY_PICTURES).toString();
	// The line commented below causes error in MotoG
	// final String photoPath = galleryPath+"/Second Sight/card1.png";
	//final String photoPath = "storage/sdcard1/Pictures/Second Sight/25per1.png";

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
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.take_camera_attendance);
		// setContentView(R.layout.quick_poll);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.display_java_surface_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		try {
			idMapping = MappingReader.loadMapping();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prepareIdMap();
		prepareAnsMap();
	}
	
	public void prepareAnsMap(){
		Set<Integer> idMapKeys = idMapping.keySet();
		Iterator<Integer> it = idMapKeys.iterator();
		while(it.hasNext()){
			ArrayList<Integer> al = new ArrayList<Integer>();
			ArrayList<Integer> ans = new ArrayList<Integer>();
			al = idMapping.get(it.next());
			Integer mapKey = al.get(0);
			for(int i =1; i<al.size();i++){
				ans.add(i);
			}
			ansMapping.put(mapKey, ans);
		}
	}
	
	public void prepareIdMap(){
		Set<Integer> idMapKeys = idMapping.keySet();
		Iterator<Integer> it = idMapKeys.iterator();
		while(it.hasNext()){
			Integer key = it.next();
			Integer value = idMapping.get(key).get(0);
			idDecode.put(key, value);
		}
	}

	// This is where all those methods are called that are required for
	// processing the image
	// First the image is fetched from the SD Card, Converted to a Matrix and
	// then processed

	protected Mat processIt(Mat grayMat, Mat rgbaMat) {

		Mat grayM = grayMat;
		Mat colorM = rgbaMat;
		// This gives us a 'grey' matrix holding greyscale values
		// Imgproc.cvtColor(tmp, grey, CvType.CV_8UC1);
		// Mat grey = new Mat(mWidth,mHeight,CvType.CV_8UC1);

		// Get a Filtered(Otsu_Threshhold followed by Binary Inversion) image
		Mat filtered = filterIt(grayM);

		// detectBlack method detects the contours and calculates the parameters
		Mat detectResults = detectBlack(filtered, colorM);

		/*
		 * Mat returnColor = new Mat(filtered.cols(), filtered.rows(),
		 * CvType.CV_8UC4);
		 * 
		 * Imgproc.cvtColor(filtered, returnColor, Imgproc.COLOR_GRAY2RGBA);
		 */

		if (detectResults != null) {
			return detectResults;
		} else {
			return colorM;
		}

	}

	private Mat detectBlack(Mat filtered, Mat colorMat) {

		Integer totContours = 0;
		Integer finContours = 0;

		cdst = System.nanoTime();
		/*Mat hierarchy = new Mat();

		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> tempContours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);
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
				if (pArea > 5 * cArea && pArea < 13 * cArea) {
					if (!tempContours.contains(mContours.get(i))) {
						tempContours.add(mContours.get(i));
					}
					if (!tempContours.contains(mContours.get((int) hVal[2]))) {
						tempContours.add(mContours.get((int) hVal[2]));
					}
				}
			}
		}

		// Iterator<MatOfPoint> each1 = mContours.iterator();
		Iterator<MatOfPoint> each1 = tempContours.iterator();
		*/
		
		// DO NOT forget to clear the contours list
		// mContours.clear();
		contours.clear();

		Mat hierarchy = new Mat();
		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);
		totContours = mContours.size();
		// Checking if individual contours can be retrieved

		Iterator<MatOfPoint> each1 = mContours.iterator();
		while (each1.hasNext()) {
			MatOfPoint wrapper = each1.next();
			if (Imgproc.contourArea(wrapper) < 86400
					&& Imgproc.contourArea(wrapper) >= 35) {
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

		Mat mat = colorMat;

		Long grs = System.nanoTime();
		findPadosi(mat);

		mat = chitraBanao(mat, colorMat, filtered);
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
	

	// Blur and dilation
	public Mat filterIt(Mat mat) {
		Mat grey = mat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		Mat dilated = new Mat();
		if (blur == true) {
			Imgproc.GaussianBlur(grey, tmp2, new Size(5, 5), 0);
		}
		Imgproc.threshold(tmp2, tmp2, 120, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		Imgproc.threshold(tmp2, tmp1, 0, 255,Imgproc.THRESH_BINARY_INV);
//		Imgproc.adaptiveThreshold(tmp2, tmp1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
		if(adaptive){
		Imgproc.adaptiveThreshold(tmp1, tmp1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
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
		/*
		 * if (id == R.id.action_settings) { return true; }
		 */
		return super.onOptionsItemSelected(item);
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
				if (xComp == 0)
					return Double.compare(a.y, b.y);
				else
					return xComp;
			}
		});
		// get the ids in the sorted order
		int[] ids = new int[centres.size()];
		for (int i = 0; i < centres.size(); i++) {
			ids[i] = centres.indexOf(arr[i]);
		}
		Long se = System.nanoTime();
		Long sd = se - ss;
		String logs = ss.toString() + "," + se.toString() + "," + sd.toString();
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
						&& Math.abs(arr[i].x - arr[j].x) < 2 * scale; j++) {
					double iArea = areas.get(ids[i]);
					double jArea = areas.get(ids[j]);
					// Area constraint important to check
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}
				}
				for (int j = i - 1; j > 0
						&& Math.abs(arr[i].x - arr[j].x) < 2 * scale; j--) {
					double iArea = areas.get(i);
					double jArea = areas.get(j);
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}

				}

				for (Integer j = 0; j < tempList.size(); j++) {

					if (Math.abs(arr[i].y - tempList.get(j).y) < 2 * scale) {
						cardList.add(centres.indexOf(tempList.get(j)));
						rem.add(centres.indexOf(tempList.get(j)));
						// Core.putText(mat, j.toString(),
						// centres.get(centres.indexOf(tempList.get(j))),Core.FONT_HERSHEY_SIMPLEX,
						// 0.7, new Scalar(0,0,255),1);

						// count++;
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
					/*
					 * Point idp = pointId(pivot, p2, p3); Core.putText(mat,
					 * "0", idp,Core.FONT_HERSHEY_SIMPLEX, 0.7, new
					 * Scalar(0,0,255),3);
					 */}
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
					/*
					 * Point idp = pointId(pivot, p1, p2); Core.putText(mat,
					 * "0", idp,Core.FONT_HERSHEY_SIMPLEX, 0.7, new
					 * Scalar(0,0,255),3);
					 */
				}

				// //>>>>>
				int p1p = (int) (pivot.x - p1.x);
				int p3p = (int) (pivot.x - p3.x);
				/*
				 * else{ p1 = p3; p3 = p2; p2 = pivot; }
				 */

				// Core.putText(mat, "o", hole,Core.FONT_HERSHEY_SIMPLEX, 0.1,
				// new Scalar(255,0,0),1);

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
					// Core.putText(mat, "o", idc,Core.FONT_HERSHEY_SIMPLEX,
					// 0.1, new Scalar(0,0,255),1);
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

						/*
						 * i1.x = (hole.x+idc.x)/2; i1.y = (hole.y+idc.y)/2;
						 * Core.putText(mat, "o", i1,Core.FONT_HERSHEY_SIMPLEX,
						 * 0.1, new Scalar(0,255,0),1); i5.x = (2*idc.x-i1.x);
						 * i5.y = (2*idc.y-i1.y); Core.putText(mat, "o",
						 * i5,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
						 * Scalar(0,255,0),1);
						 */
						// Double angx = hole.x-idc.x;
						// Double angy = hole.y-idc.y;
						// angle = (double)(Math.atan2(angy,
						// angx)/0.0174532925)-45;
						// angle = (double)(Math.atan2(angy,
						// angx)/0.0174532925)-50;
						// angle = angle-50;
						//
						// String a = angle.toString();
						// Core.putText(mat, option.toString(),
						// hole,Core.FONT_HERSHEY_SIMPLEX, 0.8, new
						// Scalar(0,0,255),3);
						// Core.putText(mat, "o", p1,Core.FONT_HERSHEY_SIMPLEX,
						// 0.2, new Scalar(0,0,255),3);
						// Core.putText(mat, "o", p2,Core.FONT_HERSHEY_SIMPLEX,
						// 0.2, new Scalar(0,0,255),3);
						// Core.putText(mat, "o", p3,Core.FONT_HERSHEY_SIMPLEX,
						// 0.2, new Scalar(0,0,255),3);

					} else if (valX > 0 && valY < 0) {
						option = 1;
						// optionCount[0]++;
						if (p1p < p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}
						/*
						 * Double angx = hole.x-idc.x; Double angy =
						 * idc.y-hole.y; angle = 45-(double)(Math.atan2(angy,
						 * angx)/0.0174532925); // theta = String a =
						 * angle.toString();
						 */
						// Core.putText(mat, "0",
						// optDot,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
						// Scalar(255,255,5),2);
						// Point idp = pointId(pivot,p1,p3);
						// double ang1 = pointId(pivot,p1,p3);

						// Core.putText(mat,
						// option.toString(),hole,Core.FONT_HERSHEY_SIMPLEX,
						// 0.8, new Scalar(0,0,255),3);
					} else if (valX < 0 && valY > 0) {
						option = 3;
						// optionCount[2]++;
						if (p1p > p3p) {
							Point temp = p1;
							p1 = p3;
							p3 = temp;
						}
						/*
						 * Double angx = idc.x-hole.x; Double angy =
						 * hole.y-idc.y; angle = 45-(double)(Math.atan2(angy,
						 * angx)/0.0174532925); String a = angle.toString();
						 */
						// Core.putText(mat, "0",
						// optDot,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
						// Scalar(255,0,255),2);
						// Core.putText(mat,
						// option.toString(),hole,Core.FONT_HERSHEY_SIMPLEX,
						// 0.8, new Scalar(0,0,255),3);
					} else if (valX < 0 && valY < 0) {
						option = 2;
						// optionCount[1]++;
						/*
						 * Double angx = idc.x-hole.x; Double angy =
						 * idc.y-hole.y; angle = (double)(Math.atan2(angy,
						 * angx)/0.0174532925)-45; String a = angle.toString();
						 */
						// Core.putText(mat, "0",
						// optDot,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
						// Scalar(0,0,255),2);
						// Core.putText(mat, option.toString(),
						// hole,Core.FONT_HERSHEY_SIMPLEX, 0.8, new
						// Scalar(0,0,255),3);
					} else {
						option = 0;
					}

					// ////////////////////////////////////////////////////
					/*
					 * Point m1p = new Point(); Point m2p = new Point();
					 * 
					 * m1p.x = (pivot.x+p1.x)/2; m1p.y = (pivot.y+p1.y)/2; m2p.x
					 * = (pivot.x+p3.x)/2; m2p.y = (pivot.y+p3.y)/2;
					 * 
					 * Point id1 = new Point((2*hole.x - m1p.x),(2*hole.y -
					 * m1p.y)); Point id2 = new Point((2*hole.x -
					 * m2p.x),(2*hole.y - m2p.y)); Point idc1 = new
					 * Point((2*id2.x - p1.x),(2*id2.y - p1.y)); Point idc2 =
					 * new Point((2*id1.x - p3.x),(2*id1.y - p3.y)); Point idAvg
					 * = new Point();
					 * 
					 * idAvg.x = (idc.x+idc1.x+idc2.x)/3; idAvg.y =
					 * (idc.y+idc1.y+idc2.y)/3;
					 * 
					 * Point a0 = new Point(); Point a1 = new Point(); Point a2
					 * = new Point(); Point a3 = new Point(); Point a4 = new
					 * Point(); Point a5 = new Point(); Point a6 = new Point();
					 * Point a7 = new Point();
					 * 
					 * a0.x = (id2.x+idAvg.x)/2; a0.y = (id2.y+idAvg.y)/2;
					 * 
					 * a4.x = (2*idAvg.x-a0.x); a4.y = (2*idAvg.y-a0.y);
					 * 
					 * a2.x = (id1.x+idAvg.x)/2; a2.y = (id1.y+idAvg.y)/2;
					 * 
					 * a6.x = (2*idAvg.x-a2.x); a6.y = (2*idAvg.y-a2.y);
					 * 
					 * a1.x = (hole.x+idAvg.x)/2; a1.y = (hole.y+idAvg.y)/2;
					 * 
					 * a5.x = (2*idAvg.x-a1.x); a5.y = (2*idAvg.y-a1.y);
					 * 
					 * a7.x = (2*a6.x-a5.x); a7.y = (2*a6.y-a5.y);
					 * 
					 * a3.x = (2*idAvg.x-a7.x); a3.y = (2*idAvg.y-a7.y);
					 * 
					 * Core.putText(mat, "1p",m1p,Core.FONT_HERSHEY_SIMPLEX,
					 * 0.4, new Scalar(255,0,0),1); Core.putText(mat,
					 * "2p",m2p,Core.FONT_HERSHEY_SIMPLEX, 0.4, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "d1",id1,Core.FONT_HERSHEY_SIMPLEX, 0.4, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "d2",id2,Core.FONT_HERSHEY_SIMPLEX, 0.4, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",idAvg,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a0,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a4,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a2,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a6,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a1,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a5,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a7,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * "o",a3,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1);
					 */
					// Core.putText(mat, "o",idc2,Core.FONT_HERSHEY_SIMPLEX,
					// 0.1, new Scalar(0,0,255),1);
					/*
					 * switch(option){ case 1: theta = (270+angle)*0.0174532925;
					 * break; case 2: theta = (270+angle-90)*0.0174532925;
					 * break; case 3: theta = (270+angle+180)*0.0174532925;
					 * break; case 4: theta = (270+angle+90-5)*0.0174532925;
					 * break;
					 * 
					 * } rad =radius[0]/2;
					 */
					// theta = (270+angle)*0.0174532925;
					/*
					 * for(int j=0;j<8;j++){
					 * 
					 * int xdash=0; int ydash = 0; if(j%2 == 1){ double newrad =
					 * rad; xdash = (int)(idc.x + (newrad)*Math.cos(theta));
					 * ydash = (int)(idc.y + (newrad)*Math.sin(theta)); }else{
					 * xdash = (int)(idc.x + (rad)*Math.cos(theta)); ydash =
					 * (int)(idc.y + (rad)*Math.sin(theta)); } Point shift = new
					 * Point(xdash,ydash); Core.putText(mat, "o",
					 * shift,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); theta+=(45*0.0174532925); // double[]
					 * colArr = filtered.get(xdash, ydash); // if(colArr[0] ==
					 * 1){ // Core.putText(mat, "o",
					 * shift,Core.FONT_HERSHEY_SIMPLEX, 0.2, new
					 * Scalar(0,255,255),3); // } Integer count = j; //
					 * Core.putText(mat, count.toString(),
					 * shift,Core.FONT_HERSHEY_SIMPLEX, 0.4, new
					 * Scalar(255,0,0),1); // Core.putText(mat, "o",
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(0,0,255),1); // Core.putText(mat, "o",
					 * pivot,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(0,0,255),1); // Core.circle(mat, idc, 10, new
					 * Scalar(255,255,255)); // Integer colIntensity =
					 * tmp.get(ydash, xdash, matBuff); // colIntensity =
					 * colIntensity/tmp.channels(); //Point pn = new Point(290)
					 * double[] data = tmp.get(ydash, xdash); Integer intCol =
					 * (int) data[0]; tmpId.add(intCol); // Core.putText(mat,
					 * intCol.toString(), shift,Core.FONT_HERSHEY_SIMPLEX, 0.6,
					 * new Scalar(0,0,255),2);
					 * 
					 * Log.d("Intensity","Checking GreyScale"); } double[] data
					 * = tmp.get((int) idc.y, (int) idc.x); Integer intCol =
					 * (int) data[0]; tmpId.add(intCol); idStore.put(i,tmpId);
					 * Core.circle(mat, hole, (int) radius[0], new
					 * Scalar(255,0,0)); Integer decId = bin2dec(tmpId);
					 * Core.putText(mat, decId.toString(),
					 * idc,Core.FONT_HERSHEY_SIMPLEX, 0.6, new
					 * Scalar(0,0,255),2); idcx = hole.x - 3*valX; idcy = hole.y
					 * - 3*valY;
					 */
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
					//tempId.add(tempCol);
					tempCol = (int) tmp.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);
					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					/*tempCol = (int) tmp.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);*/

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
					
					tempCol = (int) tmp.get((int) a0.y, (int) a0.x)[0];
					tempId.add(tempCol);
				/*	tempCol = (int) tmp.get((int) idAvg.y, (int) idAvg.x)[0];
					tempId.add(tempCol);*/
					Integer idVal = bin2dec(tempId);
					tempId.clear();

					// Core.putText(mat, idVal.toString(),
					// hole,Core.FONT_HERSHEY_SIMPLEX, 2, new
					// Scalar(0,0,255),3);
					// >>>>>>>>>
					// This block logs the options with their corresponding IDs
					// and also records the frequency of options
					/*
					 * if(frameStore.isEmpty()){ ArrayList<Integer> fs = new
					 * ArrayList<Integer>(); fs.add(frameCount);
					 * frameStore.put(idVal, fs); }else{
					 * if(frameStore.containsKey(idVal)){ ArrayList<Integer> fs
					 * = frameStore.get(idVal); if(fs.size()<3){
					 * fs.add(frameCount); frameStore.put(idVal, fs); }else
					 * if(fs.size() == 3){ int f3 = fs.get(2); int f2 =
					 * fs.get(1); int f1 = fs.get(0); if(f3-f2 != 1){
					 * frameStore.remove(idVal); }else if(f2-f1 != 1){
					 * fs.remove(0); frameStore.put(idVal, fs); }else if(f3-f2
					 * == 1 && f2-f1 == 1){ if(!tempAns.isEmpty()){
					 * if(tempAns.containsKey(idVal)){ fs = tempAns.get(idVal);
					 * fs.set(option-1, fs.get(option-1)+1); tempAns.put(idVal,
					 * fs); Core.putText(mat, option.toString(),
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 1, new
					 * Scalar(0,0,255),2); }else{ ArrayList<Integer> listAns =
					 * new ArrayList<Integer>(); listAns.add(0); listAns.add(0);
					 * listAns.add(0); listAns.add(0); listAns.set(option-1,
					 * listAns.get(option-1)+1); tempAns.put(idVal, listAns);
					 * Core.putText(mat, option.toString(),
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 2, new
					 * Scalar(0,0,255),2); } }else{ ArrayList<Integer> listAns =
					 * new ArrayList<Integer>(); listAns.add(0); listAns.add(0);
					 * listAns.add(0); listAns.add(0); listAns.set(option-1,
					 * listAns.get(option-1)+1); tempAns.put(idVal, listAns);
					 * Core.putText(mat, option.toString(),
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 2, new
					 * Scalar(0,0,255),2); } } }else if(fs.size()>3){
					 * fs.clear(); } }else{ ArrayList<Integer> fs = new
					 * ArrayList<Integer>(); fs.add(frameCount);
					 * frameStore.put(idVal, fs); ArrayList<Integer> listAns =
					 * new ArrayList<Integer>(); listAns.add(0); listAns.add(0);
					 * listAns.add(0); listAns.add(0); listAns.set(option-1,
					 * listAns.get(option-1)+1); tempAns.put(idVal, listAns); }
					 * }
					 */
					// <<<<<<<<<<<<<<<<<<

					/*
					 * if(idFrequency.size() == 0){ idFrequency.put(idVal, 1);
					 * }else{ if(idFrequency.containsKey(idVal)){
					 * idFrequency.put(idVal, idFrequency.get(idVal)+1); }else{
					 * idFrequency.clear(); idFrequency.put(idVal, 1); } }
					 */

					/*
					 * if(idFrequency.containsKey(idVal)){ int freq =
					 * idFrequency.get(idVal) + 1; idFrequency.put(idVal, freq);
					 * }else{ idFrequency.put(idVal, 1); }
					 */
					// Core.putText(mat, idVal.toString(),
					// hole,Core.FONT_HERSHEY_SIMPLEX, 1, new
					// Scalar(0,0,255),3);
					/*
					 * if(frameCount == 1){ frame1.add(idVal); } if(frameCount
					 * == 2){ frame2.add(idVal); } if(frameCount == 3){
					 * frame3.add(idVal); }
					 * 
					 * if(frameCount == 3){ if(frame1.contains(idVal) &&
					 * frame2.contains(idVal) && frame3.contains(idVal)){
					 */

					// if(idFrequency.get(idVal)>4 && option != 0){
					// idFrequency.put(idVal, 0);

					// >>>>>>>>>>>>>>>>>>
					/*
					 * ArrayList<Integer> fs = new ArrayList<Integer>();
					 * fs.add(0); if(frameCount<3){ frameStore.put(0, fs);
					 * frameStore.put(1, fs); frameStore.put(2, fs); }
					 * switch(frameCount%3){ case 0:
					 * if(!frameStore.get(0).isEmpty() &&
					 * !frameStore.get(0).contains(idVal)){ fs =
					 * frameStore.get(0); fs.add(idVal); frameStore.put(0, fs);
					 * } break;
					 * 
					 * case 1: if(!frameStore.get(1).isEmpty() &&
					 * !frameStore.get(1).contains(idVal)){ fs =
					 * frameStore.get(1); fs.add(idVal); frameStore.put(1, fs);
					 * } break;
					 * 
					 * case 2: if(!frameStore.get(2).isEmpty() &&
					 * !frameStore.get(2).contains(idVal)){ fs =
					 * frameStore.get(2); fs.add(idVal); frameStore.put(2, fs);
					 * } break; }
					 * 
					 * if(frameCount>2 && frameCount%3 == 1 &&
					 * !frameStore.isEmpty()){
					 * if(frameStore.get(0).contains(idVal) &&
					 * frameStore.get(1).contains(idVal) &&
					 * frameStore.get(2).contains(idVal)){ switch(option){ case
					 * 1: if(!tempAns.containsKey(idVal)){ ArrayList<Integer>
					 * ansList = new ArrayList<Integer>(); ansList.add(option);
					 * ansList.add(0); ansList.add(0); ansList.add(0);
					 * tempAns.put(idVal, ansList); }else{ ArrayList<Integer>
					 * listAns = tempAns.get(idVal); listAns.set(0,
					 * listAns.get(0)+1); tempAns.put(idVal, listAns); } break;
					 * case 2: if(!tempAns.containsKey(idVal)){
					 * ArrayList<Integer> ansList = new ArrayList<Integer>();
					 * ansList.add(0); ansList.add(option); ansList.add(0);
					 * ansList.add(0); tempAns.put(idVal, ansList); }else{
					 * ArrayList<Integer> listAns = tempAns.get(idVal);
					 * listAns.set(1, listAns.get(1)+1); tempAns.put(idVal,
					 * listAns); } break; case 3:
					 * if(!tempAns.containsKey(idVal)){ ArrayList<Integer>
					 * ansList = new ArrayList<Integer>(); ansList.add(0);
					 * ansList.add(0); ansList.add(option); ansList.add(0);
					 * tempAns.put(idVal, ansList); }else{ ArrayList<Integer>
					 * listAns = tempAns.get(idVal); listAns.set(2,
					 * listAns.get(2)+1); tempAns.put(idVal, listAns); } break;
					 * case 4: if(!tempAns.containsKey(idVal)){
					 * ArrayList<Integer> ansList = new ArrayList<Integer>();
					 * ansList.add(0); ansList.add(0); ansList.add(0);
					 * ansList.add(option); tempAns.put(idVal, ansList); }else{
					 * ArrayList<Integer> listAns = tempAns.get(idVal);
					 * listAns.set(3, listAns.get(3)+1); tempAns.put(idVal,
					 * listAns); } break; } Core.putText(mat, idVal.toString(),
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 1, new
					 * Scalar(0,0,255),1);
					 * 
					 * }else{ if(frameStore.get(0).contains(idVal)){
					 * frameStore.get
					 * (0).remove(frameStore.get(0).indexOf(idVal)); }
					 * if(frameStore.get(1).contains(idVal)){
					 * frameStore.get(1).remove
					 * (frameStore.get(1).indexOf(idVal)); } } }
					 */

					// />>>>>>>>>>>>>>>>>>

					// holeList.add(hole);
					// idsList.add(idVal);
					// }
					// if(idFrequency.get(idVal)>4 && option != 0){
					// if(goodId(idAvg,pivot,p1)){
					/*
					 * idFrequency.put(idVal, 0); //if(frameCount==10){
					 * if(!answers.containsKey(idVal)){ Core.putText(mat,
					 * idVal.toString(),idAvg,Core.FONT_HERSHEY_SIMPLEX, 0.5,
					 * new Scalar(0,0,255),2); //Core.putText(mat,
					 * idVal.toString(),idAvg,Core.FONT_HERSHEY_SIMPLEX, 0.5,
					 * new Scalar(255,0,0),2); Core.putText(mat,
					 * option.toString(), hole,Core.FONT_HERSHEY_SIMPLEX, 3, new
					 * Scalar(0,0,255),3); answers.put(idVal, option);
					 * if(optionCount[option-1] != null){
					 * optionCount[option-1]++; }else{ optionCount[option-1] =
					 * 1; }
					 * 
					 * }else{ Core.putText(mat, option.toString(),
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 1, new
					 * Scalar(255,0,0),1); // Core.putText(mat,
					 * "o",m1p,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); // Core.putText(mat,
					 * "o",m2p,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); // Core.putText(mat,
					 * "o",id1,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); // Core.putText(mat,
					 * "o",id2,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),1); Core.putText(mat,
					 * idVal.toString(),idAvg,Core.FONT_HERSHEY_SIMPLEX, 0.5,
					 * new Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a0,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a4,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a2,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a6,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a1,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a5,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a7,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); Core.putText(mat,
					 * "o",a3,Core.FONT_HERSHEY_SIMPLEX, 0.1, new
					 * Scalar(255,0,0),2); //Core.putText(mat, "X",
					 * hole,Core.FONT_HERSHEY_SIMPLEX, 2, new
					 * Scalar(0,0,255),3); }
					 */
					// }
					// }
					// }
					/*
					 * } frame1.remove(frame1.indexOf(idVal));
					 * frame2.remove(frame2.indexOf(idVal));
					 * frame3.remove(frame3.indexOf(idVal)); }
					 */

					// Log everything
					if (!tempAns.isEmpty()) {
						if (tempAns.containsKey(idVal)) {
							ArrayList<Integer> ansList = tempAns.get(idVal);
							ansList.set(option - 1, ansList.get(option - 1) + 1);
							tempAns.put(idVal, ansList);
							/*
							 * Core.putText(mat, option.toString(), hole,
							 * Core.FONT_HERSHEY_SIMPLEX, 3, new Scalar(0, 0,
							 * 255), 3);
							 */
							Core.putText(mat, idVal.toString(), hole,
									Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(
											255, 0, 0), 3);
						} else {
							ArrayList<Integer> ansList = new ArrayList<Integer>();
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.set(option - 1, ansList.get(option - 1) + 1);
							tempAns.put(idVal, ansList);
							/*
							 * Core.putText(mat, option.toString(), hole,
							 * Core.FONT_HERSHEY_SIMPLEX, 3, new Scalar(0, 0,
							 * 255), 3);
							 */
							Core.putText(mat, idVal.toString(), hole,
									Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(
											255, 0, 0), 3);
						}
					} else {
						ArrayList<Integer> ansList = new ArrayList<Integer>();
						ansList.add(0);
						ansList.add(0);
						ansList.add(0);
						ansList.add(0);
						ansList.set(option - 1, ansList.get(option - 1) + 1);
						tempAns.put(idVal, ansList);
						/*
						 * Core.putText(mat, option.toString(), hole,
						 * Core.FONT_HERSHEY_SIMPLEX, 3, new Scalar(0, 0, 255),
						 * 3);
						 */
						Core.putText(mat, idVal.toString(), hole,
								Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255,
										0, 0), 3);
					}

					idX.add(idcx);
					idY.add(idcy);
					holes.add(hole);
				}

			}
			// Core.putText(mat, option, new
			// Point(hole.x,hole.y),Core.FONT_HERSHEY_SIMPLEX, 0.7, new
			// Scalar(255,255,255),3);
			// Core.putText(mat, "0", new
			// Point(idcx,idcy),Core.FONT_HERSHEY_SIMPLEX, 0.1, new
			// Scalar(255,0,0),3);
		}
		//
		return mat;
	}

	/*
	 * private void saveResults(){ Set<Integer> ansKeys = tempAns.keySet();
	 * Iterator<Integer> ansIt = ansKeys.iterator(); while(ansIt.hasNext()){ int
	 * eachKey = ansIt.next(); ArrayList<Integer> ansList =
	 * tempAns.get(eachKey); int max = 0; for(int j = 0; j<4;j++){
	 * if(ansList.get(j)>max){ max = ansList.get(j); } } int logOption =
	 * ansList.indexOf(max); int logId = eachKey; }
	 */

	// }
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

	private int bin2dec(ArrayList<Integer> tmpId) {
		int sum = 0;
		for (int i = 0; i < tmpId.size() ; i++) {
			int curBit = tmpId.get(tmpId.size() - i - 1);
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
	
	

	private Mat idPoints(Mat mat, Point idc, Point hole, Point pivot, Point p3,
			Point p1, int option) {

		Point m1p = new Point();
		Point m2p = new Point();
		m1p.x = (pivot.x + p1.x) / 2;
		m1p.y = (pivot.y + p1.y) / 2;
		m2p.x = (pivot.x + p3.x) / 2;
		m2p.y = (pivot.y + p3.y) / 2;
		Point id1 = new Point((2 * hole.x - m1p.x), (2 * hole.y - m1p.y));
		Point id2 = new Point((2 * hole.x - m2p.x), (2 * hole.y - m2p.y));
		Point idc1 = new Point((2 * id2.x - p1.x), (2 * id2.y - p1.y));
		Point idc2 = new Point((2 * id1.x - p3.x), (2 * id1.y - p3.y));
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
		a0.x = (id2.x + idAvg.x) / 2;
		a0.y = (id2.y + idAvg.y) / 2;

		a4.x = (2 * idAvg.x - a0.x);
		a4.y = (2 * idAvg.y - a0.y);

		a2.x = (id1.x + idAvg.x) / 2;
		a2.y = (id1.y + idAvg.y) / 2;

		a6.x = (2 * idAvg.x - a2.x);
		a6.y = (2 * idAvg.y - a2.y);

		a1.x = (hole.x + idAvg.x) / 2;
		a1.y = (hole.y + idAvg.y) / 2;

		a5.x = (2 * idAvg.x - a1.x);
		a5.y = (2 * idAvg.y - a1.y);

		a7.x = (2 * a6.x - a5.x);
		a7.y = (2 * a6.y - a5.y);

		a3.x = (2 * idAvg.x - a7.x);
		a3.y = (2 * idAvg.y - a7.y);

		Core.putText(mat, "1p", m1p, Core.FONT_HERSHEY_SIMPLEX, 0.4,
				new Scalar(255, 0, 0), 1);
		Core.putText(mat, "2p", m2p, Core.FONT_HERSHEY_SIMPLEX, 0.4,
				new Scalar(255, 0, 0), 1);
		Core.putText(mat, "d1", id1, Core.FONT_HERSHEY_SIMPLEX, 0.4,
				new Scalar(255, 0, 0), 1);
		Core.putText(mat, "d2", id2, Core.FONT_HERSHEY_SIMPLEX, 0.4,
				new Scalar(255, 0, 0), 1);
		Core.putText(mat, "o", idAvg, Core.FONT_HERSHEY_SIMPLEX, 0.1,
				new Scalar(255, 0, 0), 1);
		Core.putText(mat, "o", a0, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a4, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a2, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a6, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a1, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a5, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a7, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		Core.putText(mat, "o", a3, Core.FONT_HERSHEY_SIMPLEX, 0.1, new Scalar(
				255, 0, 0), 1);
		return mat;
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
		/*
		 * for(int i=0; i<cardsTable.size(); i++){ int ind1 =
		 * cardsTable.get(i).get(0); int ind2 = cardsTable.get(i).get(1); int
		 * ind3 = cardsTable.get(i).get(2); Point c1 = centres.get(ind1); Point
		 * c2 = centres.get(ind2); Point c3 = centres.get(ind3);
		 * 
		 * }
		 */
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
		if (mRgba != null) {
			mRgba.release();
		}
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub

		Mat rgbaMat = inputFrame.rgba();
		// Mat newMat = new Mat(rgbaMat.cols(),rgbaMat.rows(), CvType.CV_16UC4);
		try{
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
			if (frameCount % 10 == 0) {
				findPresent();
				listAbsent();
				ArrayList<String> tempPre = getPresentIds();
				ArrayList<String> tempAbs = getAbsentIds();
				ArrayList<String> tempExt = getExtraIds();
				listDetails(tempPre, tempAbs, tempExt);
			}
			Mat resultMat = processIt(grayMat, rgbaMat);
			Mat newMat = new Mat(rgbaMat.width(), rgbaMat.height(),
					CvType.CV_8UC4);

			Imgproc.cvtColor(grayMat, newMat, Imgproc.COLOR_GRAY2RGBA);
			return resultMat;

		}
		
		}catch(Exception e){

		}
		return inputFrame.rgba();
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

	private void findPresent() {

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
			finalAns.put(each, maxIndex);
			if (ReadRoster.getIds().contains(each.toString())) {
				if (!pList.contains(each.toString())) {
					pList.add(each.toString());
				}
			} else {
				if (!eList.contains(each.toString())) {
					eList.add(each.toString());
				}
			}
		}

	}

	private void listAbsent() {
		ArrayList<String> tempList = ReadRoster.getIds();
		if (tempList != null) {
			Iterator<String> it = tempList.iterator();
			while (it.hasNext()) {
				String each = it.next();
				if (!pList.contains(each)) {
					if (!aList.contains(each)) {
						aList.add(each);
					}
				}
			}
		}
	}

	public static ArrayList<String> getPresentIds() {
		return pList;
	}

	public static ArrayList<String> getAbsentIds() {
		return aList;
	}

	public static ArrayList<String> getExtraIds() {
		ArrayList<String> tempList = aList;
		if (tempList != null) {
			Iterator<String> it = tempList.iterator();
			while (it.hasNext()) {
				String each = it.next();
				if (eList.contains(each)) {
					eList.remove(each);
				}
			}
		}
		return eList;
	}

	public void showResults(View view) {
		Intent i = new Intent(this, AttendanceResults.class);
		startActivity(i);
	}

}
