package pfq.ocrserver.state;

public class ErrorState implements State{

	private String message;
	
	@Override
	public void doAction(ContextDocument context) {
		System.out.println("Error");
		context.errorMessage = message;
		context.status       = DocumentStatus.ERROR;
		message = "";	
	}
	public void setErrorMesage(String error) {
		this.message = error;
	}

}
