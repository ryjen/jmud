/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import java.util.Collection;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.view.AbstractCollectionView;
import net.arg3.jmud.view.CollectionView;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class CollectionEditor extends AbstractCollectionEditor {

	private static final long serialVersionUID = 1L;
	Collection<IDataObject<?>> data;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return data;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object obj) {
		data = (Collection<IDataObject<?>>) obj;
		setEditor(data);
	}

	@Override
	protected AbstractCollectionView createEditorPanel() {
		return new CollectionView();
	}
}
