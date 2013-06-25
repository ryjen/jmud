package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("mana")
public class ManaAffect extends Affect {
	private static final long serialVersionUID = 1L;

	public ManaAffect() {
	}

	public ManaAffect(int modifier) {
		setModifier(modifier);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}
		super.apply(to, silent);

		((Character) to).getVitals().adjustMana(getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}

		super.undo(to, silent);

		((Character) to).getVitals().adjustMana(-(getModifier()));
	}
}
