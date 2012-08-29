/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public abstract class AbstractEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	public AbstractEditor() {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder());
	}

	public abstract Object getValue();

	public abstract void setReadOnly(boolean value);

	public abstract void setValue(Object value);
}
