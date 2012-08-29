package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Object;

@Entity
@DiscriminatorValue("liquid_container")
public class LiquidContainerObject extends Object {

	private static final long serialVersionUID = 1L;
	int liquidTotal;
	int liquidLeft;
	boolean poisoned;
	String liquid;

	@Transient
	public int getLiquidTotal() {
		return liquidTotal;
	}

	public void setLiquidTotal(int liquidTotal) {
		this.liquidTotal = liquidTotal;
	}

	@Transient
	public int getLiquidLeft() {
		return liquidLeft;
	}

	public void setLiquidLeft(int liquidLeft) {
		this.liquidLeft = liquidLeft;
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
		return new java.lang.Object[] { liquidTotal, liquidLeft, liquid,
				poisoned };
	}

	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 4);

		liquidTotal = (Integer) values[0];
		liquidLeft = (Integer) values[1];
		liquid = (String) values[2];
		poisoned = (Boolean) values[3];
	}

	public String getLiquid() {
		return liquid;
	}

	public void setLiquid(String liquid) {
		this.liquid = liquid;
	}

}
