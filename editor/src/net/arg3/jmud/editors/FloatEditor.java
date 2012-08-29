/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import javax.swing.SpinnerNumberModel;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class FloatEditor extends NumberEditor {

	private static final long serialVersionUID = 1L;

	public FloatEditor() {
		super(new SpinnerNumberModel(Float.MIN_NORMAL, Float.MIN_VALUE,
				Float.MAX_VALUE, 0.1));
	}
}
