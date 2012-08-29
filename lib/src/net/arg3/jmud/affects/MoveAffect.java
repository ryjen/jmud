package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Character;
import net.arg3.jmud.interfaces.IAffectable;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("move")
public class MoveAffect extends Affect {
	private static final long serialVersionUID = 1L;

	public MoveAffect() {
	}

	public MoveAffect(int mod) {
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

		((Character) to).getVitals().adjustMove(getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(LevelAffect.class).error(
					"affect not on a character: ", to.getClass().getName());
			return;
		}
		super.apply(to, silent);

		((Character) to).getVitals().adjustMove(-(getModifier()));
	}
}
