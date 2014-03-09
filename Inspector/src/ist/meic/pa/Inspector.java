package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Inspector {
	private boolean go = true;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public void inspect(Object object) {
		try {

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

	private Object eval(String cmd) {
		if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("q")) {
			go = false;
		}
		System.err.println(cmd);
		return cmd;
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
