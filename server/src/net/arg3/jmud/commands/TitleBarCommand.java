package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Player;

public class TitleBarCommand extends Command {

	public TitleBarCommand() {
		super("titlebar");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		if (!ch.isPlayer()) {
			return 1;
		}

		Player p = (Player) ch;

		p.getFlags().toggle(Player.TITLEBAR);

		p.writeln("Titlebar "
				+ (p.getFlags().has(Player.TITLEBAR) ? "on." : "off."));
		return 0;
	}
}
