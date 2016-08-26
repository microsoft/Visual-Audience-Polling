/*
 * 
 */
package com.abhinav.qcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class QCardsLibrary.
 */
public class QCardsLibrary {

	/** The input mat. */
	Mat inputMat;
	private boolean processed = false;

	private int countA;
	private int countB;
	private int countC;
	private int countD;
	private int countX;

	private ArrayList<Integer> optionCountList;

	/** The color return mat. */
	private Mat colorReturnMat;

	/** The bnw return mat. */
	private Mat bnwReturnMat;

	/** The filtered return mat. */
	private Mat filteredReturnMat;

	/** The thresh_otsu. */
	private int thresh_otsu = 120;

	/** The thresh_inv. */
	private int thresh_inv = 150;

	/** The blur. */
	private boolean blur = true;

	/** The highlight card bool. */
	private boolean highlightCardBool = true;

	/** The id x. */
	private List<Double> idX = new ArrayList<Double>();

	/** The id y. */
	private List<Double> idY = new ArrayList<Double>();

	/** The holes. */
	private List<Point> holes = new ArrayList<Point>();
	
	/** The q card list. */
	private ArrayList<QCard> qCardList = new ArrayList<QCard>();

	/** The contours. */
	private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

	/** The x list. */
	private List<Double> xList = new ArrayList<Double>();

	/** The y list. */
	private List<Double> yList = new ArrayList<Double>();

	/** The perimeter list. */
	private List<Double> perimeterList = new ArrayList<Double>();

	/** The area list. */
	private List<Double> areaList = new ArrayList<Double>();

	/** The centres. */
	private List<Point> centres = new ArrayList<Point>();

	/** The cards table. */
	private Hashtable<Integer, ArrayList<Integer>> cardsTable = new Hashtable<Integer, ArrayList<Integer>>();

	/** The temp ans. */
	private Hashtable<String, ArrayList<Integer>> tempAns = new Hashtable<String, ArrayList<Integer>>();
	
	private Hashtable<String,ArrayList<Point>> groupTable = new Hashtable<String,ArrayList<Point>>();
	
	private Hashtable<String, ArrayList<MatOfPoint>> groupContourTable = new Hashtable<String, ArrayList<MatOfPoint>>();

	private Hashtable<String, ArrayList<String>> namedRecords = new Hashtable<String, ArrayList<String>>();
	/**
	 * Instantiates a new q cards library.
	 * 
	 * @param rgbaMat
	 *            the rgba matrix
	 * @param tempAns
	 *            the temporary Answers HashTable
	 */
	public QCardsLibrary(Mat rgbaMat,
			Hashtable<String, ArrayList<Integer>> tempAns) {
		inputMat = rgbaMat;
		this.tempAns = tempAns;
	}

	public QCardsLibrary(Hashtable<String, ArrayList<Integer>> tempAns) {
		this.tempAns = tempAns;
	}
	
	public QCardsLibrary(){
		
	}

	/**
	 * Process it.
	 * 
	 * @param grayMat
	 *            the gray mat
	 * @param rgbaMat
	 *            the rgba mat
	 * @return the mat
	 */
	public Mat processIt(Mat grayMat, Mat rgbaMat) {

		Mat grayM = grayMat;
		Mat colorM = rgbaMat;

		// Get a Filtered(Otsu_Threshhold followed by Binary Inversion) image
		Mat filtered = filterIt(grayM);

		// detectBlack method detects the contours and calculates the parameters
		Mat detectResults = detectBlack(filtered, colorM);

		if (detectResults != null) {
			processed = true;
			return detectResults;
		} else {
			return colorM;
		}

	}

	// setters
	/**
	 * Sets the highlight cards.
	 * 
	 * @param bool
	 *            the new highlight cards
	 */
	public void setHighlightCards(Boolean bool) {
		highlightCardBool = bool;
	}

