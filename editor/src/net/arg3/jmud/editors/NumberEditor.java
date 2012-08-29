/**
 * Project: jmudeditor
 * Date: 2009-09-25
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public abstract class NumberEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;
	JSpinner spinner;

	public NumberEditor(SpinnerModel model) {
		spinner = new JSpinner(model);
		JComponent e = spinner.getEditor();
		if (e instanceof DefaultEditor) {
			((DefaultEditor) e).getTextField().setHorizontalAlignment(
					SwingConstants.LEFT);
		}
		add(spinner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.AbstractEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return spinner.getValue();
	}

	@Override
	public void setReadOnly(boolean value) {
		spinner.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jennings.ryan.jMUD.editors.AbstractEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		spinner.setValue(value);
	}
}
