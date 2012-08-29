/**
 * Project: jmudserver
 * Date: 2009-09-23
 * Package: net.arg3.jmud.commands
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;
import net.arg3.jmud.channels.Channel;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class ChannelsCommand extends Command {

	public ChannelsCommand() {
		super("channels");
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

		ch.writeln(ColorHelper.boldcolorizeText("Channels available:",
				ColorHelper.WHITE));
		ch.writeln("-------------------");
		for (Channel chan : Channel.getList()) {
			ch.writelnf("%-10s: %s", ColorHelper.boldcolorizeText(
					chan.getName(), ColorHelper.WHITE), chan.getDescription());
		}
		return 0;
	}
}
