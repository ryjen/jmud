/**
 * Project: jmudserver
 * Date: 2009-09-23
 * Package: net.arg3.jmud.commands
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.commands;

import java.util.Random;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.World;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Hint;
import net.arg3.jmud.model.Player;
import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class HintsCommand extends Command {

	public HintsCommand() {
		super("hints");
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
		if (!ch.isPlayer()) {
			return 1;
		}

		Player p = ch.toPlayer();

		if (argument.isNullOrEmpty()) {
			p.getFlags().toggle(Player.HINTS);
			p.writeln("Hints "
					+ (p.getFlags().has(Player.HINTS) ? "on." : "off."));
			return 0;
		}

		if (p.getLevel() > World.IMMORTAL) {
			String arg = argument.getNext();

			if (arg.equalsIgnoreCase("add")) {
				if (argument.isNullOrEmpty()) {
					p.writeln("You must provide a hint to add!");
					return 1;
				}

				Hint hint = new Hint();
				hint.setValue(argument.toString());
				Hint.list.add(hint);
				Persistance.save(hint);
				return 0;
			} else if (arg.equalsIgnoreCase("list")) {
				StringBuilder buf = new StringBuilder();

				for (Hint hint : Hint.list) {
					buf.append(hint.getId());
					buf.append(") ");
					buf.append(hint.getValue());
					buf.append(BasicTerminalIO.CRLF);
				}

				p.write(buf);
				return 0;
			} else if (arg.equalsIgnoreCase("remove")) {
				int index;
				try {
					index = Integer.parseInt(arg);
				} catch (ClassCastException ex) {
					p.writeln("You must specify the index of a hint. Use 'list' to view.");
					return 1;
				}

				if (index < 1 || index > Hint.list.size()) {
					p.writeln("That is not a valid index for a hint.");
					return 1;
				}

				Hint hint = Hint.list.get(index - 1);
				Persistance.delete(hint);
				Hint.list.remove(hint);
				return 0;
			}
		}

		if (argument.startsWith("random")) {
			int index = new Random().nextInt(Hint.list.size());

			p.writeln(ColorHelper.boldcolorizeText("HINT", ColorHelper.RED)
					+ ": " + Hint.list.get(index).getValue());
		}

		ch.writeln(getHelp().getSyntax());
		return 0;
	}
}
