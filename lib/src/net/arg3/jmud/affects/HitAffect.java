package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("hit")
public class HitAffect extends Affect {

	private static final long serialVersionUID = 1L;

	public HitAffect() {
	}

	public HitAffect(int mod) {
		setModifier(mod);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(HitAffect.class).error(
					"affect not on character - ", to.getClass().getName());
			return;
		}

		super.apply(to, silent);

		((Character) to).getVitals().adjustHit(getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(HitAffect.class).error(
					"affect not on character - ", to.getClass().getName());
			return;
		}

		super.apply(to, silent);

		((Character) to).getVitals().adjustHit(getModifier());
	}
}
