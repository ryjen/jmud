package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Object;

@Entity
@DiscriminatorValue("treasure")
public class TreasureObject extends Object {

	private static final long serialVersionUID = -7762286726259325574L;

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return null;
	}

	@Override
	protected void setValues(java.lang.Object[] values) {
	}

}
