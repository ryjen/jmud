package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Object;
import net.arg3.jmud.annotations.FlagValue;

@Entity
@DiscriminatorValue("container")
public class ContainerObject extends Object {

	private static final long serialVersionUID = 1L;
	int maxWeight;
	int containerFlags;
	long key;

	@FlagValue
	public static final int CLOSEABLE = (1 << 0);
	@FlagValue
	public static final int PICKPROOF = (1 << 1);
	@FlagValue
	public static final int CLOSED = (1 << 2);
	@FlagValue
	public static final int LOCKED = (1 << 3);
	@FlagValue
	public static final int PUT_ON = (1 << 4);

	@Transient
	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	@Transient
	public int getContainerFlags() {
		return containerFlags;
	}

	public void setContainerFlags(int containerFlags) {
		this.containerFlags = containerFlags;
	}

	@Transient
	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { maxWeight, containerFlags, key };
	}

	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 3);

		maxWeight = (Integer) values[0];
		containerFlags = (Integer) values[1];
		key = (Long) values[2];
	}

}
