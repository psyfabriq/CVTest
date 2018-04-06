package pfq.ocrserver.state;

public class ProcessState implements State{

	@Override
	public void doAction(ContextDocument context) {
		System.out.println("ProcessState");
		context.document.startWork();
		context.notifyListener();
		
	}

}
