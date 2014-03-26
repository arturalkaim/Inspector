package ist.meic.pa;

import ist.meic.pa.TypeManager;
import ist.meic.pa.exceptions.ObjectNotExistsException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class InspectorGadgets {

	private Search _s;
	private History _h;
	private Object theForce;

	public InspectorGadgets(Search _s, History _h) {
		this._s = _s;
		this._h = _h;
	}

	/**
	 * @param args
	 * @param theCForce
	 * @return
	 */
	public Class<?> goUpPlus(String[] args, Class<?> theCForce) {
		while (args[1].startsWith("+")
				&& !theCForce.getSuperclass().getCanonicalName()
						.equals("java.lang.Object")) {
			theCForce = theCForce.getSuperclass();
			args[1] = args[1].substring(1);
		}
		return theCForce;
	}

	public void invokeMethod(Class<?> theCForce, String[] args)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException, SecurityException, ObjectNotExistsException {
		try {
			if (args.length == 2) {
				callNoArgs(theCForce, args);

			} else if (args.length > 2) {
				callWithArgs(theCForce, args);

			}
		} catch (NoSuchMethodException e) {
			if (theCForce.getSuperclass().getName().equals("java.lang.Object")) {
				if (e.getMessage().startsWith("Method named"))
					throw e;
				else
					throw new NoSuchMethodException("Method named: " + args[1]
							+ " not found!!");
			} else {
				invokeMethod(theCForce.getSuperclass(), args);
			}
		}
	}

	/**
	 * @param theCForce
	 * @param args
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ObjectNotExistsException
	 */
	private void callWithArgs(Class<?> theCForce, String[] args)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			ObjectNotExistsException {
		Method m;
		/**
		 * @arg Array that contains the method parameter types
		 */
		Class<?>[] arg = new Class[args.length - 2];
		m = _s.findMethod(theCForce, args[1], args.length - 2);
		int nParams = m.getTypeParameters().length;
		if (nParams == args.length - 2) {
			System.err.println("nParams: " + nParams);
			buildMethodParamTypeArray(arg, m);
		} else
			throw new NoSuchMethodException("Method named " + args[1]
					+ " with " + (args.length - 2)
					+ " not found \nTrying to call " + m.toString() + "?");

		/**
		 * @params Array that contains the parsed method parameters
		 */
		Object[] params = new Object[args.length - 2];
		buildMethodParamArray(args, arg, params);

		m.setAccessible(true);
		Object res = m.invoke(theForce, (Object[]) params);
		if (res != null) {
			System.err.println(res);
			_h.saveObject(res, "res");
		}
	}

	/**
	 * @param args
	 * @param arg
	 * @param params
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ObjectNotExistsException
	 */
	private void buildMethodParamArray(String[] args, Class<?>[] arg,
			Object[] params) throws InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ObjectNotExistsException {
		for (int i = 2; i < args.length; i++) {
			params[i - 2] = parseFromStr(args[i], arg[i - 2]);
		}
	}

	/**
	 * @param theCForce
	 * @param args
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void callNoArgs(Class<?> theCForce, String[] args)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Method m;
		m = theCForce.getDeclaredMethod(args[1]);
		m.setAccessible(true);
		Object res = m.invoke(theForce);
		if (res != null) {
			System.err.println(res);
			_h.saveObject(res, "res");
		}
	}

	private void buildMethodParamTypeArray(Class<?>[] arg, Method m) {
		int i = 0;
		for (Class<?> t : m.getParameterTypes()) {
			arg[i] = new TypeManager().box(t);
			i++;
		}
	}

	/**
	 * @param f
	 *            field that will be changed
	 * @param str
	 *            new value in a {@link String} form
	 */
	public void setVar(Field f, String str) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException,
			SecurityException, ObjectNotExistsException {
		if (f.getType().isPrimitive())
			f.set(theForce, parseFromStr(str, f.get(theForce).getClass()));
		else
			f.set(theForce, parseFromStr(str, f.getClass()));
	}

	@SuppressWarnings("unchecked")
	private <T> T parseFromStr(String s, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ObjectNotExistsException {
		if (s.startsWith("@"))
			return (T) _h.getSavedObject(s.substring(1));
		return clazz.getConstructor(new Class[] { String.class })
				.newInstance(s);
	}

	public void printField(Field f) throws IllegalArgumentException,
			IllegalAccessException {
		f.setAccessible(true);
		if (f.get(theForce) != null) {
			if (f.getModifiers() != 0) {
				System.err.println(Modifier.toString(f.getModifiers()) + " "
						+ f.getType().getCanonicalName() + " " + f.getName()
						+ " = " + f.get(theForce).toString());
			} else {
				System.err.println(f.getType().getCanonicalName() + " "
						+ f.getName() + " = " + f.get(theForce).toString());
			}
		} else if (f.getModifiers() != 0) {
			System.err.println(Modifier.toString(f.getModifiers()) + " "
					+ f.getType().getCanonicalName() + " " + f.getName()
					+ " = null");
		} else {
			System.err.println(f.getType().getCanonicalName() + " "
					+ f.getName() + " = null");
		}

	}

	public void printData(Object object, Boolean printName)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Class<?> cl = object.getClass();
		if (printName)
			System.err.println(object.toString() + " is an instance of class "
					+ cl.getCanonicalName());
		Class<?> sup = cl.getSuperclass();

		if (!(sup.getName().equals("java.lang.Object"))) {
			System.err.println("extends " + sup);
			printData(sup.newInstance(), false);
		} else
			System.err.println("----------");

		for (Field f : cl.getDeclaredFields()) {
			f.setAccessible(true);
			printField(f);
		}

	}

}
