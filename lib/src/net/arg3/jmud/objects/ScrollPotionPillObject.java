package net.arg3.jmud.objects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import net.arg3.jmud.model.AbstractObject;
import net.arg3.jmud.model.Spell;

public abstract class ScrollPotionPillObject extends AbstractObject {

	protected static final long serialVersionUID = 1L;

	int spellLevel;

	List<Integer> spells;

	public ScrollPotionPillObject() {
		spells = new ArrayList<Integer>();
	}

	@Transient
	public int getSpellLevel() {

		return spellLevel;
	}

	@Transient
	public List<Integer> getSpells() {
		return spells;
	}

	public void setSpellLevel(int value) {
		spellLevel = value;
	}

	public void addSpell(Spell value) {
		if (value != null)
			spells.add(value.getId());
	}

	public void removeSpell(int index) {
		spells.remove(index);
	}

	public void removeSpell(Spell value) {
		if (value != null)
			spells.remove(value.getId());
	}

	@Override
	@Transient
	public java.lang.Object[] getValues() {
		ArrayList<java.lang.Object> values = new ArrayList<java.lang.Object>();
		values.add(spellLevel);
		values.addAll(spells);

		return values.toArray();
	}

	@Override
	public void setValues(java.lang.Object[] values) {
		assert (values.length > 0);

		spellLevel = (Integer) values[0];

		for (int i = 1; i < values.length; i++)
			spells.add((Integer) values[i]);
	}
}
