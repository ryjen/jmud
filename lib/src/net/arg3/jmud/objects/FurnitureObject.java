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
@DiscriminatorValue("furniture")
public class FurnitureObject extends AbstractObject {

	private static final long serialVersionUID = 1L;

	@FlagValue
	public static final int STAND_AT = (1 << 0);
	@FlagValue
	public static final int STAND_ON = (1 << 1);
	@FlagValue
	public static final int STAND_IN = (1 << 2);
	@FlagValue
	public static final int SIT_AT = (1 << 3);
	@FlagValue
	public static final int SIT_ON = (1 << 4);
	@FlagValue
	public static final int SIT_IN = (1 << 5);
	@FlagValue
	public static final int REST_AT = (1 << 6);
	@FlagValue
	public static final int REST_ON = (1 << 7);
	@FlagValue
	public static final int REST_IN = (1 << 8);
	@FlagValue
	public static final int SLEEP_AT = (1 << 9);
	@FlagValue
	public static final int SLEEP_ON = (1 << 10);
	@FlagValue
	public static final int SLEEP_IN = (1 << 11);
	@FlagValue
	public static final int PUT_AT = (1 << 12);
	@FlagValue
	public static final int PUT_ON = (1 << 13);
	@FlagValue
	public static final int PUT_IN = (1 << 14);
	@FlagValue
	public static final int PUT_INSIDE = (1 << 15);

	int healRate;
	int manaRate;
	int furnitureFlags;

	@Transient
	public int getHealRate() {
		return healRate;
	}

	public void setHealRate(int healRate) {
		this.healRate = healRate;
	}

	@Transient
	public int getManaRate() {
		return manaRate;
	}

	public void setManaRate(int manaRate) {
		this.manaRate = manaRate;
	}

	@Transient
	public int getFurnitureFlags() {
		return furnitureFlags;
	}

	public void setFurnitureFlags(int furnitureFlags) {
		this.furnitureFlags = furnitureFlags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#getValues()
	 */
	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { healRate, manaRate, furnitureFlags };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.Object#setValues(java.lang.Object[])
	 */
	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 3);

		healRate = (Integer) values[0];
		manaRate = (Integer) values[1];
		furnitureFlags = (Integer) values[2];
	}

}
