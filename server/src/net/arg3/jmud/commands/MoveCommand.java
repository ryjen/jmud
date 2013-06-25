/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Exit;
import net.arg3.jmud.model.Room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ryan
 */
public class MoveCommand extends Command {

	static final Logger log = LoggerFactory.getLogger(MoveCommand.class);
	Direction direction;

	public MoveCommand(Direction dir) {
		super(dir.toString().toLowerCase());
		direction = dir;
	}

	@Override
	public int execute(Character ch, Argument argument) {
		if (ch.getRoom() == null) {
			log.error(ch + " is not in a room.");
			return 1;
		}

		Exit exit = ch.getRoom().getExit(direction);

		if (exit == null) {
			ch.writeln("You can't move in that direction.");
			return 1;
		}

		Room toRoom = exit.getToRoom();

		if (toRoom == null) {
			log.error(exit + " has to destination.");
			ch.writeln("Unable to move in that direction. An error has been logged.");
			return 1;
		}

		ch.getRoom().getCharacters().remove(ch);
		toRoom.getCharacters().add(ch);
		ch.setRoom(toRoom);

		new LookCommand().execute(ch, new Argument());
		return 0;
	}
}
