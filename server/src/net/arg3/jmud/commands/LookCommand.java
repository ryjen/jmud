/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud.commands
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Room;
import net.wimpi.telnetd.io.terminal.ColorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class LookCommand extends Command {

	static final Logger logger = LoggerFactory.getLogger(LookCommand.class);

	public LookCommand() {
		super("look");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.commands.Command#execute(java.lang.Character,
	 * java.lang.String)
	 */
	@Override
	public int execute(Character ch, Argument argument) {

		Room room;

		if ((room = ch.getRoom()) == null) {
			logger.warn("character with no room - " + ch);
			return 1;
		}

		ch.writeln(ColorHelper.boldcolorizeText(room.getName(),
				ColorHelper.GREEN));

		ch.writeln(room.getDescription());
		ch.writeln("");

		ch.write(ColorHelper
				.colorizeText("Exits: [ ", ColorHelper.GREEN, false));

		if (room.getExits().size() == 0) {
			ch.write("none");
		} else {
			for (Direction d : Direction.values()) {
				if (room.getExit(d) != null) {
					ch.write(d.toString().toLowerCase() + " ");
				}
			}
		}
		ch.writeln(ColorHelper.colorizeText("]", ColorHelper.GREEN));

		for (Object obj : room.getObjects()) {
			ch.writeln(obj.getLongDescr());
		}
		for (Character rch : room.getCharacters()) {
			if (rch == ch) {
				continue;
			}
			ch.writeln(rch.getLongDescr());
		}
		return 0;
	}
}
