package ist.meic.pa;

import ist.meic.pa.exceptions.NoSuchCommandException;
import ist.meic.pa.exceptions.ObjectNotExistsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Inspector {
	private boolean go = true;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	private Object theForce;
	private History _h = new History();
	private Search _s = new Search();
	private InspectorGadgets _ig;

	public void inspect(Object object) {
		try {
			_ig = new InspectorGadgets(_s, _h, object);
			theForce = object;
			_h.recordObj(object);
			_ig.printData(object, true);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (NullPointerException npe) {
			if (object == null) {
				System.err.println("The inspect argument can NOT be null!");
				System.out.println();
				go = false;
				return;
			} else
				npe.printStackTrace();
		}

		while (go) {
			try {

				eval(read());
			} catch (ObjectNotExistsException e) {
				System.err.println(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
			} catch (NoSuchCommandException e) {
				System.err.println(e.getMessage());
			} catch (NoSuchFieldException e) {
				System.err.println(e.getMessage());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
				go = false;
			}
		}

	}

	private Object eval(String cmd) throws Throwable {
		if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("q")) {
			go = false;
		} else {
			exCommand(cmd);
		}
		return cmd;
	}

	private String read() {
		String cmd = "";
		System.err.print(">");
		try {
			cmd = in.readLine();
		} catch (IOException e) {
			e.printStackTrace(); // XXX
		}

		_h.recordCmd(cmd);
		return cmd;

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
				.println("m <name> <value> 		: Modify the value of <name> with <value>");
		System.err
				.println("c <name> <arg1> <argn>	: Call method <name> with <arg..> as arguments");
		System.err.println("lm		 				: List methods of the inspected Object");
		System.err.println("b 						: Repete ");
		System.err
				.println("lc <value>				: Go back to one command in the last <value> commands");
		System.err
				.println("bo <value> 				: Go back in history of inspected Objects [default max: 10 objects]");
		System.err
				.println("so <name> 				: Save the current inspected Object with <name>");
		System.err.println("exit or q - to exit");
		System.err.println();
		System.err.println("*********************************");
		System.err.println();
	}

	private void exCommand(String cmd) throws Throwable {
		String[] line = cmd.split(" ");

		Class<?> cl = this.getClass();
		String mName = line[0] + "Command"; // ex: "i d" command calls iCommand
		if (line[0].length() > 2) {
			throw new NoSuchCommandException(line[0]);
		}
		try {
			Method m = cl
					.getDeclaredMethod(mName, new Class[] { String.class });
			m.setAccessible(true);
			m.invoke(this, new Object[] { cmd });
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (NoSuchMethodException e) {
			throw new NoSuchCommandException(line[0]);
		}
	}

	@SuppressWarnings("unused")
	private void rCommand(String cmd) throws Throwable {
		eval(_h.back()); // Calls the last command
	}

	@SuppressWarnings("unused")
	private void bCommand(String cmd) throws Throwable {
		inspect(_h.getObject(0)); // Inspects the last Inspected Object
	}

	@SuppressWarnings("unused")
	private void lcCommand(String cmd) throws Throwable {
		String[] args = cmd.split(" ");
		String aux;
		if (args.length > 2) {
			System.err.println("This command gets one arg");
			return;
		}
		int cout = 0;
		for (String c : _h.getLast(Integer.parseInt(args[1]))) {
			System.err.println("[" + cout + "] - " + c);
			cout++;
		}
		System.err.print("Insert the id of the command to go back>");
		aux = in.readLine();
		int backTo = Integer.parseInt(aux);
		eval(_h.back(backTo));

	}

	@SuppressWarnings("unused")
	private void soCommand(String cmd) throws IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String[] args = cmd.split(" ");
		if (args.length != 2) {
			System.err.println("This command gets one argument");
			return;
		}

		_h.saveObject(theForce, args[1]);

	}

	@SuppressWarnings("unused")
	private void boCommand(String cmd) throws Throwable {
		String[] args = cmd.split(" ");
		String aux;
		int nObjects = 10;
		if (args.length > 2) {
			System.err.println("This command gets one optional argument");
			return;
		}
		if (args.length == 2)
			nObjects = Integer.parseInt(args[1]);

		int cout = 0;
		for (Object c : _h.getLastNObjects(nObjects)) {
			System.err.println("[" + cout + "] - " + c);
			cout++;
		}
		System.err.print("insert the id of the object to inspect>");
		aux = in.readLine();
		int backTo = 0;
		try {
			backTo = Integer.parseInt(aux);
		} catch (NumberFormatException e) {
			return;
		}

		inspect(_h.getObject(backTo));

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
		theCForce = _ig.goUpPlus(args, theCForce);

		_ig.invokeMethod(theCForce, args);
	}

	@SuppressWarnings("unused")
	private void mCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ObjectNotExistsException {
		String[] args = cmd.split(" ");
		if (args.length != 3) {
			System.err
					.println("The use of this command is:  m <name> <value> ");
			return;
		}
		Class<?> theCForce = theForce.getClass();
		theCForce = _ig.goUpPlus(args, theCForce);

		Field f = _s.findField(args[1], theCForce);
		_ig.setVar(f, args[2]);

		_ig.printData(theForce, true);
	}

	@SuppressWarnings("unused")
	private void iCommand(String cmd) throws Throwable {
		String[] args = cmd.split(" ");
		if (args.length != 2) {
			System.err.println("The use of this command is:  i <name>");
			return;
		}
		Class<?> theCForce = theForce.getClass();
		theCForce = _ig.goUpPlus(args, theCForce);

		Field f = _s.findField(args[1], theCForce);
		f.setAccessible(true);

		if (f.getType().isPrimitive() || f.get(theForce) == null) {
			_ig.printField(f);
		} else {
			inspect(f.get(theForce));
		}

	}

}
