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
public class LongEditor extends NumberEditor {

	private static final long serialVersionUID = 1L;

	public LongEditor() {
		super(new SpinnerNumberModel(0, Long.MIN_VALUE, Long.MAX_VALUE, 1));
	}
}
