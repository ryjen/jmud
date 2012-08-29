/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public abstract class AbstractDefaultView extends JPanel {

	private static final long serialVersionUID = 1L;
	protected Class<?> type;

	public AbstractDefaultView(Class<?> type) {
		this.type = type;
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractDefaultView)) {
			return false;
		}
		AbstractDefaultView other = (AbstractDefaultView) obj;
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public abstract void refresh(java.lang.Object obj);

	public abstract void save(java.lang.Object obj);

	@Override
	public String toString() {
		return type.getSimpleName();
	}
}
