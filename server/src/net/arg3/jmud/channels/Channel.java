/**
 * Project: jmudlib
 * Date: 2009-09-22
 * Package: net.arg3.jmud.channels
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.channels;

import java.util.HashSet;
import java.util.LinkedList;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.interfaces.IExecutable;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Player;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public abstract class Channel implements IExecutable {

	public static final long GOSSIP = (1L << 0);
	public static final long ADMIN = (1L << 1);
	private static HashSet<Channel> list = null;

	public static HashSet<Channel> getList() {
		if (list == null) {
			list = new HashSet<Channel>();
			list.add(new GossipChannel());
			list.add(new AdminChannel());
		}
		return list;
	}

	public static int Interpret(Character ch, String argument) {
		Argument arg = new Argument(argument);

		String name = arg.getNext();

		Channel c = lookup(name);

		if (c != null)
			return c.execute(ch, arg);
		else
			return -1;
	}

	public static Channel lookup(String argument) {
		if (Jmud.isNullOrEmpty(argument))
			return null;

		for (Channel c : getList()) {
			if (Jmud.isPrefix(c.getName(), argument)) {
				return c;
			}
		}

		return null;
	}

	private final LinkedList<String> history;
	private final int historyLength;
	private String name;
	private String display;
	private String description;

	public Channel(String name, int historyLength) {
		history = new LinkedList<String>();
		this.name = name;
		this.display = Jmud.capitalize(name);
		this.historyLength = historyLength;
	}

	public abstract boolean canSee(Character ch, Character viewer);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Channel)) {
			return false;
		}
		Channel other = (Channel) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public int execute(Character ch, Argument argument) {
		if (argument.isNullOrEmpty()) {
			if (ch.isPlayer()) {
				Player p = ch.toPlayer();

				p.getChannelFlags().toggle(getFlag());
				p.writeln(Jmud.capitalize(getName())
						+ " channel is now "
						+ (!p.getChannelFlags().has(getFlag()) ? "on." : "off."));
				return 0;
			}
		} else if (argument.peekNext().contentEquals("-h")) {
			displayHistory(ch);
			return 0;
		} else if (ch.isPlayer()) {
			// turn the channel on if its off and we're sending a message
			Player p = ch.toPlayer();

			if (p.getChannelFlags().has(getFlag())) {
				p.getChannelFlags().toggle(getFlag());
			}
		}

		String message;

		if (argument.peekNext().contentEquals("!")) {
			argument.getNext();
			ch.writeln(getDisplay() + " You " + argument);
			message = getDisplay() + " " + ch.getName() + " " + argument;
		} else {
			ch.writeln(getDisplay() + " You say: " + argument);
			message = getDisplay() + " " + ch.getName() + " says: " + argument;
		}
		updateHistory(message);
		for (Player p : Player.getPlaying()) {
			if (p == ch) {
				continue;
			}

			if (!p.getChannelFlags().has(getFlag()) && canSee(ch, p)) {
				p.writeln(message);
			}
		}
		return 0;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}

	public abstract long getFlag();

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public void setDescription(String value) {
		description = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Channel [name=" + name + "]";
	}

	private void displayHistory(Character ch) {
		if (history.size() == 0) {
			ch.writeln("No messages in " + getName() + " history.");
			return;
		}
		for (String msg : history) {
			ch.writeln(msg);
		}
	}

	private void updateHistory(String message) {
		history.add("[" + Jmud.now() + "] " + message);
		if (history.size() > historyLength) {
			history.removeFirst();
		}
	}

	/**
	 * @param display
	 *            the display to set
	 */
	protected void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}
}
