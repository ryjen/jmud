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
public class DoubleEditor extends NumberEditor {

	private static final long serialVersionUID = 1L;

	public DoubleEditor() {
		super(new SpinnerNumberModel(Double.MIN_NORMAL, Double.MIN_VALUE,
				Double.MAX_VALUE, 0.1));
	}
}
