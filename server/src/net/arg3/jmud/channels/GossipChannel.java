/**
 * Project: jmudlib
 * Date: 2009-09-22
 * Package: net.arg3.jmud.channels
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.channels;

import net.arg3.jmud.model.Character;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class GossipChannel extends Channel {

	public GossipChannel() {
		super("gossip", 20);
		setDisplay(ColorHelper
				.boldcolorizeText("*Gossip*", ColorHelper.MAGENTA));
		setDescription("Used for global roleplaying and character development.");
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.channels.Channel#getFlag()
	 */
	@Override
	public long getFlag() {
		return Channel.GOSSIP;
	}
}
