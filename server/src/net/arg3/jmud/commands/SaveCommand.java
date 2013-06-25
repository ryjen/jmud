/**
 * 
 */
package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.model.Character;

/**
 * @author Ryan
 * 
 */
public class SaveCommand extends Command {

	public SaveCommand() {
		super("save");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jennings.ryan.jMUD.commands.Command#execute(net.jennings.ryan.jMUD
	 * .Character, net.jennings.ryan.jMUD.Argument)
	 */
	@Override
	public int execute(Character ch, Argument argument) {
		if (!ch.isPlayer())
			return 1;

		Persistance.save(ch);

		ch.writeln("Character saved.");
		return 0;
	}

}
