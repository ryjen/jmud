/**
 * Project: jmudeditor
 * Date: 2009-09-28
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class EnumEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;
	JComboBox combo;

	public EnumEditor() {
		combo = new JComboBox();

		add(combo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.AbstractEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return combo.getSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.AbstractEditor#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean value) {
		combo.setEditable(value);
	}

	public void setType(Class<?> type) {
		combo.setModel(new DefaultComboBoxModel(type.getEnumConstants()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jennings.ryan.jMUD.editors.AbstractEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object obj) {
		combo.setSelectedItem(obj);
	}
}