	/**
	 * Modify cam settings.
	 * 
	 * @param thresh_otsu
	 *            the thresh_otsu
	 * @param thresh_inv
	 *            the thresh_inv
	 * @param blur
	 *            the blur
	 */
	public void modifyCamSettings(int thresh_otsu, int thresh_inv, boolean blur) {
		this.thresh_otsu = thresh_otsu;
		this.thresh_inv = thresh_inv;
		this.blur = blur;
	}

	
	/**
	 * Gets the contours list. Each entry in the list is a MatOfPoint corresponding
	 * to the contours of our interest. These contours are obtained after processing
	 * all the contours in the frame and eliminating those which fail our criteria.
	 * 
	 * @return the contours list
	 */
	public List<MatOfPoint> getContoursList() {
		return contours;
	}

	/**
	 * Gets the contours center x.
	 * 
	 * @return the contours center x
	 */
	public List<Double> getContoursCenterX() {
		return xList;
	}

	/**
	 * Gets the contours center y.
	 * 
	 * @return the contours center y
	 */
	public List<Double> getContoursCenterY() {
		return yList;
	}

	
	/**
	 * Gets the temp answers hashtable. Each key in the table is a studentID.
	 * Corresponding to each key, there is an ArrayList with 5 entries.
	 * Each index in the ArrayList is the count of A, B, C, D and None option recorded
	 * for that ID throughout that polling session.
	 * We later refine these answers and treat the max of these values as the final answer.
	 * 
	 * @return the tempAns Hashtable.
	 */
	public Hashtable<String, ArrayList<Integer>> getTempAnswers() {
		return this.tempAns;
	}

	// processing methods
	/**
	 * Filter it.
	 * 
	 * @param grayMat
	 *            the gray mat
	 * @return the mat
	 */
	public Mat filterIt(Mat grayMat) {
		Mat grey = grayMat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		Mat dilated = new Mat();
		if (blur == true) {
			Imgproc.GaussianBlur(grey, tmp2, new Size(5, 5), 0);
		}

		Imgproc.threshold(tmp2, tmp1, thresh_otsu, 255, Imgproc.THRESH_OTSU);
		Imgproc.threshold(tmp1, tmp1, thresh_inv, 255,
				Imgproc.THRESH_BINARY_INV);

		if (tmp1 != null) {
			return tmp1;
		} else {
			return grey;
		}

	}

	/**
	 * Filter it.
	 *
	 * @param grayMat the gray mat
	 * @param otsu_bool set otsu_bool true or false depending upon whether you want otsu filter or not
	 * @param blur set gaussian blur true or false depending upon whether you want otsu filter or not
	 * @return the mat
	 */
	public Mat filterIt(Mat grayMat, boolean otsu_bool, boolean blur) {
		Mat grey = grayMat;
		Mat tmp1 = grey;
		Mat tmp2 = grey;
		if (blur == true) {
			Imgproc.GaussianBlur(grey, tmp2, new Size(5, 5), 0);
		}
		if (otsu_bool) {
			Imgproc.threshold(tmp2, tmp1, thresh_otsu, 255, Imgproc.THRESH_OTSU);
		}
		Imgproc.threshold(tmp1, tmp1, thresh_inv, 255,
				Imgproc.THRESH_BINARY_INV);

		if (tmp1 != null) {
			return tmp1;
		} else {
			return grey;
		}
	}
		
		/**
		 * Filter it.
		 *
		 * @param grayMat the gray mat
		 * @param otsu_bool the otsu_bool
		 * @param blur the blur
		 * @param thresh_otsu the thresh_otsu
		 * @param thresh_inv the thresh_inv
		 * @return the mat
		 */
		public Mat filterIt(Mat grayMat, boolean otsu_bool, boolean blur, int thresh_otsu, int thresh_inv) {
			Mat grey = grayMat;
			Mat tmp1 = grey;
			Mat tmp2 = grey;
			if (blur == true) {
				Imgproc.GaussianBlur(grey, tmp2, new Size(5, 5), 0);
			}
			if (otsu_bool) {
				Imgproc.threshold(tmp2, tmp1, thresh_otsu, 255, Imgproc.THRESH_OTSU);
			}
			Imgproc.threshold(tmp1, tmp1, thresh_inv, 255,
					Imgproc.THRESH_BINARY_INV);

			if (tmp1 != null) {
				return tmp1;
			} else {
				return grey;
			}

	}

