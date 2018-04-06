package pfq.ocrserver.models;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.davidcarboni.ResourceUtils;

import pfq.ocrserver.state.DocumentType;

public class DocInvoiceTexture extends Document {

	public DocInvoiceTexture() {
		super();
		docType = DocumentType.INVOIS_TEXTURE;
	}
	@Override
	public void instance() {
		try {
			File file = ResourceUtils.getFile(path+"invoice-texture/type.jpg");
			if (file != null) {
				bufferedTemplateImage = ImageIO.read(file);				
		    }
		} catch (NullPointerException | IOException e) {
			//e.printStackTrace();
		}	
	}
	
	@Override
	public Document clone() throws CloneNotSupportedException {
		 try {
			 DocInvoiceTexture c = (DocInvoiceTexture) super.clone();
	            return c;
	        } catch (CloneNotSupportedException e) {
	            throw new InternalError();
	        }
	}
	@Override
	public void startWork() {
		// TODO Auto-generated method stub
		
	}
}
