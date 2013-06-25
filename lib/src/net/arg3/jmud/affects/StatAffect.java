package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.enums.StatType;
import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("stat")
public class StatAffect extends Affect {

	private static final long serialVersionUID = 1L;

	public StatAffect() {
	}

	public StatAffect(StatType type, int mod) {
		setExtra(type.ordinal());
		setModifier(mod);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(StatAffect.class).error(
					"affect not on character: ", to.getClass().getName());
			return;
		}
		super.apply(to, silent);

		StatType type = StatType.values()[getExtra()];

		((Character) to).getStats().adjustStat(type, getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(StatAffect.class).error(
					"affect not on character: ", to.getClass().getName());
			return;
		}
		super.undo(to, silent);

		StatType type = StatType.values()[getExtra()];

		((Character) to).getStats().adjustStat(type, -(getModifier()));
	}
}
