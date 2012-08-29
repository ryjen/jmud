package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Character;
import net.arg3.jmud.interfaces.IAffectable;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("level")
public class LevelAffect extends Affect {

	private static final long serialVersionUID = 1L;

	public LevelAffect() {
	}

	public LevelAffect(int mod) {
		setModifier(mod);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}

		super.apply(to, silent);

		((Character) to).setLevel(((Character) to).getLevel() + getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}

		super.undo(to, silent);

		((Character) to).setLevel(((Character) to).getLevel() - getModifier());
	}
}
