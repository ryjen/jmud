/**
 * Project: jmudlib
 * Date: 2009-09-24
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import javax.swing.SpinnerNumberModel;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class IntegerEditor extends NumberEditor {

	private static final long serialVersionUID = 1L;

	public IntegerEditor() {
		super(
				new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE,
						1));
	}
}
