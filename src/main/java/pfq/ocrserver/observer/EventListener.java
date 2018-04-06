package pfq.ocrserver.observer;

import java.io.File;

import pfq.ocrserver.models.Document;

public interface EventListener {
	 public void update(Document doc);
}
