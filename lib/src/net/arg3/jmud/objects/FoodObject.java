package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Object;

@Entity
@DiscriminatorValue("food")
public class FoodObject extends Object {

	private static final long serialVersionUID = 1L;
	int fillValue;
	boolean poisoned;

	@Transient
	public int getFillValue() {
		return fillValue;
	}

	public void setFillValue(int fillValue) {
		this.fillValue = fillValue;
	}

	@Transient
	public boolean isPoisoned() {
		return poisoned;
	}

	public void setPoisoned(boolean poisoned) {
		this.poisoned = poisoned;
	}

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { fillValue, poisoned };
	}

	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 2);

		fillValue = (Integer) values[0];
		poisoned = (Boolean) values[1];
	}

}
