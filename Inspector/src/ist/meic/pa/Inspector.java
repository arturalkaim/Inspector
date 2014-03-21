package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Inspector {
	private boolean go = true;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	Object theForce;
	History _h = new History();

	public void inspect(Object object) {
		theForce = object;
		try {
			printData(object, true);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (go) {
			try {

				eval(read());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Object eval(String cmd) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("q")) {
			go = false;
		} else {
			exCommand(cmd);
		}
		return cmd;
	}

	private void exCommand(String cmd) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String[] line = cmd.split(" ");

		Class<?> cl = this.getClass();
		String mName = line[0] + "Command"; // ex: "i d" command calls iCommand

		Method m = cl.getDeclaredMethod(mName, new Class[] { String.class });
		m.setAccessible(true);
		m.invoke(this, new Object[] { cmd });
	}

	@SuppressWarnings("unused")
	private void bCommand(String cmd) throws Exception {
		eval(_h.back()); // Calls the last command
	}

	@SuppressWarnings("unused")
	private void lbCommand(String cmd) throws IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String[] args = cmd.split(" ");
		String aux;
		if (args.length > 2) {
			System.err.println("This command get any args");
			return;
		}
		int cout = 0;
		for (String c : _h.getLast(Integer.parseInt(args[1]))) {
			System.err.println("[" + cout + "] - " + c);
			cout++;
		}
		System.err.print(">");
		aux = in.readLine();
		int backTo = Integer.parseInt(aux);
		eval(_h.back(backTo));

	}

	@SuppressWarnings("unused")
	private void lmCommand(String cmd) {
		String[] args = cmd.split(" ");
		if (args.length > 1) {
			System.err.println("This command doesn't get any args");
			return;
		}

		for (Method m : theForce.getClass().getDeclaredMethods()) {
			System.err.println(m.toString());
		}

	}

	@SuppressWarnings("unused")
	private void hCommand(String cmd) {
		System.err.println();
		System.err.println("Inspector help");
		System.err.println();
		System.err.println("*********************************");
		System.err.println();
		System.err.println("h - to see this message");
		System.err.println("i <name> 				: Inspect the field <name>");
		System.err
				.println("m <name> <value> 		: Modifie the value of <name> with <value>");
		System.err
				.println("c <name> <arg1> <argn>	: Call method <name> with <arg..> as arguments");
		System.err.println("lm		 				: List methods of the inspected Object");
		System.err
				.println("b 						: select object within an array. The array should have been obtained as the result of the previous command.");
		System.err
				.println("lb <value>				: Go back to one command in the last <value> commands");
		System.err.println("exit or q - to exit");
		System.err.println();
		System.err.println("*********************************");
		System.err.println();
	}

	/**
	 * Call function with the name in {@cmd}
	 * 
	 * @param cmd
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unused")
	private void cCommand(String cmd) throws Exception {
		String[] args = cmd.split(" ");
		if (args.length < 2) {
			System.err
					.println("The use of this command is:  m <name> [<value> <value> <value> ...] ");
			return;
		}
		Class<?> theCForce = theForce.getClass();
		invokeMethod(theCForce, args);
	}

	private void invokeMethod(Class<?> theCForce, String[] args)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException, SecurityException {
		System.err.println("The force!");
		try {
			Method m;
			if (args.length == 2) {
				m = theCForce.getMethod(args[1]);
				m.setAccessible(true);
				m.invoke(theForce);
			} else if (args.length > 2) {
				/**
				 * @arg Array that contains the method parameter types
				 */
				Class<?>[] arg = new Class[args.length - 2];
				m = findMethod(theCForce, args[1]);
				buildMethodArray(arg, m);
				
				/**
				 * @params Array that contains the parsed method parameters
				 */
				Object[] params = new Object[args.length - 2];
				for (int i = 2; i < args.length; i++) {
					params[i - 2] = parseFromStr(args[i], arg[i - 2]);
				}

				m.setAccessible(true);
				Object res = m.invoke(theForce, (Object[]) params);
				if (res != null) {
					System.err.println(res);
				}
			}
		} catch (NoSuchMethodException e) {
			if (theCForce.getSuperclass().getName().equals("java.lang.Object")) {
				throw e;
			} else {
				invokeMethod(theCForce.getSuperclass(), args);
			}
		}
	}

	private void buildMethodArray(Class<?>[] arg, Method m) {
		int i = 0;
		for (Class<?> t : m.getParameterTypes()) {
			arg[i] = new TypeManager().box(t);
			System.err.println(arg[i].getCanonicalName());
			i++;
		}
	}

	private Method findMethod(Class<?> theCForce, String name)
			throws NoSuchMethodException {
		for (Method m : theCForce.getDeclaredMethods()) {
			if (m.getName().equals(name))
				return m;
		}
		throw new NoSuchMethodException("Method named: " + name + " not found!");
	}

	@SuppressWarnings("unused")
	private void mCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		String[] args = cmd.split(" ");
		if (args.length != 3) {
			System.err
					.println("The use of this command is:  m <name> <value> ");
			return;
		}
		Class<?> theCForce = theForce.getClass();

		Field f = findField(args[1], theCForce);
		setVar(f, args[2]);

		printData(theForce, true);
	}

	private Field findField(String name, Class<?> theCForce)
			throws NoSuchFieldException {

		for (Field f : theCForce.getDeclaredFields()) {
			if (f.getName().equals(name)) {
				f.setAccessible(true);
				return f;
			}
		}
		if (!theCForce.getSuperclass().getSimpleName().equals("Object")) {
			return findField(name, theCForce.getSuperclass());
		}
		throw new NoSuchFieldException("Field named: " + name + " not found!");
	}

	/**
	 * @param f
	 *            field that will be changed
	 * @param str
	 *            new value in a {@link String} form
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private void setVar(Field f, String str) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		f.set(theForce, parseFromStr(str, f.get(theForce).getClass()));
	}

	public <T> T parseFromStr(String s, Class<T> clazz)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return clazz.getConstructor(new Class[] { String.class })
				.newInstance(s);
	}

	@SuppressWarnings("unused")
	private void iCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			NoSuchFieldException {
		String[] args = cmd.split(" ");
		if (args.length != 2) {
			System.err.println("The use of this command is:  i <name>");
			return;
		}
		Class<?> theCForce = theForce.getClass();
		Field f = findField(args[1], theCForce);
		f.setAccessible(true);

		if (f.getType().isPrimitive()) {
			printField(f);
		} else {
			if (f.get(theForce) != null) {
				inspect(f.get(theForce));
			} else {
				printField(f);
			}
			// printData(theForce, true);
		}

	}

	private void printField(Field f) throws IllegalArgumentException,
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

	private String read() {
		String cmd = "";
		System.err.print(">");
		try {
			cmd = in.readLine();
		} catch (IOException e) {
			e.printStackTrace(); // XXX
		}

		_h.record(cmd);
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
