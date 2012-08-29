package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;
import net.arg3.jmud.Player;

public class ColorCommand extends Command {

	public ColorCommand() {
		super("color");
	}

	@Override
	public int execute(Character ch, Argument argument) {
		if (!ch.isPlayer()) {
			return 1;
		}

		Player p = (Player) ch;

		p.getTerminal().setColor(!p.getTerminal().colorOnOff());

		p.writeln("Color is now "
				+ (p.getTerminal().colorOnOff() ? "on." : "off."));
		return 0;
	}
}