	/**
	 * Detect black.
	 * 
	 * @param filtered
	 *            the binarized and inverted matrix
	 * @param colorMat
	 *            the rgba matrix from camera frame
	 * @return the mat(coloured matrix to be drawn on screen)
	 */
	private Mat detectBlack(Mat filtered, Mat colorMat) {

		Integer totContours = 0;
		Integer finContours = 0;
		Mat hierarchy = new Mat();

		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> tempContours = new ArrayList<MatOfPoint>();

		// finding contours
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);
		totContours = mContours.size();

		double[] data = new double[hierarchy.rows() * hierarchy.cols()
				* hierarchy.channels()];

		for (int i = 0; i < mContours.size(); i++) {
			double[] hVal = hierarchy.get(0, i);
			// 3rd entry in hierarchy is child contour -
			// http://docs.opencv.org/master/d9/d8b/tutorial_py_contours_hierarchy.html
			int childId = properCastX(hVal[2]);
			if (childId > -1) {
				/*
				 * double pArea = Imgproc.contourArea(mContours.get(i)); double
				 * cArea = Imgproc .contourArea(mContours.get(childId));
				 */

				/*
				 * Comparing child and parent areas. Ideal ratio is 1:9 if
				 * (pArea > 5 * cArea && pArea < 13 * cArea) { Add parent to
				 * tempContours list
				 */
				if (!tempContours.contains(mContours.get(i))) {
					tempContours.add(mContours.get(i));
				}
				// Add child to tempContours list
				if (!tempContours.contains(mContours.get(childId))) {
					tempContours.add(mContours.get(childId));
				}
				// }
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

					/*
					 * Calculating image moments and center coordinates for each
					 * contour
					 */
					double moment00 = Imgproc.moments(wrapper).get_m00();
					double moment01 = Imgproc.moments(wrapper).get_m01();
					double moment10 = Imgproc.moments(wrapper).get_m10();
					double centerX = moment10 / moment00;
					double centerY = moment01 / moment00;

					/*
					 * Ensure that the centers of the contours are white
					 */
					if (filtered
							.get(properCastY(centerY), properCastX(centerX))[0] == 0) {
						contours.add(wrapper);
						double peri = Imgproc.arcLength(forrotatedrect, true);
						perimeterList.add(peri);
						double area = Imgproc.contourArea(wrapper);
						areaList.add(area);
						xList.add(centerX);
						yList.add(centerY);
						centres.add(new Point(centerX, centerY));
					}
				}
			}

		}

		Mat mat = colorMat;

		// finding the potential neighbours for each contour
		findNeighbors(mat);

		// Group the neighbours, identify options and draw contours
		mat = drawIdOptions(mat, colorMat, filtered);

		if (mat != null) {
			return mat;
		} else {
			return colorMat;
		}
	}
	
	
	
	private Mat detectBlackRelaxed(Mat filtered, Mat colorMat) {

		Integer totContours = 0;
		Integer finContours = 0;
		Mat hierarchy = new Mat();

		List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> tempContours = new ArrayList<MatOfPoint>();

		// finding contours
		Imgproc.findContours(filtered, mContours, hierarchy, Imgproc.RETR_TREE,
				Imgproc.CHAIN_APPROX_SIMPLE);
		totContours = mContours.size();

		double[] data = new double[hierarchy.rows() * hierarchy.cols()
				* hierarchy.channels()];

		for (int i = 0; i < mContours.size(); i++) {
			double[] hVal = hierarchy.get(0, i);
			// 3rd entry in hierarchy is child contour -0
			// http://docs.opencv.org/master/d9/d8b/tutorial_py_contours_hierarchy.html
			int childId = properCastX(hVal[2]);
			if (childId > -1) {
				
				if (!tempContours.contains(mContours.get(i))) {
					tempContours.add(mContours.get(i));
				}
				// Add child to tempContours list
				if (!tempContours.contains(mContours.get(childId))) {
					tempContours.add(mContours.get(childId));
				}
				// }
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

					/*
					 * Calculating image moments and center coordinates for each
					 * contour
					 */
					double moment00 = Imgproc.moments(wrapper).get_m00();
					double moment01 = Imgproc.moments(wrapper).get_m01();
					double moment10 = Imgproc.moments(wrapper).get_m10();
					double centerX = moment10 / moment00;
					double centerY = moment01 / moment00;

					/*
					 * Ensure that the centers of the contours are white
					 */
					if (filtered
							.get(properCastY(centerY), properCastX(centerX))[0] == 0) {
						contours.add(wrapper);
						double peri = Imgproc.arcLength(forrotatedrect, true);
						perimeterList.add(peri);
						double area = Imgproc.contourArea(wrapper);
						areaList.add(area);
						xList.add(centerX);
						yList.add(centerY);
						centres.add(new Point(centerX, centerY));
					}
				}
			}

		}

		Mat mat = colorMat;

		// finding the potential neighbours for each contour
		findNeighbors(mat);

		// Group the neighbours, identify options and draw contours
		mat = drawIdOptions(mat, colorMat, filtered);

		if (mat != null) {
			return mat;
		} else {
			return colorMat;
		}
	}

	/**
	 * Proper cast x.
	 * 
	 * @param p
	 *            the point whose x value is to be casted to int
	 * @return the int
	 */
	private int properCastX(Point p) {
		double check = Math.ceil(p.x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	/**
	 * Proper cast y.
	 * 
	 * @param p
	 *            the p
	 * @return the int
	 */
	private int properCastY(Point p) {
		double check2 = Math.ceil(p.y);
		int checkY = (int) (check2 * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

	/**
	 * Proper cast x.
	 * 
	 * @param x
	 *            the x
	 * @return the int
	 */
	private int properCastX(double x) {
		double check = Math.ceil(x);
		int checkX = (int) (check * 10);
		int checkedX = checkX / 10;

		return checkedX;
	}

	/**
	 * Proper cast y.
	 * 
	 * @param y
	 *            the y
	 * @return the int
	 */
	private int properCastY(double y) {
		double check = Math.ceil(y);
		int checkY = (int) (check * 10);
		int checkedY = checkY / 10;

		return checkedY;
	}

	/**
	 * Proper cast.
	 * 
	 * @param p
	 *            the p
	 * @return the point
	 */
	private Point properCast(Point p) {
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
	 * @param pivot
	 *            the pivot
	 * @param p1
	 *            the p1
	 * @param p2
	 *            the p2
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
	 * Center fix.
	 * 
	 * @param p1
	 *            the p1
	 * @param tmp
	 *            the tmp
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

	/**
	 * Bin2dec.
	 * 
	 * @param tmpId
	 *            the tmp id
	 * @return the int
	 */
	private int bin2dec(ArrayList<Integer> tmpId) {

		int sum = 0;
		for (int i = 0; i < tmpId.size() - 1; i++) {
			int curBit = tmpId.get(tmpId.size() - i - 1);
			if (curBit < 100) {
				sum += Math.pow(2, i);
			}

		}
		return sum;
	}

	/**
	 * Find neighbors.
	 * 
	 * @param mat
	 *            the mat
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
			long ps = System.nanoTime();
			ArrayList<Integer> cardList = new ArrayList<Integer>();
			// cardList.add(0);
			ArrayList<Point> tempList = new ArrayList<Point>();
			ArrayList<Integer> tempId = new ArrayList<Integer>();

			// scale = length of a side
			double scale = perimeterList.get(ids[i]) / 4;
			if (!rem.contains(ids[i])) {

				// Finding potential neighbour centers in x+ direction
				for (int j = i; j < centres.size()
						&& Math.abs(arr[i].x - arr[j].x) < 2 * scale; j++) {
					double iArea = areaList.get(ids[i]);
					double jArea = areaList.get(ids[j]);

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
					double iArea = areaList.get(i);
					double jArea = areaList.get(j);
					if (iArea < 1.2 * jArea && iArea > 0.8 * jArea) {
						tempId.add(ids[j]);
						tempList.add(arr[j]);
					}

				}

				/*
				 * After the above two loops, we get a set of all potential
				 * neighbouring contours for contour with id 'i' based on the
				 * value of their x-coordinate. It can be imagined as a column in
				 * the image
				 */

				/*
				 * The 'for' loop below searches for the potential neighbours in
				 * the column obtained in the last two 'for' loops.
				 */

				for (Integer j = 0; j < tempList.size(); j++) {

					/*
					 * Checking in the column, if the y-coordinates are in
					 * permissible range then add the index of the contour to
					 * the cardList and also add it to the rem list so that we
					 * do not process it again
					 */
					if (Math.abs(arr[i].y - tempList.get(j).y) < 2 * scale) {

						cardList.add(centres.indexOf(tempList.get(j)));
						rem.add(centres.indexOf(tempList.get(j)));

					}
				}

				/*
				 * Maintain a cardsTable to hold the cardLists cardsTable holds
				 * only those cardLists which have more than 2 elements as we
				 * require at least three squares for a valid pattern
				 */
				if (cardList.size() > 2) {
					cardsTable.put(counter, cardList);
					counter++;
				}
				// cardList.clear();
			}

		}

	}

	/**
	 * Draw id options.
	 * 
	 * @param drawingMat
	 *            the drawing mat
	 * @param colorMat
	 *            the color mat
	 * @param bnw
	 *            the bnw
	 * @return the mat
	 */
	private Mat drawIdOptions(Mat drawingMat, Mat colorMat, Mat bnw) {
		Integer option = 0;
		double idcx = 0;
		double idcy = 0;
		float[] radius = new float[1];

		for (Integer i = 0; i < cardsTable.size(); i++) {
			// ArrayList<Integer> tmpId = new ArrayList<Integer>();
			

			Point[] arr = new Point[3];

			Point p1 = centres.get(cardsTable.get(i).get(0));
			p1 = centerFix(p1, colorMat);
			arr[0] = p1;

			Point p2 = centres.get(cardsTable.get(i).get(1));
			p2 = centerFix(p2, colorMat);
			arr[1] = p2;

			Point p3 = centres.get(cardsTable.get(i).get(2));
			p3 = centerFix(p3, colorMat);
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
						option = 4;

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
					// double[] tempCol1 = colorMat.get(945, 1313);
					a0.x = (id2.x + idAvg.x) / 2;
					a0.y = (id2.y + idAvg.y) / 2;
					// tempCol = (int) bnw.get((int)a0.y, (int)a0.x)[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) colorMat.get(properCastY(a0),
							properCastX(a0))[0];
					tempId.add(tempCol);

					a1.x = (hole.x + idAvg.x) / 2;
					a1.y = (hole.y + idAvg.y) / 2;
					tempCol = (int) colorMat.get(properCastY(a1),
							properCastX(a1))[0];
					tempId.add(tempCol);

					a2.x = (id1.x + idAvg.x) / 2;
					a2.y = (id1.y + idAvg.y) / 2;
					tempCol = (int) colorMat.get(properCastY(a2),
							properCastX(a2))[0];
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

					tempCol = (int) colorMat.get(properCastY(a3),
							properCastX(a3))[0];
					tempId.add(tempCol);

					tempCol = (int) colorMat.get(properCastY(a4),
							properCastX(a4))[0];
					tempId.add(tempCol);

					tempCol = (int) colorMat.get(properCastY(a5),
							properCastX(a5))[0];
					tempId.add(tempCol);

					tempCol = (int) colorMat.get(properCastY(a6),
							properCastX(a6))[0];
					tempId.add(tempCol);

					tempCol = (int) colorMat.get(properCastY(a7),
							properCastX(a7))[0];
					tempId.add(tempCol);

					tempCol = (int) colorMat.get(properCastY(idAvg),
							properCastX(idAvg))[0];
					tempId.add(tempCol);
					Integer idVal = bin2dec(tempId);
					
					
					ArrayList<Point> groupPoints = new ArrayList<Point>();
					groupPoints.add(p1);
					groupPoints.add(pivot);
					groupPoints.add(p3);
					groupTable.put(idVal.toString(), groupPoints);
					
					tempId.clear();

					// Log everything
					if (option > 0) {
						String ansDisplay = null;
						switch (option) {
						case 1:
							ansDisplay = "A";
							break;
						case 2:
							ansDisplay = "B";
							break;
						case 3:
							ansDisplay = "C";
							break;
						case 4:
							ansDisplay = "D";
							break;
						}
						int rad = (int) radius[0];

						/*
						 * logging the option detected for a particular id in
						 * each frame tempAns takes ID as key and an arraylist
						 * containing the count of options corresponding to that
						 * ID
						 */
						if (!tempAns.isEmpty()) {
							if (tempAns.containsKey(idVal)) {
								ArrayList<Integer> ansList = tempAns.get(idVal);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal.toString(), ansList);

								if (highlightCardBool) {
									Core.circle(drawingMat, hole,
											(int) radius[0], new Scalar(50,
													158, 33), -1);

									Core.putText(drawingMat, ansDisplay, hole,
											Core.FONT_HERSHEY_SIMPLEX, 2,
											new Scalar(255, 255, 255), 3);

									Core.putText(drawingMat, idVal.toString(),
											idAvg, Core.FONT_HERSHEY_SIMPLEX,
											1, new Scalar(255, 0, 0), 2);
								}
							} else {
								/*
								 * If HashTable doesn't contain an entry for ID,
								 * then create an entry
								 */
								ArrayList<Integer> ansList = new ArrayList<Integer>();
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.add(0);
								ansList.set(option - 1,
										ansList.get(option - 1) + 1);
								tempAns.put(idVal.toString(), ansList);

								if (highlightCardBool) {
									Core.circle(drawingMat, hole,
											(int) radius[0], new Scalar(50,
													158, 33), -1);

									Core.putText(drawingMat, ansDisplay, hole,
											Core.FONT_HERSHEY_SIMPLEX, 1.5,
											new Scalar(255, 255, 255), 2);

									Core.putText(drawingMat, idVal.toString(),
											idAvg, Core.FONT_HERSHEY_SIMPLEX,
											1, new Scalar(255, 0, 0), 2);
								}
							}
						} else {
							ArrayList<Integer> ansList = new ArrayList<Integer>();
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.add(0);
							ansList.set(option - 1, ansList.get(option - 1) + 1);
							tempAns.put(idVal.toString(), ansList);

							if (highlightCardBool) {

								Core.circle(drawingMat, hole, (int) radius[0],
										new Scalar(50, 158, 33), -1);
								Core.putText(drawingMat, ansDisplay, hole,
										Core.FONT_HERSHEY_SIMPLEX, 2,
										new Scalar(255, 255, 255), 3);

								Core.putText(drawingMat, idVal.toString(),
										idAvg, Core.FONT_HERSHEY_SIMPLEX, 1,
										new Scalar(255, 0, 0), 2);
							}
						}
					}

					idX.add(idcx);
					idY.add(idcy);
					holes.add(hole);
				}

			}

		}

		return drawingMat;
	}

	/**
	 * Gets the refined answers.
	 * 
	 * @param tempAns
	 *            the temp ans
	 * @return the refined answers
	 */
	public Hashtable<String, Integer> getRefinedAnswers(
			Hashtable<String, ArrayList<Integer>> tempAns) {
		Hashtable<String, Integer> finalAns = new Hashtable<String, Integer>();
		Set<String> keyAns = tempAns.keySet();
		Iterator<String> itAns = keyAns.iterator();
		if (keyAns.size() == 0) {
			finalAns.clear();
		}

		while (itAns.hasNext()) {
			String each = itAns.next();
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
		}
		return finalAns;
	}

	

	/**
	 * Gets the missing list.
	 * 
	 * @param presentList
	 *            the present list
	 * @return the missing list
	 */
	public ArrayList<String> getMissingList(ArrayList<String> presentList) {
		ArrayList<String> missingList = new ArrayList<String>();
		Iterator<String> it = presentList.iterator();
		while (it.hasNext()) {
			if (!tempAns.containsKey(Integer.parseInt(it.next()))) {
				missingList.add(it.next());
			}
		}
		return missingList;
	}

	/**
	 * Gets the final answers.
	 * 
	 * @param refinedAnswers
	 *            the refined answers
	 * @param finalAnswers
	 *            the final answers
	 * @return the final answers
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Hashtable<String, ArrayList<String>> getQuizFinalAnswers(
			Hashtable<String, Integer> refinedAnswers)
			throws IOException {
		Hashtable<String, ArrayList<String>> finalAnswers = new Hashtable<String, ArrayList<String>>();
		ArrayList<String> presentList = new ArrayList<String>();
		optionCountList = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			optionCountList.add(i, 5);
		}
		Integer option = 5;
		presentList.add("17");
		presentList.add("237");
		presentList.add("297");
		presentList.add("485");
		// presentList = getPresentList();
		Iterator<String> it = presentList.iterator();
		while (it.hasNext()) {
			String studentId = it.next();
			ArrayList<String> studentAnswers = new ArrayList<String>();
			if (finalAnswers.containsKey(studentId)) {
				studentAnswers = finalAnswers.get(studentId);
			}
			if (refinedAnswers.containsKey(studentId)) {
				option = refinedAnswers.get(studentId);
				studentAnswers.add(option.toString());
			} else {
				option = 5;
				studentAnswers.add(option.toString());
			}
			
			finalAnswers.put(studentId, studentAnswers);

			switch (option) {
			case 0:
				countA++;
				break;
			case 1:
				countB++;
				break;
			case 2:
				countC++;
				break;
			case 3:
				countD++;
				break;
			default:
				countX++;
			}

			optionCountList.remove(0);
			optionCountList.add(0, countA);
			optionCountList.remove(1);
			optionCountList.add(1, countB);
			optionCountList.remove(2);
			optionCountList.add(2, countC);
			optionCountList.remove(3);
			optionCountList.add(3, countD);
			optionCountList.remove(4);
			optionCountList.add(4, countX);
		}
		
		generateNamedRecords(finalAnswers);
		return finalAnswers;
	}
	
	
	private void generateNamedRecords(Hashtable<String, ArrayList<String>> finalAnswers){
		Hashtable<String, String> pNameId = new Hashtable<String, String>();
		try {
			pNameId = FileOperations.getPresentNameId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<String> keySet = finalAnswers.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String id = it.next();
			if(pNameId.containsKey(id)){
				ArrayList<String> studentAnsList = finalAnswers.get(id);
				String studentName = pNameId.get(id);
				namedRecords.put(studentName, studentAnsList);
			}
		}
	}
	
	public Hashtable<String, ArrayList<String>> getNamedRecords(){
		return namedRecords;
	}
	
	public Hashtable<String, ArrayList<Integer>> getQuickFinalAnswers(
			Hashtable<String, Integer> refinedAnswers,
			Hashtable<String, ArrayList<Integer>> finalAnswers)
			throws IOException {
	
		optionCountList = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			optionCountList.add(i, 5);
		}
		Integer option = 5;
		
		Set<String> keySet = refinedAnswers.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String id = it.next();
		option = refinedAnswers.get(id);

			switch (option) {
			case 0:
				countA++;
				break;
			case 1:
				countB++;
				break;
			case 2:
				countC++;
				break;
			case 3:
				countD++;
				break;
			default:
				countX++;
			}

			optionCountList.remove(0);
			optionCountList.add(0, countA);
			optionCountList.remove(1);
			optionCountList.add(1, countB);
			optionCountList.remove(2);
			optionCountList.add(2, countC);
			optionCountList.remove(3);
			optionCountList.add(3, countD);
			optionCountList.remove(4);
			optionCountList.add(4, countX);
		}

		return finalAnswers;
	}

	private void lastQuestionStats(int option) {
		switch (option) {
		case 0:
			countA++;
			break;
		case 1:
			countB++;
			break;
		case 2:
			countC++;
			break;
		case 3:
			countD++;
			break;
		default:
			countX++;
		}

		optionCountList.add(0, countA);
		optionCountList.add(1, countB);
		optionCountList.add(2, countC);
		optionCountList.add(3, countD);
		optionCountList.add(4, countX);
	}
	
	

	public ArrayList<Integer> getLastQuestionStats() {
		return optionCountList;
	}

	public void callWithRosterAttendanceResults(
			Hashtable<String, ArrayList<Integer>> tempAnswers,
			Context applicationContext, String sharedPreferenceName) {
		StringBuilder sb = new StringBuilder();
		KeyValues keyVal = new KeyValues(applicationContext,
				sharedPreferenceName);
		List<MyCards> rosterCards = getRosterContent(keyVal);
		Set<String> tempAnswersKeySet = tempAnswers.keySet();
		Iterator<String> it = tempAnswersKeySet.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			sb.append(",");
		}
		String presentString = sb.toString();
	}

	private List<MyCards> getRosterContent(KeyValues keyVal) {

		String fileName = keyVal.getAttendanceRoster();
		BufferedReader br = null;
		// AssetManager assetManager = getResources().getAssets();
		// InputStream inputStream = null;
		List cardList = new ArrayList<MyCards>();
		// performFileSearch();
		ArrayList<String> testIds = new ArrayList<String>();
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
				testIds = loadRoster.getIds();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return cardList;
	}

	private BufferedReader rosterReader(String file_name)
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
	
	public List<MyCards> getRosterCards(KeyValues keyVal) {
		BufferedReader br = null;

		ArrayList<MyCards> cardList = new ArrayList<MyCards>();
		try {

			br = rosterReader(keyVal.getAttendanceRoster());
			ReadRoster loadRoster = new ReadRoster(br, 2, ",");
			try {
				cardList = loadRoster.getCards();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return cardList;
	}
	
	/**
	 * Gets the centers list.
	 * Do not call this method before processIt()
	 *
	 * @param applicationContext the application context
	 * @return the centers list containing the centers(Point) of detected squares
	 */
	public List<Point> getCentersList(Context applicationContext){
		if(processed){
			return centres;
		}else{
			List<Point> centerList = new ArrayList<Point>();
			Toast.makeText(applicationContext, "DO NOT Call getCentersList method before processIt method", Toast.LENGTH_LONG).show();
			return centerList;
		}		
	}
	
	/**
	 * Gets the cards table.
	 * Do not call this method before processIt()
	 * 
	 * @param applicationContext the application context
	 * @return the cards table containing an ArrayList with the ids of the centers belonging to the same card
	 */
	public Hashtable<Integer, ArrayList<Integer>> getCardsTable(Context applicationContext){
		if(processed){
			return cardsTable;
		}else{
			Hashtable<Integer, ArrayList<Integer>> nullCardsTable = new Hashtable<Integer, ArrayList<Integer>>();
			Toast.makeText(applicationContext, "DO NOT Call getCentersList method before processIt method", Toast.LENGTH_LONG).show();
			return nullCardsTable;
		}	
	}
	
	/**
	 * Generate QCard objects containing each of the detected card's ID, Option and the centers of the 
	 * squares that were grouped together.
	 *
	 * @param refinedAns the refined ans
	 * @return the array list containing QCard objects
	 */
	private ArrayList<QCard> generateQCards(Hashtable<String, Integer> refinedAns){
		Set<String> keySet = refinedAns.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String id = it.next();
			QCard card = new QCard();
			card.setId(id);
			card.setAnswer(refinedAns.get(id));
			card.setOptionCenters(groupTable.get(id));
			qCardList.add(card);			
		}
		return qCardList;
	}
	
	/**
	 * Gets the qCards list containing QCards objects containing each of the detected card's ID, Option and the centers of the 
	 * squares that were grouped together.
	 *
	 * @param tempAns the temp ans
	 * @return the q cards list containing QCard objects.
	 */
	public ArrayList<QCard> getQCardsList(Hashtable<String, ArrayList<Integer>> tempAns){
		return generateQCards(getRefinedAnswers(tempAns));
	}

}
