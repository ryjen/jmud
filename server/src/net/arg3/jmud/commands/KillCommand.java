package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Fight;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.updates.Updater;

public class KillCommand extends Command {

	public KillCommand() {
		super("kill");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		Character victim = ch.getRoom().getChar(argument.toString());

		if (victim == null) {
			ch.writeln("They aren't here.");
			return 0;
		}

		if (victim == ch) {
			ch.writeln("Suicide? Lets debate that.");
			return 0;
		}

		ch.writeln();

		Updater.Add(new Fight(ch, victim));
		return 0;
	}

}
