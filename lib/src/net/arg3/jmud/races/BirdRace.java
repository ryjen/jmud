package net.arg3.jmud.races;

import javax.persistence.Entity;

import net.arg3.jmud.enums.Size;
import net.arg3.jmud.model.Race;

@Entity
public class BirdRace extends Race {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "bird";
	}

	@Override
	public Size getSize() {
		return Size.Small;
	}

}
