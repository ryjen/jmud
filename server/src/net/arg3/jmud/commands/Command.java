/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.commands;

import java.util.HashSet;
import java.util.Iterator;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Help;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.Social;
import net.arg3.jmud.channels.Channel;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.interfaces.IExecutable;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public abstract class Command implements IExecutable {

	private static HashSet<Command> list = null;

	public static HashSet<Command> getList() {
		if (list == null) {
			list = new HashSet<Command>();

			for (Direction dir : Direction.values()) {
				list.add(new MoveCommand(dir));
			}

			list.add(new LookCommand());
			list.add(new QuitCommand());
			list.add(new ShutdownCommand());
			list.add(new LogoffCommand());
			list.add(new TitleBarCommand());
			list.add(new ColorCommand());
			list.add(new AnnounceCommand());
			list.add(new CommandsCommand());
			list.add(new HelpCommand());
			list.add(new ScoreCommand());
			list.add(new ChannelsCommand());
			list.add(new HintsCommand());
			list.add(new SayCommand());
			list.add(new SaveCommand());
			list.add(new KillCommand());

		}
		return list;
	}

	public static int Interpret(net.arg3.jmud.Character ch, String argument) {

		if (Jmud.isNullOrEmpty(argument)) {
			return 0;
		}

		Argument arg = new Argument(argument);
		String name = arg.getNext();

		IExecutable cmd = Channel.lookup(name);

		if (cmd != null)
			return cmd.execute(ch, arg);

		cmd = lookup(name);

		if (cmd != null)
			return cmd.execute(ch, arg);

		ch.writeln("No such command '" + argument + "'!");
		return -1;
	}

	public static IExecutable lookup(String argument) {
		if (Jmud.isNullOrEmpty(argument)) {
			return null;
		}

		IExecutable cmd = Channel.lookup(argument);

		if (cmd != null)
			return cmd;

		Iterator<Command> it = getList().iterator();

		while (it.hasNext()) {
			cmd = it.next();

			if (Jmud.isPrefix(cmd.getName(), argument)) {
				return cmd;
			}
		}

		return Social.lookup(argument);
	}

	private String name;
	private int level;
	private Help help;

	public Command(String name) {
		this.name = name;

		setHelp(Help.find(name + " command"));
	}

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
		if (!(obj instanceof Command)) {
			return false;
		}
		Command other = (Command) obj;
		if (getLevel() != other.getLevel()) {
			return false;
		}
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
	public abstract int execute(net.arg3.jmud.Character ch, Argument argument);

	/**
	 * @return the help
	 */
	public Help getHelp() {
		return help;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

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
		result = prime * result + level;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @param help
	 *            the help to set
	 */
	public void setHelp(Help help) {
		this.help = help;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
