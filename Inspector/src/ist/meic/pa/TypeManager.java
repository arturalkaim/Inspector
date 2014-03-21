package ist.meic.pa;

import java.util.HashMap;
import java.util.TreeMap;

public class TypeManager {

	HashMap<Class<?>, Class<?>> tm = new HashMap<Class<?>, Class<?>>();

	public TypeManager() {
		tm.put(boolean.class, Boolean.class);
		tm.put(byte.class, Byte.class);
		tm.put(char.class, Character.class);
		tm.put(double.class, Double.class);
		tm.put(float.class, Float.class);
		tm.put(int.class, Integer.class);
		tm.put(long.class, Long.class);
		tm.put(short.class, Short.class);
		tm.put(void.class, Void.class);
	}

	public <T> Class<T> box(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) tm.get(c) : c;
	}

}
