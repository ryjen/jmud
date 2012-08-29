package net.arg3.jmud.commands;

import java.util.TreeSet;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;
import net.arg3.jmud.Player;
import net.arg3.jmud.channels.Channel;

public class CommandsCommand extends Command {

	public CommandsCommand() {
		super("commands");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		int columns = (ch.isPlayer() ? ((Player) ch).getTerminal().getColumns()
				: 80) / 10;
		int i = 0;
		TreeSet<String> names = new TreeSet<String>();

		for (Command cmd : getList()) {
			names.add(cmd.getName());
		}

		for (Channel chan : Channel.getList()) {
			if (!chan.canSee(ch, ch)) {
				continue;
			}

			names.add(chan.getName());
		}

		for (String name : names) {
			if ((++i % columns) == 0) {
				ch.writelnf("%-10s", name);
			} else {
				ch.writef("%-10s", name);
			}
		}
		if ((i % columns) != 0) {
			ch.writeln("");
		}
		return 0;
	}
}
