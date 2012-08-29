package net.arg3.jmud.view;

import java.lang.reflect.Method;
import java.util.Map;

public class MapView extends AbstractCollectionView {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	Map data;
	Method keyGetter;
	String key;

	public MapView(String key) {
		this.key = key;
	}

	private java.lang.Object getKey(java.lang.Object obj) {
		try {
			Method m = obj.getClass().getMethod(
					"get" + java.lang.Character.toUpperCase(key.charAt(0))
							+ key.substring(1));
			return m.invoke(obj);
		} catch (Exception e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void add(java.lang.Object obj) {
		data.put(getKey(obj), obj);
	}

	@Override
	protected java.lang.Object[] getValues() {
		return data == null ? null : data.values().toArray();
	}

	@Override
	protected void remove(java.lang.Object obj) {
		data.remove(getKey(obj));
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void setData(java.lang.Object obj) {
		data = (Map) obj;
	}
}
