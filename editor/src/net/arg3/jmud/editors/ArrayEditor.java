/**
 * 
 */
package net.arg3.jmud.editors;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.view.AbstractCollectionView;
import net.arg3.jmud.view.ArrayView;

/**
 * @author Ryan
 * 
 */
public class ArrayEditor extends AbstractCollectionEditor {

	private static final long serialVersionUID = 1L;
	IDataObject<?>[] data;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.editors.AbstractCollectionEditor#createEditorPanel()
	 */
	@Override
	protected AbstractCollectionView createEditorPanel() {
		return new ArrayView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.editors.AbstractEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.editors.AbstractEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		data = (IDataObject<?>[]) value;
	}

}
