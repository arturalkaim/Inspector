package ist.meic.pa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Search {

	public Field findField(String name, Class<?> theCForce)
			throws NoSuchFieldException {

		for (Field f : theCForce.getDeclaredFields()) {
			if (f.getName().equals(name)) {
				f.setAccessible(true);
				return f;
			}
		}
		if (!theCForce.getCanonicalName().equals("java.lang.Object")) {
			return findField(name, theCForce.getSuperclass());
		}
		throw new NoSuchFieldException("Field named: " + name + " not found!");
	}
	
	public Method findMethod(Class<?> theCForce, String name, int nArgs)
			throws NoSuchMethodException {
		for (Method m : theCForce.getDeclaredMethods()) {
			if (m.getName().equals(name)
					&& m.getParameterTypes().length == nArgs)
				return m;
		}
		if (!theCForce.getCanonicalName().equals("java.lang.Object")) {
			return findMethod(theCForce.getSuperclass(), name, nArgs);
		}
		throw new NoSuchMethodException("Method named: " + name + " not found!");
	}
}
