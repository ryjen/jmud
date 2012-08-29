package net.arg3.jmud.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Object;
import net.arg3.jmud.enums.DamType;

@Entity
@DiscriminatorValue("armor")
public class ArmorObject extends Object {

	private static final long serialVersionUID = 1L;
	Map<DamType, Integer> resistances;

	public ArmorObject() {
		resistances = new HashMap<DamType, Integer>();
	}

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		ArrayList<java.lang.Object> values = new ArrayList<java.lang.Object>();

		for (Map.Entry<DamType, Integer> entry : resistances.entrySet()) {
			values.add(entry.getKey());
			values.add(entry.getValue());
		}
		return values.toArray();
	}

	@Transient
	public int getResistance(DamType type) {
		return resistances.get(type);
	}

	public void setResistance(DamType type, int value) {
		resistances.put(type, value);
	}

	@Override
	@Transient
	protected void setValues(java.lang.Object[] values) {
		assert (values.length % 2 == 0);

		int i = 0;

		while (i < values.length) {
			DamType type = (DamType) values[i++];
			int value = (Integer) values[i++];

			resistances.put(type, value);
		}
	}

}
