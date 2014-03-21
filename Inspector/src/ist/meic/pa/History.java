package ist.meic.pa;

import java.util.Hashtable;

public class History {
	private Hashtable<Integer, String> _history = new Hashtable<Integer, String>();
	private static Integer id = 0;

	public History(){
		id=0;
	}
	
	public void record(String cmd) {
		_history.put(id++, cmd);
	}

	public String back() {
		id--;
		return _history.get(id - 1);
	}
	public String back(int n) {
		id-=n;
		return _history.get(id - 1);
	}
	public String getLast() {
		return _history.get(id - 1);
	}

	public String[] getLast(int n) {
		id--;
		if(n==0)
			n=3;
		if(n>id)
			n=id;
		String[] cmds = new String[n];

		for (int i = id - n; i < id; i++) {
			cmds[id-i-1] = _history.get(i);
		}

		return cmds;
	}

}
