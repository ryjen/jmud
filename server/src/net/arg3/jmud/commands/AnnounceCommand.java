package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;
import net.arg3.jmud.Server;
import net.arg3.jmud.World;

public class AnnounceCommand extends Command {

	public AnnounceCommand() {
		super("announce");
		setLevel(World.IMMORTAL);
	}

	@Override
	public int execute(Character ch, Argument argument) {

		if (argument.isNullOrEmpty()) {
			ch.writeln("You must provide something to announce!");
			return 1;
		}

		Server.getInstance().announce(ch, argument.toString());

		return 0;
	}
}
