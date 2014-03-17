package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;



public class Inspector {
	private boolean go = true;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	Object god;

	public void inspect(Object object) {
		try {
			god = object;
			printData(object, true);

			while (go) {
				eval(read());
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	private Object eval(String cmd) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			ReflectiveOperationException, RuntimeException {
		if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("q")) {
			go = false;
		} else if (cmd.startsWith("i ")) {
			InspectCommand(cmd);
		} else if (cmd.startsWith("m ")) {
			ModifieCommand(cmd);
		} else if (cmd.startsWith("c ")) {
			CallCommand(cmd);
		} else
			System.err.println(cmd);
		return cmd;
	}

	/**
	 * Call function with the name in {@cmd}
	 * @param cmd 
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void CallCommand(String cmd) throws SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		String[] args = cmd.split(" ");
		if (args.length < 2) {
			System.err
					.println("The use of this command is:  m <name> [<value> <value> <value> ...] ");
		}
		Class<?> godClass = god.getClass();
		try {
			invokeMethod(godClass, args);
		} catch (NoSuchMethodException e) {
			if (godClass.isInstance(Object.class)) {
				throw e;
			} else {
				invokeMethod(godClass.getSuperclass(), args);
			}
		}

	}

	private void invokeMethod(Class<?> godClass, String[] args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method m;
		if (args.length == 2) {

			m = godClass.getMethod(args[1]);

			m.setAccessible(true);
			m.invoke(god);

		} else if (args.length > 2) {
			Class<Integer>[] arg = new Class[args.length - 2];
			Arrays.fill(arg, Integer.TYPE);
			m = godClass.getDeclaredMethod(args[1], arg);
			m.setAccessible(true);
			Integer[] params = new Integer[args.length - 2];
			for (int i = 2; i < args.length; i++) {
				params[i - 2] = Integer.parseInt(args[i]);
			}
			Object res = m.invoke(god, (Object[]) params);
			if (res != null) {
				System.err.println(res.toString());
			}
		}
	}

	private void ModifieCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException, ReflectiveOperationException {
		String[] args = cmd.split(" ");
		if (args.length != 3) {
			System.err
					.println("The use of this command is:  m <name> <value> ");
		}
		Class<?> godClass = god.getClass();

		Field f = findField(args[1], godClass);

		setVar(f, args[2]);

		printField(f);
	}

	private Field findField(String name, Class<?> godClass)
			throws NoSuchFieldException {
		if (!godClass.getSuperclass().getSimpleName().equals("Object"))
			return findField(name, godClass.getSuperclass());

		for (Field f : godClass.getSuperclass().getDeclaredFields()) {
			if (f.getName().equals(name)) {
				f.setAccessible(true);
				return f;
			}
		}

		throw new NoSuchFieldException("Field " + name + " not found!");
	}

	/**
	 * @param f field that will be changed
	 * @param str new value in a {@link String} form
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void setVar(Field f, String str) throws IllegalArgumentException,
			IllegalAccessException {
		System.err.println(f.getType().getSimpleName());

		if (f.getType().getSimpleName().equals("int")
				|| f.getType().getSimpleName().equals("Integer")) {
			f.setInt(god, Integer.parseInt(str));
		} else if (f.getType().getSimpleName().equalsIgnoreCase("Float")) {
			f.setFloat(god, Float.parseFloat(str));
		} else if (f.getType().getSimpleName().equalsIgnoreCase("Boolean")) {
			f.setBoolean(god, Boolean.parseBoolean(str));
		} else if (f.getType().getSimpleName().equalsIgnoreCase("Double")) {
			f.setDouble(god, Double.parseDouble(str));
		} else if (f.getType().getSimpleName().equals("String")) {
			f.set(god, str);
		} else if (f.getType().getSimpleName().equalsIgnoreCase("Long")) {
			f.setLong(god, Long.parseLong(str));
		} else
			System.err.println("Type of input nos suported to modifie variable");
	}

	private void InspectCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		String[] args = cmd.split(" ");
		if (args.length != 2) {
			System.err.println("The use of this command is:  i <name>");
		}
		Class<?> godClass = god.getClass();

		for (Field f : godClass.getDeclaredFields()) {
			if (f.getName().equals(args[1])) {
				f.setAccessible(true);
				printField(f);
				new ist.meic.pa.Inspector().inspect(f.get(god));
				printData(god, true);
			}
		}

	}

	private void printField(Field f) throws IllegalArgumentException,
			IllegalAccessException {
		f.setAccessible(true);
		if (f.get(god) != null) {
			if (f.getModifiers() != 0) {
				System.err.println(Modifier.toString(f.getModifiers()) + " "
						+ f.getType().getCanonicalName() + " " + f.getName()
						+ " = " + f.get(god).toString());
			} else {
				System.err.println(f.getType().getCanonicalName() + " "
						+ f.getName() + " = " + f.get(god).toString());
			}
		} else if (f.getModifiers() != 0) {
			System.err.println(Modifier.toString(f.getModifiers()) + " "
					+ f.getType().getCanonicalName() + " " + f.getName());
		} else {
			System.err.println(f.getType().getCanonicalName() + " "
					+ f.getName());
		}

	}

	private String read() {
		String cmd = "";
		System.err.print(">");
		try {
			cmd = in.readLine();
		} catch (IOException e) {
			e.printStackTrace(); //XXX
		}

		return cmd;

	}

	private void printData(Object object, Boolean printName)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		Class<?> cl = object.getClass();
		if (printName)
			System.err.println(object.toString() + " is an instance of class "
					+ cl.getCanonicalName());
		Class<?> sup = cl.getSuperclass();

		if (!(sup.isInstance(new Object()))) {
			// System.err.println("extends " + sup);
			printData(sup.newInstance(), false);
		} else
			System.err.println("----------");

		for (Field f : cl.getDeclaredFields()) {
			f.setAccessible(true);
			printField(f);
		}

	}

}
