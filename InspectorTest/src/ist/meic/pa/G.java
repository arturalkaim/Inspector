package ist.meic.pa;

public class G extends E {
	private int i;
	
	G(int i){
		this.i = i;
	}
	G(){
		this.i = 0;
	}
	void holla(){
		System.out.println("holla " +i);
		i++;
	}
}
