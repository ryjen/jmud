/**
 * 
 */
package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.model.AbstractObject;

/**
 * @author Ryan
 * 
 */
@Entity
@DiscriminatorValue("key")
public class KeyObject extends AbstractObject {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#getValues()
	 */
	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#setValues(java.lang.Object[])
	 */
	@Override
	protected void setValues(java.lang.Object[] values) {
	}

}
