package net.arg3.jmud;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;

import net.arg3.jmud.enums.DamType;

@Embeddable
@Access(AccessType.PROPERTY)
public class Attributes implements Cloneable {

	private Map<DamType, Integer> resistances;

	private Stats stats;

	public Attributes() {
		resistances = new HashMap<DamType, Integer>();
		stats = new Stats();
	}

	@Override
	public Attributes clone() throws CloneNotSupportedException {
		return (Attributes) super.clone();
	}

	public void adjustResistance(DamType type, int value) {
		if (!resistances.containsKey(type)) {
			resistances.put(type, value);
			return;
		}

		resistances.put(type, resistances.get(type) + value);
	}

	@Transient
	public int getResistance(DamType type) {
		return resistances.get(type);
	}

	@Embedded
	public Stats getStats() {
		return stats;
	}

	public void setResistance(DamType type, int value) {
		resistances.put(type, value);
	}

	@ElementCollection
	@CollectionTable(name = "char_resistance")
	@MapKeyColumn(name = "dam_type")
	@Column(name = "value")
	protected Map<DamType, Integer> getResistances() {
		return resistances;
	}

	protected void setResistances(Map<DamType, Integer> value) {
		resistances = value;
	}

	protected void setStats(Stats value) {
		stats = value;
	}
}
