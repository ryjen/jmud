/**
 * @author A00651678, Ryan Jennings
 * @version Sep 26, 2009
 */
package net.arg3.jmud.editors;

import java.util.Map;

import net.arg3.jmud.view.AbstractCollectionView;
import net.arg3.jmud.view.MapView;

/**
 * @author Ryan Jennings
 * @version Sep 26, 2009
 * 
 */
public class MapEditor extends AbstractCollectionEditor {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	Map data;
	String key;

	public MapEditor(String key) {
		this.key = key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return data;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setValue(Object obj) {
		data = (Map) obj;
		setEditor(data);
	}

	@Override
	protected AbstractCollectionView createEditorPanel() {
		return new MapView(key);
	}
}
