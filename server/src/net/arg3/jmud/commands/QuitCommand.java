package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;

public class QuitCommand extends Command {

	public class QuitException extends RuntimeException {

		private static final long serialVersionUID = 1L;
	}

	public QuitCommand() {
		super("quit");
	}

	@Override
	public int execute(Character ch, Argument argument) {
		if (!ch.isPlayer()) {
			return 1;
		}

		ch.writeln("Good-bye!");

		throw new QuitException();
	}
}
