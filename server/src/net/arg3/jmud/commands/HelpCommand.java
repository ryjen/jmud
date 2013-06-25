package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Help;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		if (argument.isNullOrEmpty()) {
			ch.writeln("No default help!");
			return 1;
		}

		Help h = Help.find(argument.toString());

		if (h == null) {
			ch.writeln("No help on that subject!");
			return 1;
		}

		ch.writeln(h.getText());

		return 0;
	}
}
