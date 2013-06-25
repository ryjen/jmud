package net.arg3.jmud.races;

import javax.persistence.Entity;

import net.arg3.jmud.enums.Size;
import net.arg3.jmud.model.PcRace;

@Entity
public class GoblinRace extends PcRace {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "goblin";
	}

	@Override
	public Size getSize() {
		return Size.Medium;
	}

}
