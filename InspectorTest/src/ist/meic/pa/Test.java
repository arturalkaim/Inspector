package ist.meic.pa;

public class Test {

	public static void main(String[] args) {
		System.out.println("Bom dia");
		new ist.meic.pa.Inspector().inspect(new B());
		System.out.println("Olá");

		new ist.meic.pa.Inspector().inspect(new E());
		
		System.out.println("Comé");

		new ist.meic.pa.Inspector().inspect(new G());
		
		System.out.println("Bye Bye");

	}

}
