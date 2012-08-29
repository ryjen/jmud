/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.arg3.jmud.editors;

import javax.swing.JTextArea;

/**
 * 
 * @author Ryan
 */
public class TextEditor extends AbstractEditor {
	private static final long serialVersionUID = 1L;
	JTextArea textArea;

	public TextEditor() {
		textArea = new JTextArea(10, 10);
		add(textArea);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return textArea.getText();
	}

	@Override
	public void setReadOnly(boolean value) {
		textArea.setEditable(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.BaseEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		textArea.setText(value == null ? "" : value.toString());
	}
}
