package pfq.ocrserver.utils;

import java.util.HashMap;
import java.util.Optional;


public class MemoryUtil {
	HashMap<String,String> variables = null;
	HashMap<String,Object> variablesObj = null;
	private static MemoryUtil SINGLETON;
	private MemoryUtil() {
		this.variables = new HashMap<>();
		this.variablesObj = new HashMap<>();
	}
	
	static {
		SINGLETON = new MemoryUtil();
	}
	
	
	public static Optional<String> get(String key) {
		return Optional.ofNullable(SINGLETON.variables.get(key));
	}
	
	public static Optional<Object> getObj(String key) {
		return Optional.ofNullable(SINGLETON.variablesObj.get(key)) ;
	}
	
	public static String get(String key, String deflt) {
		
		if (SINGLETON.get(key).isPresent()) {
			return SINGLETON.get(key).get();
		} else {
			return deflt;
		}
	}
	
	public static String get(String key, String deflt, boolean savee) {
		String obj = SINGLETON.variables.get(key);
		if (obj == null) {
			if(savee) {
			   SINGLETON.variables.put(key, deflt);
			}
			return deflt;
		} else {
			return obj;
		}
	}
	
	public static int getInt(String key, int deflt) {
		String obj = SINGLETON.variables.get(key);
		if (obj == null) {
			return deflt;
		} else {
			return new Integer(obj).intValue();
		}
	}
	
	public static void put(String key, String data) {
		if (data == null) {
			throw new IllegalArgumentException();
		} else {
			SINGLETON.variables.put(key, data);
		}
	}
	
	public static void putObj(String key, Object data) {
		if (data == null) {
			throw new IllegalArgumentException();
		} else {
			SINGLETON.variablesObj.put(key, data);
		}
	}
}

