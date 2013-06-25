package net.arg3.jmud.affects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Character;

@Entity
@DiscriminatorValue("sex")
public class SexAffect extends Affect {

	private static final long serialVersionUID = 1L;

	Sex oldSex;

	public SexAffect() {
	}

	public SexAffect(Sex mod) {
		setModifier(mod.ordinal());
	}

	@Override
	public void apply(IAffectable to, boolean silent) {
		super.apply(to, silent);
		oldSex = ((Character) to).getSex();
		Sex newSex = Sex.values()[getModifier()];
		((Character) to).setSex(newSex);
	}

	@Override
	public void undo(IAffectable to, boolean silent) {
		super.undo(to, silent);

		((Character) to).setSex(oldSex);
	}
}
