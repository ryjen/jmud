package net.arg3.jmud.races;

import javax.persistence.Entity;

import net.arg3.jmud.Race;
import net.arg3.jmud.enums.Size;

@Entity
public class KoboldRace extends Race {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "kobold";
	}

	@Override
	public Size getSize() {
		return Size.Medium;
	}

}
