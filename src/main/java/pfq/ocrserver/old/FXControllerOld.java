package pfq.ocrserver.old;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pfq.ocrserver.utils.Utill;


public class FXControllerOld implements Initializable {

    @FXML
    private ImageView templateFrame;

    @FXML
    private ImageView currentFrame;
    
    @FXML
    private ImageView resultFrame;

	@FXML
	private Button openFileButton;
	
    @FXML
    private Button openTemplateButton;

	@FXML
	private Button runButton;
	
    @FXML
    private Button findContursButton;
	
	private BufferedImage bufferedImage;
	private BufferedImage bufferedTemplateImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("View is now loaded!");
	}
	

	@FXML
	void handleOpenTemplateButtonAction(ActionEvent event) {

		Node node = (Node) event.getSource();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Template File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File file = fileChooser.showOpenDialog(node.getScene().getWindow());

		if (file != null) {
			try {
				bufferedTemplateImage = ImageIO.read(file);
				Image image = SwingFXUtils.toFXImage(bufferedTemplateImage, null);
				templateFrame.setImage(image);

				openTemplateButton.setDisable(false);
			} catch (IOException ex) {
				// throw new FileNotFoundException();
			}
		}

	}

	@FXML
	void handleOpenFileButtonAction(ActionEvent event) {
		Node node = (Node) event.getSource();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File file = fileChooser.showOpenDialog(node.getScene().getWindow());

		if (file != null) {
			try {
				bufferedImage = ImageIO.read(file);
				
				/*
				bufferedImage = ImageUtill.toGray(bufferedImage);
				bufferedImage = ImageUtill.binarize(bufferedImage);
	            double res = Deskew.doIt(bufferedImage);
	            System.out.println(" -- skew " + " :" + res);
	            */
				
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
				currentFrame.setImage(image);

				runButton.setDisable(false);
			} catch (IOException ex) {
				// throw new FileNotFoundException();
			}
		}
	}
	
	
	

	@FXML
	void handleRunButtonAction(ActionEvent event) {
		
	        Mat source = null;
	        Mat gray = null;
	        Mat edged = null;
	        Mat closed = null;
	        Mat template = null;
			try {
				source = Utill.BufferedImage2Mat(bufferedImage);
			   // template = Utill.BufferedImage2Mat(bufferedTemplateImage);
			    gray  = new Mat(source.size(), CvType.CV_8UC4);
			    edged = new Mat(source.size(), CvType.CV_32F);
			    closed = new Mat(source.size(), CvType.CV_32F);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //Imgproc.cvtColor(source, source, Imgproc.COLOR_RGB2BGR);
	        
	      
		    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
		    Imgproc.GaussianBlur(gray, gray, new Size(5,5), 0);
		    Imgproc.adaptiveThreshold(gray, edged, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,5, 35);
		    Imgproc.Canny(edged,edged,10,40 );
		   // Imgproc.adaptiveThreshold(source, source, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,15, -1);
		    
		    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(7,7));
		    Imgproc.morphologyEx(edged,closed, Imgproc.MORPH_CLOSE, kernel);
	       
		    
		    
		    List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); 
		    Imgproc.findContours(closed.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
		    
		    
		    for (MatOfPoint matOfPoint : contours) {
		        MatOfPoint2f mat2f = new MatOfPoint2f();
		        matOfPoint.convertTo(mat2f, CvType.CV_32FC2);
		        
		        RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );
		        
		        Point[] vertices = new Point[4];
		        rotatedRect.points(vertices);
		        List<MatOfPoint> boxContours = new ArrayList<>();
		        boxContours.add(new MatOfPoint(vertices));
		        Imgproc.drawContours( source, boxContours, 0, new Scalar(0, 250, 0), 4);

			}
		    
		   
		
		    
	        BufferedImage resultBufferedImage = null;
	        BufferedImage testBufferedImage = null;
			try {
				resultBufferedImage = Utill.Mat2BufferedImage(source);
				testBufferedImage = Utill.Mat2BufferedImage(closed);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        Image image = SwingFXUtils.toFXImage(resultBufferedImage, null);
	        Image imagetest = SwingFXUtils.toFXImage(testBufferedImage, null);
	        currentFrame.setImage(image);
	        resultFrame.setImage(imagetest);

	}
	
    @FXML
    void handleFindContursButtonAction(ActionEvent event) {
    	
    	  Mat image = null;
			try {
				image = Utill.BufferedImage2Mat(bufferedImage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			    Mat imageHSV = new Mat(image.size(), CvType.CV_8UC4);
			    Mat imageBlurr = new Mat(image.size(), CvType.CV_8UC4);
			    Mat imageA = new Mat(image.size(), CvType.CV_32F);
			    //Mat imageCanny = new Mat(image.size(), CvType.CV_32F);
			    
			    
			    
			    Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
			    Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5,5), 0);
			    Imgproc.adaptiveThreshold(imageBlurr, imageA, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,15, -1);
			    //Imgproc.Canny(imageA,imageCanny,0,50 );
			    


				
			    
			    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
			    Imgproc.findContours(imageHSV, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
			    
			    for(int i=0; i< contours.size();i++){
			        System.out.println(Imgproc.contourArea(contours.get(i)));
			        if (Imgproc.contourArea(contours.get(i)) > 50 ){
			            Rect rect = Imgproc.boundingRect(contours.get(i));
			            System.out.println(rect.height);
			            if (rect.height > 28){
			    	        Imgproc.rectangle(image, new Point(rect.x,rect.height), new Point(rect.y,rect.width),new Scalar(0,0,255));
			          //  Core.rectangle(image, new Point(rect.x,rect.height), new Point(rect.y,rect.width),new Scalar(0,0,255));
			            }
			        }
			    }
			    
			    BufferedImage resultBufferedImage = null;
				try {
					resultBufferedImage = Utill.Mat2BufferedImage(image);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		        Image imagew = SwingFXUtils.toFXImage(resultBufferedImage, null);
		        currentFrame.setImage(imagew);


    }
    
    public static RotatedRect getBestRectByAreas(List<RotatedRect> boundingRects) {
        RotatedRect bestRect = null;

        if (boundingRects.size() >= 1) {
            RotatedRect boundingRect;
            Point[] vertices = new Point[4];
            Rect rect;
            double maxArea;
            int ixMaxArea = 0;

            // find best rect by area
            boundingRect = boundingRects.get(ixMaxArea);
            boundingRect.points(vertices);
            rect = Imgproc.boundingRect(new MatOfPoint(vertices));
            maxArea = rect.area();

            for (int ix = 1; ix < boundingRects.size(); ix++) {
                boundingRect = boundingRects.get(ix);
                boundingRect.points(vertices);
                rect = Imgproc.boundingRect(new MatOfPoint(vertices));

                if (rect.area() > maxArea) {
                    maxArea = rect.area();
                    ixMaxArea = ix;
                }
            }

            bestRect = boundingRects.get(ixMaxArea);
        }

        return bestRect;
    }
}
