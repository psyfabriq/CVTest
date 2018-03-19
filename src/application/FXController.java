package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class FXController implements Initializable {

	    @FXML
	    private Button openFileButton;

	    @FXML
	    private Button runButton;

	    @FXML
	    private Button openTemplateButton;

	    @FXML
	    private Button findContursButton;

	    @FXML
	    private ImageView resultFrame;

	    @FXML
	    private ImageView test1Frame;

	    @FXML
	    private ImageView test2Frame;

	    @FXML
	    private ImageView test3Frame;

	    @FXML
	    private ImageView test4Frame;

	    @FXML
	    private ImageView test5Frame;

	    @FXML
	    private ImageView templateFrame;


	    @FXML
	    private Label label1;

	    @FXML
	    private Slider slider1;

	    @FXML
	    private Label label2;

	    @FXML
	    private Slider slider2;

	    @FXML
	    private Label label3;

	    @FXML
	    private Slider slider3;

	    @FXML
	    private Label label4;

	    @FXML
	    private Slider slider4;

	    @FXML
	    private Label label5;

	    @FXML
	    private Slider slider5;

	    @FXML
	    private Label label6;

	    @FXML
	    private Slider slider6;

	    @FXML
	    private Label label7;

	    @FXML
	    private Slider slider7;

	    @FXML
	    private Label label8;

	    @FXML
	    private Slider slider8;

	    @FXML
	    private Label label9;

	    @FXML
	    private Slider slider9;

	    @FXML
	    private Label label10;

	    @FXML
	    private Slider slider10;

	    
	    
		private BufferedImage bufferedImage;
		private BufferedImage bufferedTemplateImage;
		
       private Mat source = null;
       private Mat gray = null;
       private Mat edged = null;
       private Mat closed = null;
       private Mat template = null;
       
       double threshold_c = 35;
       double canny_a = 10;
       double canny_b = 40;
       double kernel_a = 7;
       double  kernel_b = 7;
		

	    @FXML
	    void handleFindContursButtonAction(ActionEvent event) {

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
					Image image = SwingFXUtils.toFXImage(bufferedImage, null);
					test5Frame.setImage(image);
					runButton.setDisable(false);
				} catch (IOException ex) {
				}
			}

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
				}
			}


	    }

	    @FXML
	    void handleRunButtonAction(ActionEvent event) {
	    	
	     	pepairImages();
	    	 List<Rect> letterBBoxes1=Utill.detectLetters(source);
	    	 for(int i=0; i< letterBBoxes1.size(); i++)
	             Imgproc.rectangle(source,letterBBoxes1.get(i).br(), letterBBoxes1.get(i).tl(),new Scalar(0,255,0),3,8,0); 
	    	 
	    	 resultFrame.setImage(Utill.getImage(source));

	    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("View is now loaded!");
		
		slider1.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    label1.setText(String.format("%.2f", new_val));
                    threshold_c = (double) new_val;
                    pepairImages();
            }
        });
		
		slider2.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    label2.setText(String.format("%.2f", new_val));
                    canny_a = (double) new_val;
                    pepairImages();
            }
        });
		slider3.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    label3.setText(String.format("%.2f", new_val));
                    canny_b = (double) new_val;
                    pepairImages();
            }
        });
		slider4.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    label4.setText(String.format("%.2f", new_val));
                    kernel_a = (double) new_val;
                    pepairImages();
            }
        });
		slider5.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    label5.setText(String.format("%.2f", new_val));
                    kernel_b = (double) new_val;
                    pepairImages();
            }
        });
	}
	
	private void pepairImages() {
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
		
		Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
	    Imgproc.GaussianBlur(gray, gray, new Size(5,5), 0);
	    Imgproc.adaptiveThreshold(gray, edged, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,5, threshold_c);
	    Imgproc.Canny(edged,edged,canny_a,canny_b );
	   // Imgproc.adaptiveThreshold(source, source, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,15, -1);
	    
	    Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(kernel_a,kernel_b));
	    Imgproc.morphologyEx(edged,closed, Imgproc.MORPH_CLOSE, kernel);
	    
	    test1Frame.setImage(Utill.getImage(gray));
	    test2Frame.setImage(Utill.getImage(edged));
	    test3Frame.setImage(Utill.getImage(kernel));
	    test4Frame.setImage(Utill.getImage(closed));
	}
}
