package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("damroll")
public class DamrollAffect extends Affect {

	private static final long serialVersionUID = 1L;

	public DamrollAffect() {
	}

	public DamrollAffect(int mod) {
		setModifier(mod);
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(DamrollAffect.class).error(
					"affect not on character: ", to.getClass().getName());
			return;
		}

		((Character) to).setDamroll(((Character) to).getDamroll()
				+ getModifier());
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		if (!(to instanceof Character)) {
			LoggerFactory.getLogger(DamrollAffect.class).error(
					"affect not on character: ", to.getClass().getName());
			return;
		}
	}
}
