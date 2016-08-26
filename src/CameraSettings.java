package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CameraSettings extends Activity implements CvCameraViewListener2, OnSeekBarChangeListener {
	private static final String TAG = null;
	private CameraBridgeViewBase mOpenCvCameraView;
	Mat mRgba;
	long cdst = 0;
	long cdet = 0;
	int mHeight;
	int mWidth;
	private static Integer thresh_inv = 120;
	private static Integer thresh_otsu = 150;
	ImageView pic;
	private SeekBar sb1;
	private SeekBar sb2;
	private TextView tv1;
	private TextView tv2;
	private static Boolean blur = false;

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
		saveToFile();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onDestroy() {
		super.onDestroy();
		saveToFile();
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
		try {
			readFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv1.setText(thresh_otsu.toString());
		tv2.setText(thresh_inv.toString());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// >>>>>>>>>
		// setContentView(R.layout.activity_main);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.camera_settings);
		//setContentView(R.layout.cam_attendance);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		sb1 = (SeekBar) findViewById(R.id.seekBar1);
		sb2 = (SeekBar) findViewById(R.id.seekBar2);
		sb1.setOnSeekBarChangeListener(this);
		sb2.setOnSeekBarChangeListener(this);		
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		//saveToFile();

	}

	

	// This is where all those methods are called that are required for
	// processing the image
	// First the image is fetched from the SD Card, Converted to a Matrix and
	// then processed

	protected Mat processIt(Mat tmp) {
		Mat grey = new Mat();

		Imgproc.cvtColor(tmp, grey, Imgproc.COLOR_RGB2GRAY);
		Mat filtered = filterIt(grey, thresh_otsu, thresh_inv);

		return filtered;
	}

	// Blur and dilation
	public Mat filterIt(Mat mat, int thresh_otsu, int thresh_inv) {
		Mat grey = mat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		Mat dilated = new Mat();
		// Imgproc.erode(grey, dilated, new Mat());
		// Imgproc.erode(dilated, grey, new Mat());
		// Imgproc.erode(grey, grey, new Mat());
		// Imgproc.dilate(dilated, tmp2, new Mat());
		if(blur == true){
		Imgproc.GaussianBlur(grey, tmp2, new Size(3, 3), 0);
		}
		// Imgproc.dilate(tmp2,tmp2,new Mat());
		// Imgproc.dilate(tmp2,tmp2,new Mat());
		Imgproc.threshold(tmp2, tmp1, thresh_otsu, 255, Imgproc.THRESH_OTSU);
		Imgproc.threshold(tmp1, tmp1, thresh_inv, 255,Imgproc.THRESH_BINARY_INV);
		

		return tmp1;
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

	@Override
	public void onCameraViewStopped() {
		mRgba.release();
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		Mat grey = inputFrame.gray();
		Mat filter = filterIt(grey, thresh_otsu, thresh_inv);
		//mRgba = inputFrame.rgba();
		//return mRgba;
	//	saveToFile();
		try {
			readFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filter;
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser, TextView text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		TextView tv = (TextView)findViewById(R.id.textView2);;
		if(seekBar.getId() == R.id.seekBar1){
			tv = (TextView)findViewById(R.id.textView1);
			thresh_otsu = progress;
			
		}else{
			thresh_inv = progress;
		}
		tv.setTextColor(Color.BLUE);
		Integer prog = progress;
		prog = progress;
		tv.setText(prog.toString());
	}

	
	private void saveToFile(){
		String filename = "thresh.csv";
		String el1 = thresh_otsu.toString();
		String el2 = thresh_inv.toString();
		String el3 = blur.toString();
		String string = el1+","+el2+","+el3;
		FileOutputStream outputStream;
		
		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(string.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	private void readFromFile() throws IOException{
		try {
			FileInputStream fis = openFileInput("thresh.csv");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			ReadCSV cr = new ReadCSV(br,3,",");
			cr.getSettings();
			ArrayList<String> cont = cr.settingsList;
			thresh_otsu = Integer.parseInt(cont.get(0));
			thresh_inv = Integer.parseInt(cont.get(1));
			blur = Boolean.parseBoolean(cont.get(2));
			/*StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine())!=null){
				sb.append(line);
			}*/
			Log.e(TAG, "Just Checking");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static int get_otsu(){
		return thresh_otsu;
	}

	public static int get_inv(){
		return thresh_inv;
	}

	public static boolean get_blur(){
		return blur;
	}
}
