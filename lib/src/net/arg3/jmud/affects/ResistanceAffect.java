package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Character;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.interfaces.IAffectable;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("resistance")
public class ResistanceAffect extends Affect {
	private static final long serialVersionUID = 1L;

	public ResistanceAffect() {
	}

	public ResistanceAffect(DamType type, int mod) {
		setModifier(mod);
		setExtra(type.ordinal());
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}
		super.apply(to, silent);

		DamType type = DamType.values()[getExtra()];

		((Character) to).adjustResistance(type, getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}
		super.apply(to, silent);

		DamType type = DamType.values()[getExtra()];

		((Character) to).adjustResistance(type, -(getModifier()));
	}
}
