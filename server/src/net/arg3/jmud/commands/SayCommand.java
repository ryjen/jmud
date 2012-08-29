/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;

/**
 * 
 * @author ryan
 */
public class SayCommand extends Command {

	public SayCommand() {
		super("say");
	}

	@Override
	public int execute(Character ch, Argument argument) {
		if (argument.isNullOrEmpty()) {
			ch.writeln("Say what?");
			return 1;
		}
		ch.writeln("~gYou say '~G" + argument + "~g'~x");

		ch.getRoom().format("~g{0} says '~G{1}~g'~x", ch, argument);
		return 0;
	}
}
