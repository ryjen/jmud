package net.arg3.jmud.updates;

import net.arg3.jmud.Area;
import net.arg3.jmud.interfaces.ITickable;

public class AreaUpdate implements ITickable {

	@Override
	public int getPeriod() {
		return SECOND * 45;
	}

	@Override
	public boolean tick() {
		for (Area area : Area.getList()) {
			area.update();
		}
		return false;
	}

}
