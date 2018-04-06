package pfq.ocrserver.models;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.davidcarboni.ResourceUtils;

import pfq.ocrserver.state.DocumentType;

public class DocPackingList extends Document {

	public DocPackingList() {
		super();
		docType = DocumentType.PACKING_LIST;
	}
	
	@Override
	public void instance() {
		try {
			File file = ResourceUtils.getFile(path+"packing-list/type.jpg");
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
			 DocPackingList c = (DocPackingList) super.clone();
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
