package net.arg3.jmud.objects;

import javax.persistence.Transient;

import net.arg3.jmud.model.AbstractObject;
import net.arg3.jmud.model.Spell;

public class WandStaffObject extends AbstractObject {

	protected static final long serialVersionUID = 1L;
	int spellLevel;
	int chargesTotal;
	int chargesRemaining;
	int spell;

	@Transient
	public int getSpellLevel() {

		return spellLevel;
	}

	@Transient
	public int getChargesTotal() {

		return chargesTotal;
	}

	@Transient
	public int getChargesRemaining() {

		return chargesRemaining;
	}

	@Transient
	public Spell getSpell() {
		return Spell.lookup(spell);
	}

	public void setSpellLevel(int value) {
		spellLevel = value;
	}

	public void setChargesTotal(int value) {
		chargesTotal = value;
	}

	public void setChargesRemaining(int value) {
		chargesRemaining = value;
	}

	public void setSpell(Spell value) {
		if (value != null)
			spell = value.getId();
	}

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { spellLevel, chargesTotal,
				chargesRemaining, spell };
	}

	@Override
	protected void setValues(java.lang.Object[] values) {
		assert (values.length == 4);

		spellLevel = (Integer) values[0];
		chargesTotal = (Integer) values[1];
		chargesRemaining = (Integer) values[2];
		spell = (Integer) values[3];
	}
}
