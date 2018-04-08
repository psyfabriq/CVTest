package pfq.ocrserver.models;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;

import pfq.ocrserver.state.DocumentType;

public abstract class Document implements Cloneable {
	//protected ClassLoader classLoader = getClass().getClassLoader();
	protected BufferedImage bufferedTemplateImage;
	protected boolean objectFound;
	protected int goodMatches;
	protected DocumentType docType;
	protected static String path = "/pfq/ocrserver/btemplates/";
	
	protected Mat outputImage,matchoutput,img,croppedImage;
	protected Map<String,Mat> listObjectsToFound;
	
    public Document() {
    	//System.out.println("DocSource");
    	instance();
    	this.listObjectsToFound = new HashMap<String,Mat>();
	}

	public abstract void instance();
	
    public Document clone() throws CloneNotSupportedException {
        return (Document) super.clone();
    }

	public BufferedImage getBufferedTemplateImage() {
		return bufferedTemplateImage;
	}

	public boolean isObjectFound() {
		return objectFound;
	}

	public void setObjectFound(boolean objectFound) {
		this.objectFound = objectFound;
	}

	public int getGoodMatches() {
		return goodMatches;
	}

	public void setGoodMatches(int goodMatches) {
		this.goodMatches = goodMatches;
	}

	public DocumentType getDocType() {
		return docType;
	}

	public Mat getOutputImage() {
		return outputImage;
	}

	public void setOutputImage(Mat outputImage) {
		this.outputImage = outputImage;
	}

	public Mat getMatchoutput() {
		return matchoutput;
	}

	public void setMatchoutput(Mat matchoutput) {
		this.matchoutput = matchoutput;
	}

	public Mat getImg() {
		return img;
	}

	public void setImg(Mat img) {
		this.img = img;
	}

	public Mat getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(Mat croppedImage) {
		this.croppedImage = croppedImage;
	}
	
	
	
	public Map<String, Mat> getListObjectsToFound() {
		return listObjectsToFound;
	}

	public abstract void startWork();
	
	
    
    
}
