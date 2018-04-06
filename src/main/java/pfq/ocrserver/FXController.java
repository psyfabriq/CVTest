package pfq.ocrserver;

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
import pfq.ocrserver.models.Document;
import pfq.ocrserver.observer.EventListener;
import pfq.ocrserver.state.ContextDocument;
import pfq.ocrserver.utils.MemoryUtil;
import pfq.ocrserver.utils.Utill;

public class FXController implements Initializable, EventListener {

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
	private ContextDocument document;

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
				resultFrame.setImage(image);
				runButton.setDisable(false);
			} catch (IOException ex) {
			}
		}

	}

	@FXML
	void handleRunButtonAction(ActionEvent event) {

		document = new ContextDocument(bufferedImage, this);
		document.start();
		
		

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("View is now loaded!");
	}

	@Override
	public void update(Document doc) {
		test1Frame.setImage(Utill.getImage(doc.getMatchoutput()));
		test2Frame.setImage(Utill.getImage(doc.getImg()));
		test3Frame.setImage(Utill.getImage(doc.getOutputImage()));
		test4Frame.setImage(Utill.getImage(doc.getCroppedImage()));
	}
}
