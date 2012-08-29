/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import javax.swing.JTextField;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class StringEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;
	JTextField textField;

	public StringEditor() {
		textField = new JTextField();
		add(textField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return textField.getText();
	}

	@Override
	public void setReadOnly(boolean value) {
		textField.setEditable(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		textField.setText(value == null ? "" : value.toString());
	}
}
