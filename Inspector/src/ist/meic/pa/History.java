package ist.meic.pa;

import ist.meic.pa.exceptions.ObjectNotExistsException;

import java.util.Hashtable;

public class History {
	private Hashtable<Integer, String> _CmdHistory = new Hashtable<Integer, String>();
	private static Hashtable<Integer, Object> _ObjHistory = new Hashtable<Integer, Object>();
	private static Hashtable<String, Object> _ObjVault = new Hashtable<String, Object>();
	private static Integer id;
	private static Integer objID;

	public History() {
		id = 0;
		objID = 0;
	}

	public void recordCmd(String cmd) {
		_CmdHistory.put(id++, cmd);
	}

	public String back() {
		id--;
		return _CmdHistory.get(id - 1);
	}

	public String back(int n) {
		id -= n;
		return _CmdHistory.get(id - 1);
	}

	public String getLast() {
		return _CmdHistory.get(id - 1);
	}

	public String[] getLast(int n) {
		id--;
		if (n == 0)
			n = 3;
		if (n > id)
			n = id;
		String[] cmds = new String[n];

		for (int i = id - n; i < id; i++) {
			cmds[id - i - 1] = _CmdHistory.get(i);
		}

		return cmds;
	}

	public void recordObj(Object object) {
		_ObjHistory.put(objID++, object);
	}

	public Object[] getLastNObjects(int i) {
		if (i == 0)
			i = 3;
		if (i > objID)
			i = objID-1;
		Object[] objs = new Object[i];

		for (int i1 = 0; i1 < i; i1++) {
			System.out.println(i + " " + i1);
			objs[i1] = _ObjHistory.get(objID - i1 - 2);
		}

		return objs;
	}

	public Object getObject(int n) {
		return _ObjHistory.get(objID - (n+1) - 1);
	}

	public void saveObject(Object object, String string) {
		_ObjVault.put(string, object);
	}

	public Object getSavedObject(String s) throws ObjectNotExistsException {
		Object obj = _ObjVault.get(s);
		if (obj == null) {
			throw new ObjectNotExistsException(s);
		}
		return obj;
	}
}
