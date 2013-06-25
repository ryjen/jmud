package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("hitroll")
public class HitrollAffect extends Affect {

	private static final long serialVersionUID = 1L;

	public HitrollAffect() {
	}

	public HitrollAffect(int mod) {
		setModifier(mod);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(HitrollAffect.class).error(
					"affect not on a character - ", to.getClass().getName());
			return;
		}

		super.apply(to, silent);

		((Character) to).setHitroll(((Character) to).getHitroll()
				+ getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(HitrollAffect.class).error(
					"affect not on a character - ", to.getClass().getName());
			return;
		}

		super.apply(to, silent);

		((Character) to).setHitroll(((Character) to).getHitroll()
				- getModifier());
	}
}
