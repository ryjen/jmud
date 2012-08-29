package net.arg3.jmud.races;

import javax.persistence.Entity;

import net.arg3.jmud.PcRace;
import net.arg3.jmud.enums.Size;

@Entity
public class TrollRace extends PcRace {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "troll";
	}

	@Override
	public Size getSize() {
		return Size.Medium;
	}

}
