package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.model.Character;

public class LogoffCommand extends Command {

	public class LogoffException extends RuntimeException {

		private static final long serialVersionUID = 1L;
	}

	public LogoffCommand() {
		super("logoff");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		if (!ch.isPlayer()) {
			return -1;
		}

		ch.writeln("Good-bye!");

		throw new LogoffException();
	}
}
