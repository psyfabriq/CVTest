package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
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
	
	public static List<Rect> detectRectangels(Mat img){    
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
	
	public static void detectObject(BufferedImage source, BufferedImage template) {
		
		 try {
			Mat objectImage       = Utill.BufferedImage2Mat(template);
			Mat sceneImage        = Utill.BufferedImage2Mat(source);  
			
			MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
	        System.out.println("Detecting key points...");
	        featureDetector.detect(objectImage, objectKeyPoints);
	        KeyPoint[] keypoints = objectKeyPoints.toArray();
	        System.out.println(keypoints);

	        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
	        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
	        System.out.println("Computing descriptors...");
	        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	        // Create the matrix for output image.
	        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
	        Scalar newKeypointColor = new Scalar(255, 0, 0);

	        System.out.println("Drawing key points on object image...");
	        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

	        // Match object image with the scene image
	        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
	        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
	        System.out.println("Detecting key points in background image...");
	        featureDetector.detect(sceneImage, sceneKeyPoints);
	        System.out.println("Computing descriptors in background image...");
	        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

	        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
	        Scalar matchestColor = new Scalar(0, 255, 0);

	        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	        System.out.println("Matching object and scene images...");
	        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

	        System.out.println("Calculating good match list...");
	        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

	        float nndrRatio = 0.7f;
	        System.out.println(matches.size());
	        for (int i = 0; i < matches.size(); i++) {
	            MatOfDMatch matofDMatch = matches.get(i);
	            DMatch[] dmatcharray = matofDMatch.toArray();
	            DMatch m1 = dmatcharray[0];
	            DMatch m2 = dmatcharray[1];

	            if (m1.distance <= m2.distance * nndrRatio) {
	                goodMatchesList.addLast(m1);

	            }
	        }

	        if (goodMatchesList.size() >= 7) {
	            System.out.println("Object Found!!!");

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

	            System.out.println("Transforming object corners to scene corners...");
	            Core.perspectiveTransform(obj_corners, scene_corners, homography);

	            Mat img = Utill.BufferedImage2Mat(source);

	            Imgproc.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
	            Imgproc.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
	            Imgproc.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
	            Imgproc.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);

	            System.out.println("Drawing matches image...");
	            MatOfDMatch goodMatches = new MatOfDMatch();
	            goodMatches.fromList(goodMatchesList);

	            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);

	            Imgcodecs.imwrite("outputImage.jpg", outputImage);
	            Imgcodecs.imwrite("matchoutput.jpg", matchoutput);
	            Imgcodecs.imwrite("img.jpg", img);
	        } else {
	            System.out.println("Object Not Found");
	        }

	        System.out.println("Ended....");

			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
		
	}
}
