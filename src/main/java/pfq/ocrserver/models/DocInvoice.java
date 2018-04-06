package pfq.ocrserver.models;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import com.github.davidcarboni.ResourceUtils;

import pfq.ocrserver.state.DocumentType;
import pfq.ocrserver.utils.Utill;

public class DocInvoice extends Document{
	
	public DocInvoice() {
		super();
		docType = DocumentType.INVOIS;
	}

	@Override
	public void instance() {
		try {
			File file = ResourceUtils.getFile(path+"invoice/type.jpg");
			if (file != null) {bufferedTemplateImage = ImageIO.read(file);}
		} catch (NullPointerException | IOException e) {
			//e.printStackTrace();
		}

	}
	
	@Override
	public Document clone() throws CloneNotSupportedException {
		 try {
			 DocInvoice c = (DocInvoice) super.clone();
	            return c;
	        } catch (CloneNotSupportedException e) {
	            throw new InternalError();
	        }
	}

	@Override
	public void startWork() {
		getHeader();
		getNumberDocument();
	}
	private void getHeader() {
		try {
			File f = ResourceUtils.getFile(path+"invoice/header.jpg");
			if (f != null) {
				Map<String,Mat> listObj = Utill.cutImage(getImg(),Utill.BufferedImage2Mat(ImageIO.read(f)),12,true,false);
				this.img = listObj.get("source");
				this.listObjectsToFound.put("header",listObj.get("image"));
			}
		} catch (IOException e) {
		}

	}
	
	private void getNumberDocument() {
		try {
			File f = ResourceUtils.getFile(path+"invoice/type.jpg");
			if (f != null) {
				Map<String,Mat> listObj = Utill.cutImage(getImg(),Utill.BufferedImage2Mat(ImageIO.read(f)),12,true,true);
				this.img = listObj.get("source");
				this.listObjectsToFound.put("number",listObj.get("image"));
			}
		} catch (IOException e) {
		}
	}
}
