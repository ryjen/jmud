/**
 * Project: jmudlib
 * Date: 2009-09-23
 * Package: net.arg3.jmud.channels
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.channels;

import net.arg3.jmud.Character;
import net.arg3.jmud.World;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class AdminChannel extends Channel {

	public AdminChannel() {
		super("admin", 20);
		setDisplay(ColorHelper.boldcolorizeText("[Admin]", ColorHelper.CYAN));
		setDescription("Used for communication between administrators.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jennings.ryan.jMUD.channels.Channel#canSee(net.jennings.ryan.jMUD
	 * .Character, net.jennings.ryan.jMUD.Character)
	 */
	@Override
	public boolean canSee(Character ch, Character viewer) {
		return viewer.getLevel() >= World.IMMORTAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.channels.Channel#getFlag()
	 */
	@Override
	public long getFlag() {
		return Channel.ADMIN;
	}
}
