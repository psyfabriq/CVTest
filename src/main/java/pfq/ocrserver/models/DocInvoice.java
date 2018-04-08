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
		getTemplate("header",12,true);
		getTemplate("type",12,true);
		getTemplate("body",12,true);
		getTemplate("bottom",12,false);
		Map<String,Mat> listObj = Utill.detectTable(getImg());
		listObj.forEach((k,v)->listObjectsToFound.put(k,v));

	}
	
	private void getTemplate(String name, int goodMaches,boolean topCaut) {
		try {
			File f = ResourceUtils.getFile(path+"invoice/"+name+".jpg");
			if (f != null) {
				Map<String,Mat> listObj = Utill.cutImage(getImg(),Utill.BufferedImage2Mat(ImageIO.read(f)),goodMaches,topCaut,true);
				this.img = listObj.get("source")!=null?listObj.get("source"):this.img;
				if(listObj.get("image")!=null)
				this.listObjectsToFound.put(name,listObj.get("image"));
			}
		} catch (IOException e) {
			System.out.print(e);
		}

	}
	
}
