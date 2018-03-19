package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Size;
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
}
