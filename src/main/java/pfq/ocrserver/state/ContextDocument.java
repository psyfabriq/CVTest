package pfq.ocrserver.state;

import java.awt.image.BufferedImage;

import pfq.ocrserver.models.Document;
import pfq.ocrserver.observer.EventListener;
import pfq.ocrserver.observer.EventManager;

public  class ContextDocument  {
	private final EventManager events;
	private final String listenerName;
	private State state;
	protected final BufferedImage originalImage;
	protected DocumentStatus status;
	protected DocumentType docType;
	protected String errorMessage;
	protected Document document;
	protected int prosent = 0;
	protected boolean needRotate;

	public  ContextDocument(BufferedImage originalImage,EventListener type) {
		super();
		this.originalImage = originalImage;
		this.status  = DocumentStatus.START;
		this.docType = DocumentType.NOT_FOUND;
		this.listenerName = type.getClass().getSimpleName();
		this.events = new EventManager(listenerName);
		this.events.subscribe(listenerName,type);
	}
	
	public void start() {
		this.state = StateConfig.getState(StartState.class);
		this.state.doAction(this);
	}
	
	public void setState(State state) {
		this.state = state;
		pull();
	}

	public State getCurrentState() {
		return state;
	}
	
	private void pull() {
		state.doAction(this);
	}
	
	public void notifyListener() {
		events.notify(listenerName, document);
	}
	
	

}
