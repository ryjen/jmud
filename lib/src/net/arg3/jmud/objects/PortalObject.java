/**
 * 
 */
package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.model.AbstractObject;

/**
 * @author Ryan
 * 
 */
@Entity
@DiscriminatorValue("portal")
public class PortalObject extends AbstractObject {

	private static final long serialVersionUID = 1L;

	@FlagValue
	public static final int NORMAL = (1 << 0);
	@FlagValue
	public static final int NOCURSE = (1 << 1);
	@FlagValue
	public static final int GOWITH = (1 << 2);
	@FlagValue
	public static final int BUGGY = (1 << 3);
	@FlagValue
	public static final int RANDOM = (1 << 4);

	int charges;
	int exitFlags;
	long key;
	int gateFlags;
	long location;

	@Transient
	public int getCharges() {
		return charges;
	}

	public void setCharges(int charges) {
		this.charges = charges;
	}

	@Transient
	public long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
	}

	@Transient
	public int getGateFlags() {
		return gateFlags;
	}

	public void setGateFlags(int gateFlags) {
		this.gateFlags = gateFlags;
	}

	@Transient
	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	@Transient
	public int getExitFlags() {
		return exitFlags;
	}

	public void setExitFlags(int exitFlags) {
		this.exitFlags = exitFlags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#getValues()
	 */
	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { charges, exitFlags, gateFlags,
				location, key };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#setValues(java.lang.Object[])
	 */
	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 5);

		charges = (Integer) values[0];
		exitFlags = (Integer) values[1];
		gateFlags = (Integer) values[2];
		location = (Long) values[3];
		key = (Long) values[4];
	}
}
