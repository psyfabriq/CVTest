package pfq.ocrserver.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utill {
	
	public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, "jpg", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMWRITE_JPEG_QUALITY);
	}
	public static BufferedImage Mat2BufferedImage(Mat matrix)throws IOException {
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    return ImageIO.read(new ByteArrayInputStream(mob.toArray())); 
	}
	    
	public static Image getImage(Mat matrix) {
		 BufferedImage resultBufferedImage = null;
			try {
				resultBufferedImage = Mat2BufferedImage(matrix);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Image imagew = SwingFXUtils.toFXImage(resultBufferedImage, null);
	        return  imagew;
	}
	
	public static List<Rect> detectLetters(Mat img){    
	    List<Rect> boundRect=new ArrayList<>();

	    Mat img_gray =new Mat(img.size(), CvType.CV_8UC4), img_sobel=new Mat(), img_threshold=new Mat(img.size(), CvType.CV_32F), element=new Mat();
	    Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_RGB2GRAY);
	    Imgproc.Sobel(img_gray, img_sobel, CvType.CV_8U, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);

	    Imgproc.threshold(img_sobel, img_threshold, 0, 255, 8);
	    element=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15,5));
	    Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_CLOSE, element);
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(img_threshold, contours,hierarchy, 0, 1);

	    List<MatOfPoint> contours_poly = new ArrayList<MatOfPoint>(contours.size());

	     for( int i = 0; i < contours.size(); i++ ){             

	         MatOfPoint2f  mMOP2f1=new MatOfPoint2f();
	         MatOfPoint2f  mMOP2f2=new MatOfPoint2f();

	         contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	         Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 2, true); 
	         mMOP2f2.convertTo(contours.get(i), CvType.CV_32S);


	            Rect appRect = Imgproc.boundingRect(contours.get(i));
	            if (appRect.width>appRect.height) {
	                boundRect.add(appRect);
	            }
	     }

	    return boundRect;
	}
	
	@SuppressWarnings("deprecation")
	public static HashMap<String,Object> detectObject(BufferedImage source, BufferedImage template,int number_of_occurrences) {
		
		HashMap<String,Object> result = new HashMap<>();
		 try {	 
			Mat objectImage       = Utill.BufferedImage2Mat(template);
			Mat sceneImage        = Utill.BufferedImage2Mat(source);  
			
			MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
	        featureDetector.detect(objectImage, objectKeyPoints);
	        KeyPoint[] keypoints = objectKeyPoints.toArray();

	        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
	        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
	        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
	        Scalar newKeypointColor = new Scalar(255, 0, 0);

	        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

	        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
	        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
	        featureDetector.detect(sceneImage, sceneKeyPoints);
	        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

	        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
	        Scalar matchestColor = new Scalar(0, 255, 0);

	        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

	        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
	        float nndrRatio = 0.6f;
	        for (int i = 0; i < matches.size(); i++) {
	            MatOfDMatch matofDMatch = matches.get(i);
	            DMatch[] dmatcharray = matofDMatch.toArray();
	            DMatch m1 = dmatcharray[0];
	            DMatch m2 = dmatcharray[1];

	            if (m1.distance <= m2.distance * nndrRatio) {
	                goodMatchesList.addLast(m1);

	            }
	        }
	        System.out.println(goodMatchesList.size() );
	        if (goodMatchesList.size() >= number_of_occurrences) {
	            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
	            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();
	            LinkedList<Point> objectPoints = new LinkedList<>();
	            LinkedList<Point> scenePoints = new LinkedList<>();

	            for (int i = 0; i < goodMatchesList.size(); i++) {
	                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
	                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
	            }

	            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
	            objMatOfPoint2f.fromList(objectPoints);
	            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
	            scnMatOfPoint2f.fromList(scenePoints);

	            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

	            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
	            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

	            obj_corners.put(0, 0, new double[]{0, 0});
	            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
	            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
	            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});

	            Core.perspectiveTransform(obj_corners, scene_corners, homography);

	            Mat img = BufferedImage2Mat(source);
	            
	            Point ocvPOut4 = new Point(scene_corners.get(0, 0));
	            Point ocvPOut1 = new Point(scene_corners.get(1, 0));
	            Point ocvPOut2 = new Point(scene_corners.get(2, 0));
	            Point ocvPOut3 = new Point(scene_corners.get(3, 0));
	
	            Rect rectCrop = getRectangleFromFourCounts(ocvPOut1,ocvPOut2,ocvPOut3,ocvPOut4);
	             
	            Mat croppedImage = null;
				try {
					croppedImage = new Mat(img.clone(), rectCrop);

					result.put("crop", croppedImage);
				} catch (CvException  e) {

				}
				/*
				Imgproc.circle(img,ocvPOut1,5,new Scalar(0, 255, 0), 4);//зеленый 
				Imgproc.circle(img,ocvPOut2,5,new Scalar(0, 0, 255), 4);//красный
				Imgproc.circle(img,ocvPOut3,5,new Scalar(255, 0, 0), 4);//синий
				Imgproc.circle(img,ocvPOut4,5,new Scalar(255, 255, 0), 4);//голубой
				*/

	          //  Imgproc.line(img, ocvPOut4, ocvPOut1, new Scalar(0, 255, 0), 4); // top line
	          //   Imgproc.line(img, ocvPOut1, ocvPOut2, new Scalar(0, 255, 0), 4);//right
	          //   Imgproc.line(img, ocvPOut2, ocvPOut3, new Scalar(0, 255, 0), 4);
	          //   Imgproc.line(img, ocvPOut3, ocvPOut4, new Scalar(0, 255, 0), 4);

	            MatOfDMatch goodMatches = new MatOfDMatch();
	            goodMatches.fromList(goodMatchesList);

	            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
	            
	            
	            
				if(checkNeedRotate(ocvPOut4, ocvPOut2)) {
					result.put("needrotate", true);
					if(croppedImage != null)
					Core.rotate(croppedImage,croppedImage, Core.ROTATE_90_CLOCKWISE); 
					
					Core.rotate(img,img, Core.ROTATE_90_CLOCKWISE); 
				}
	            
	            result.put("outputImage", outputImage);
	            result.put("matchoutput", matchoutput);
	            result.put("img", img);
	            result.put("goodMatches", goodMatchesList.size());
	            result.put("found", true);
	            
	        } else {
	        //    System.out.println("Object Not Found");
	            result.put("found", false);
	        }
	      //  System.out.println("Ended....");
			
		} catch (IOException e) {
		} 
		 return result;
		
	}
	
	public static Rect getRectangleFromFourCounts(Point ...p) {
		int xmax=0, ymax=0, xmin =0, ymin =0;

		for (Point point : p) {
			int x = (int)point.x;
			int y = (int)point.y;
			if(xmax<x || xmax == 0) {
				xmax = x;
			}
			if(ymax<y || ymax == 0) {
				ymax = y;
			}
			if(xmin>x || xmin == 0) {
				xmin = x;
			}
			if(ymin>y || ymin == 0) {
				ymin = y;
			}
		}
		
        int startX = (int)xmin;
        int startY = (int)ymin;
        
        int width  = (int)(xmax-xmin);
        int height = (int)(ymax-ymin);
        
        Rect rectCrop = new Rect(startX, startY, width, height);
		
		return rectCrop;
	}
	
	public static boolean checkNeedRotate(Point a, Point b) {
		if(a.x==b.x||a.x>b.x||a.y==b.y||a.y>b.y) {
			return true;
		}
		return false;
	}
	
	
	public static double getDegree(Point a, Point b) {
		double f = Math.toDegrees(Math.acos(((a.x*b.x)+(a.y*b.y))/(Math.sqrt(Math.pow(a.x, 2)+Math.pow(a.y, 2))*Math.sqrt(Math.pow(b.x, 2)+Math.pow(b.y, 2)))));
		return f;
	}
	
	public static Mat drawTableLines(Mat sceneImage) {
		
		sceneImage = correctPerspective(sceneImage);
		
		
		double ratio2 = 3;
		int kernel_size = 3;
		double lowThreshold = 30;
		
		Mat img = sceneImage.clone();
		
		Mat img_gray = new Mat(sceneImage.size(), CvType.CV_8UC4);
		Mat edges    = new Mat();
		Mat lines    = new Mat();

		Imgproc.cvtColor(sceneImage, img_gray, Imgproc.COLOR_RGB2GRAY);
	    Imgproc.GaussianBlur(img_gray, img_gray, new Size(5, 5), 5);
		Imgproc.Canny(img_gray, edges, lowThreshold, lowThreshold * ratio2,kernel_size,true);
	
		//Imgproc.Canny
	
		//Imgproc.HoughLines(edges, lines, 2, Math.PI / 180, 300);
		Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 400);

		
		for (int i = 0; i < lines.rows(); i++){
			double data[] = lines.get(i, 0);
			double rho = data[0];
			double theta = data[1];
			double cosTheta = Math.cos(theta);
			double sinTheta = Math.sin(theta);
			double x0 = cosTheta * rho;
			double y0 = sinTheta * rho;
			Point pt1 = new Point(x0 + 10000 * (-sinTheta), y0 + 10000 * cosTheta);
			Point pt2 = new Point(x0 - 10000 * (-sinTheta), y0 - 10000 * cosTheta);
			Imgproc.line(img, pt1, pt2, new Scalar(0, 0, 200), 3);
		}
		
		
		/*
		for (int i = 0; i < lines.rows(); i++) {
			double[] val = lines.get(i, 0);
			Imgproc.line(img, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
		}
		
		*/
		

		return img;
	}
	
	 public static Map<String,Mat> detectTable(Mat sceneImage){
		 
			double ratio2 = 2;
			int kernel_size = 3;
			double lowThreshold = 50;
			
		  
		    Map<String,Mat> result = new HashMap<String,Mat>();
		 
		    Mat img_gray =new Mat(sceneImage.size(), CvType.CV_8UC4),
		    	img_bw=new Mat(sceneImage.size(), CvType.CV_32F);
		    
		    Imgproc.cvtColor(sceneImage, img_gray, Imgproc.COLOR_RGB2GRAY);
		    Imgproc.blur(img_gray, img_gray, new Size(3, 3));
		    
		   // Imgproc.adaptiveThreshold(img_gray, img_bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, -2);
			Imgproc.Canny(img_gray, img_bw, lowThreshold, lowThreshold * ratio2,kernel_size,false);

		    result.put("blackwite", img_bw);
		    
		    Mat horizontal = img_bw.clone();
		    Mat vertical = img_bw.clone();
		    
		    
		    // ############################# Horizontal
		    
		    int scale = 15; // play with this variable in order to increase/decrease the amount of lines to be detected
		    // Specify size on horizontal axis
		    int horizontalsize = horizontal.cols() / scale;
		    
		    // Create structure element for extracting horizontal lines through morphology operations
		    Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalsize,1));
		    
		    // Apply morphology operations
		    Imgproc.erode(horizontal, horizontal, horizontalStructure, new Point(-1, -1),2);
		    Imgproc.dilate(horizontal, horizontal, horizontalStructure, new Point(-1, -1),2);
		    
		    result.put("horizontal", horizontal);
		    
		    // ############################# Vertical
		    
		    // Specify size on vertical axis
		    int verticalsize = vertical.rows() / scale;

		    // Create structure element for extracting vertical lines through morphology operations
		    Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 1,verticalsize));

		    // Apply morphology operations
		    Imgproc.erode(vertical, vertical, verticalStructure, new Point(-1, -1),2);
		    Imgproc.dilate(vertical, vertical, verticalStructure, new Point(-1, -1),2);
		    
		    result.put("vertical", vertical);
		    
		    
		    //Mat mask = horizontal + vertical;
		 //   adaptiveThreshold(~gray, bw, 255, CV_ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, -2);

		    
		 return result;
	 }
	 
	 public static Mat correctPerspective(Mat imgSource) {
		 
		    
		    
		    Mat sourceImage = imgSource.clone();
		    
		    Imgproc.Canny(imgSource.clone(), imgSource, 50, 50);
		    Imgproc.GaussianBlur(imgSource, imgSource, new Size(5, 5), 5);
		    // find the contours
		    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		    Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		    double maxArea = -1;
		    MatOfPoint temp_contour = contours.get(0); // the largest is at the
		                                                // index 0 for starting
		                                                // point
		    MatOfPoint2f approxCurve = new MatOfPoint2f();

		    for (int idx = 0; idx < contours.size(); idx++) {
		        temp_contour = contours.get(idx);
		        double contourarea = Imgproc.contourArea(temp_contour);
		        // compare this contour to the previous largest contour found
		        if (contourarea > maxArea) {
		            // check if this contour is a square
		            MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
		            int contourSize = (int) temp_contour.total();
		            MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
		            Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.05, true);
		            if (approxCurve_temp.total() == 4) {
		                maxArea = contourarea;
		                approxCurve = approxCurve_temp;
		            }
		        }
		    }
		    
		   

		    Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
		    
		   
		    double[] temp_double;
		    temp_double = approxCurve.get(0, 0);
		    Point p1 = new Point(temp_double[0], temp_double[1]);
		    temp_double = approxCurve.get(1, 0);
		    Point p2 = new Point(temp_double[0], temp_double[1]);
		    temp_double = approxCurve.get(2, 0);
		    Point p3 = new Point(temp_double[0], temp_double[1]);
		    temp_double = approxCurve.get(3, 0);
		    Point p4 = new Point(temp_double[0], temp_double[1]);
		    List<Point> source = new ArrayList<Point>();
		    source.add(p1);
		    source.add(p2);
		    source.add(p3);
		    source.add(p4);
		    Mat startM = Converters.vector_Point2f_to_Mat(source);
		    Mat result = warp(sourceImage, startM,sourceImage.width(),sourceImage.height());
		    
		    return result;
		}

	 public static Mat warp(Mat inputMat, Mat startM,int resultWidth,int resultHeight) {
		 
		    
		    Core.addWeighted(inputMat, 1.5, inputMat, -0.5, 0, inputMat);

		    Point ocvPOut1 = new Point(0, 0);
		    Point ocvPOut2 = new Point(0, resultHeight);
		    Point ocvPOut3 = new Point(resultWidth, resultHeight);
		    Point ocvPOut4 = new Point(resultWidth, 0);


		    Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);

		    List<Point> dest = new ArrayList<Point>();
		    dest.add(ocvPOut1);
		    dest.add(ocvPOut2);
		    dest.add(ocvPOut3);
		    dest.add(ocvPOut4);

		    Mat endM = Converters.vector_Point2f_to_Mat(dest);

		    Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

		    Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

		    return outputMat;
		}
	 
	@SuppressWarnings("deprecation")
	public static Map<String,Mat> cutImage(Mat sceneImage, Mat objectImage,int number_of_occurrences ,boolean topCut, boolean sourceWidth) {
		
		Map<String,Mat> result = new HashMap<String,Mat>();
		
		try {
			
			//Imgproc.cvtColor(sceneImage, sceneImage, Imgproc.COLOR_RGB2GRAY);
			//Imgproc.cvtColor(objectImage, objectImage, Imgproc.COLOR_RGB2GRAY);

			
			MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
			FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
			featureDetector.detect(objectImage, objectKeyPoints);
			MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
			DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
			descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

			Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
			Scalar newKeypointColor = new Scalar(255, 0, 0);

			Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

			MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
			MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
			featureDetector.detect(sceneImage, sceneKeyPoints);
			descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

			List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
			DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
			descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

			LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
			float nndrRatio = 0.6f;
			for (int i = 0; i < matches.size(); i++) {
				MatOfDMatch matofDMatch = matches.get(i);
				DMatch[] dmatcharray = matofDMatch.toArray();
				DMatch m1 = dmatcharray[0];
				DMatch m2 = dmatcharray[1];

				if (m1.distance <= m2.distance * nndrRatio) {
					goodMatchesList.addLast(m1);

				}
			}
			System.out.println("##############################################################");
			System.out.println(goodMatchesList.size());
			if (goodMatchesList.size() >= number_of_occurrences) {
				// System.out.println("Object Found!!!");

				List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
				List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

				LinkedList<Point> objectPoints = new LinkedList<>();
				LinkedList<Point> scenePoints = new LinkedList<>();

				for (int i = 0; i < goodMatchesList.size(); i++) {
					objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
					scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
				}

				MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
				objMatOfPoint2f.fromList(objectPoints);
				MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
				scnMatOfPoint2f.fromList(scenePoints);

				Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

				Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
				Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

				obj_corners.put(0, 0, new double[] { 0, 0 });
				obj_corners.put(1, 0, new double[] { objectImage.cols(), 0 });
				obj_corners.put(2, 0, new double[] { objectImage.cols(), objectImage.rows() });
				obj_corners.put(3, 0, new double[] { 0, objectImage.rows() });

				Core.perspectiveTransform(obj_corners, scene_corners, homography);

				Point ocvPOut4 = new Point(scene_corners.get(0, 0));
				Point ocvPOut1 = new Point(scene_corners.get(1, 0));
				Point ocvPOut2 = new Point(scene_corners.get(2, 0));
				Point ocvPOut3 = new Point(scene_corners.get(3, 0));
				
				if(sourceWidth) {
					ocvPOut4.x = 0;
					ocvPOut3.x = 0;
					ocvPOut1.x = sceneImage.width();
					ocvPOut2.x = sceneImage.width();	
				}

				Rect rectCrop = getRectangleFromFourCounts(ocvPOut1, ocvPOut2, ocvPOut3, ocvPOut4);

				//Mat img = BufferedImage2Mat(source);
				Mat croppedImage = new Mat(sceneImage.clone(), rectCrop);

				Point top_left = new Point(0, 0);
				Point top_right = new Point(sceneImage.width(), 0);
				Point bottom_left = new Point(0, sceneImage.height());
				Point bottom_right = new Point(sceneImage.width(), sceneImage.height());

				Rect rectCropSource = null;

				if (topCut) {
					rectCropSource = getRectangleFromFourCounts(new Point(top_right.x, ocvPOut2.y), bottom_left,bottom_right, new Point(top_left.x, ocvPOut3.y));
				} else {
					rectCropSource = getRectangleFromFourCounts(top_right, new Point(bottom_left.x, ocvPOut1.y),new Point(bottom_right.x, ocvPOut4.y), top_left);
				}

				Mat croppedSourceImage = new Mat(sceneImage.clone(), rectCropSource);

				result.put("source", croppedSourceImage);
				result.put("image", croppedImage);

			}

		} catch (CvException e) {
		}

		return result;
	}
	
}
