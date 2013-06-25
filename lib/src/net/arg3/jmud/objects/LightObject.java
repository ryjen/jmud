package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.model.AbstractObject;

@Entity
@DiscriminatorValue("light")
public class LightObject extends AbstractObject {

	private static final long serialVersionUID = 1L;
	int lightRemaining;
	int lightTotal;

	public LightObject() {
	}

	@Transient
	public int getLightRemaining() {
		return lightRemaining;
	}

	public void setLightRemaining(int lightRemaining) {
		this.lightRemaining = lightRemaining;
	}

	@Transient
	public int getLightTotal() {
		return lightTotal;
	}

	public void setLightTotal(int lightTotal) {
		this.lightTotal = lightTotal;
	}

	@Override
	@Transient
	public java.lang.Object[] getValues() {
		return new java.lang.Object[] { lightRemaining, lightTotal };
	}

	@Override
	public void setValues(java.lang.Object[] values) {
		assert (values.length == 2);

		lightRemaining = (Integer) values[0];

		lightTotal = (Integer) values[1];
	}
}
