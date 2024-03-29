package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.World;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Player;

public class ShutdownCommand extends Command {

	public ShutdownCommand() {
		super("shutdown");
		setLevel(World.IMMORTAL);
	}

	@Override
	public int execute(Character ch, Argument argument) {

		for (Player p : Player.getPlaying()) {

			p.writeln("Shutting down...");
			Persistance.save(p);
		}

		System.exit(0);
		return 0;
	}
}
