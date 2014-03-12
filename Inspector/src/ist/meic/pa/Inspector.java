package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Inspector {
	private boolean go = true;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	Object god;

	public void inspect(Object object) {
		try {
			god = object;
			printData(object);

			while (go) {
				print(eval(read()));
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Object eval(String cmd) throws IllegalArgumentException,
			IllegalAccessException {
		if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("q")) {
			go = false;
		} else if (cmd.startsWith("i ")) {
			InspectCommand(cmd);
		} else
			System.err.println(cmd);
		return cmd;
	}

	private void InspectCommand(String cmd) throws IllegalArgumentException,
			IllegalAccessException {
		String[] args = cmd.split(" ");

		Class<?> godClass = god.getClass();

		for (Field f : godClass.getDeclaredFields()) {
			if (f.getName().equals(args[1])) {
				f.setAccessible(true);
				System.err.println(Modifier.toString(f.getModifiers()) + " "
						+ f.getType().getCanonicalName() + " " + f.getName()
						+ " = " + f.get(god).toString());
				new ist.meic.pa.Inspector().inspect(f.get(god));
			}
		}

	}

	private void print(Object eval) {

	}

	private String read() {
		String cmd = "";
		System.err.print(">");
		try {

			cmd = in.readLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cmd;

	}

	private void printData(Object object) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		Class<?> cl = object.getClass();
		System.err.println(object.toString() + " is an instance of class "
				+ cl.getCanonicalName());
		Class<?> sup = cl.getSuperclass();

		if (!(sup.isInstance(new Object()))) {
			System.err.println("extends " + sup);
			System.err.println("----------");

			for (Field f : sup.getDeclaredFields()) {
				f.setAccessible(true);
				System.err.println(Modifier.toString(f.getModifiers()) + " "
						+ f.getType().getCanonicalName() + " " + f.getName()
						+ " = " + f.get(object).toString());
			}

		} else
			System.err.println("----------");

		for (Field f : cl.getDeclaredFields()) {
			f.setAccessible(true);
			System.err.println(Modifier.toString(f.getModifiers()) + " "
					+ f.getType().getCanonicalName() + " " + f.getName()
					+ " = " + f.get(object).toString());
		}
	}
}
