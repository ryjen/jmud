package net.arg3.jmud.races;

import javax.persistence.Entity;

import net.arg3.jmud.Race;
import net.arg3.jmud.enums.Size;

@Entity
public class ModronRace extends Race {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "modron";
	}

	@Override
	public Size getSize() {
		return Size.Medium;
	}

}