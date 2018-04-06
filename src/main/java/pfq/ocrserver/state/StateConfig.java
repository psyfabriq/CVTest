package pfq.ocrserver.state;

import java.util.HashMap;

import pfq.ocrserver.models.*;

public class StateConfig {
	private final HashMap<String, State> list;
	private StateConfig() {
		super();
		list = new HashMap<String, State>();

		list.put(StartState.class.getSimpleName(), new StartState());
		list.put(ErrorState.class.getSimpleName(), new ErrorState());
		list.put(ProcessState.class.getSimpleName(), new ProcessState());	
	}

	private static class SingletonHolder {
		public static final StateConfig instance = new StateConfig();
	}

	public static StateConfig getInstance() {
		return SingletonHolder.instance;
	}

	private State getState(String key) {
		return list.get(key);
	}

	public static <T extends State> T getState(Class<T> type) {
		return type.cast(SingletonHolder.instance.getState(type.getSimpleName()));
	}


}
