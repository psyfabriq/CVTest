package pfq.ocrserver.state;

import java.io.IOException;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pfq.ocrserver.models.DocAct;
import pfq.ocrserver.models.DocInvoice;
import pfq.ocrserver.models.DocInvoiceTexture;
import pfq.ocrserver.models.DocPackingList;
import pfq.ocrserver.models.Document;
import pfq.ocrserver.utils.Utill;

public class StartState implements State {
	
	private final HashMap<String, Document> documents ;
	
	
	public StartState() {
		super();
		documents = new HashMap<String, Document>();
		documents.put(DocAct.class.getSimpleName(), new DocAct());
		documents.put(DocInvoice.class.getSimpleName(), new DocInvoice());
		documents.put(DocInvoiceTexture.class.getSimpleName(), new DocInvoiceTexture());
		documents.put(DocPackingList.class.getSimpleName(), new DocPackingList());
	}
	
	private void check(Document v, ContextDocument context ) {
	
		//Mat outimage = v.getOutputImage();
		try {
			Mat img = v.getCroppedImage();
			//Mat templ = Utill.BufferedImage2Mat(v.getBufferedTemplateImage());
			//int match_method = Imgproc.TM_CCOEFF;
			
			 // Create the result matrix
		    //int result_cols = img.cols() - templ.cols() + 1;
		   // int result_rows = img.rows() - templ.rows() + 1;
		    //Mat res = new Mat(result_rows, result_cols, CvType.CV_32F);
		    
		    // Do the Matching and Normalize
		   // Imgproc.matchTemplate(img, templ, res, match_method);
		   // Core.normalize(res, res, 0, 255, Core.NORM_MINMAX, -1, new Mat());

		    // Localizing the best match with minMaxLoc
		   // Core.MinMaxLocResult mmr = Core.minMaxLoc(res);
		    
		   // double minMatchQuality = 0.9; // with CV_TM_SQDIFF_NORMED you could use 0.1
		    
		   //if (mmr.maxVal > minMatchQuality) { // with CV_TM_SQDIFF_NORMED use minValue < minMatchQuality 
				context.document = v;
				context.docType  = v.getDocType();
				context.prosent  = 10;
				//context.needRotate = needrotate;
				
		  //  }
		  // else {
		   //	System.out.println(mmr.maxVal );
		    //}
	        
		} catch (CvException e) {

	    	System.out.println( e.toString() );
	    //	check( v,  context,  needrotate );
		} 
	
	}


	@Override
	public void doAction(ContextDocument context) {
		System.out.println("StartState");
		ErrorState es = StateConfig.getState(ErrorState.class);
		if(context.originalImage == null) {
			es.setErrorMesage("not set original document");
			context.setState(es);
		}else {
			documents.forEach((k, v) -> {
				System.out.println(k);
				HashMap<String,Object> result = Utill.detectObject(context.originalImage, v.getBufferedTemplateImage(), 12);
				if((boolean)result.get("found")) {
					v.setGoodMatches((int)result.get("goodMatches"));
					v.setObjectFound((boolean)result.get("found"));
					v.setImg((Mat)result.get("img"));
					v.setOutputImage((Mat)result.get("outputImage"));
					v.setMatchoutput((Mat)result.get("matchoutput"));
					v.setCroppedImage((Mat)result.get("crop"));

					if(context.document == null || context.document.getGoodMatches() < v.getGoodMatches()) {
						
						if (v.getCroppedImage() != null) {				
							check(v,context);
						}

					}
				}
			});	
			
			if(context.docType == DocumentType.NOT_FOUND) {
				es.setErrorMesage("Coud not check type of document");
				context.setState(es);
			}else {
				ProcessState ps = StateConfig.getState(ProcessState.class);
				context.setState(ps);
				
			}

		}
		
	}

}
