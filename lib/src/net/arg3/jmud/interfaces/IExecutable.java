package net.arg3.jmud.interfaces;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;

public interface IExecutable {
	public int execute(Character ch, Argument argument);

	// for lookups
	public String getName();
}
