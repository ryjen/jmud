package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.enums.SpellAffects;
import net.arg3.jmud.model.Affect;

@Entity
@DiscriminatorValue("spell")
public class SpellAffect extends Affect {
	private static final long serialVersionUID = 1L;

	public SpellAffect() {
	}

	public SpellAffect(SpellAffects type) {
		setExtra(type.ordinal());
	}
}
