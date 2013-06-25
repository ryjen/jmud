package net.arg3.jmud.interfaces;

import net.arg3.jmud.model.Affect;

public interface IAffectable {
	void addAffect(Affect af);

	void removeAffect(Affect af);

	Affect findAffect(long flag);
}
