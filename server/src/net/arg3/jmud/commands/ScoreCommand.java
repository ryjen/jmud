package net.arg3.jmud.commands;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Character;
import net.arg3.jmud.Player;

public class ScoreCommand extends Command {

	public ScoreCommand() {
		super("score");
	}

	@Override
	public int execute(Character ch, Argument argument) {

		Character.Vitals stats = ch.getVitals();

		ch.writeln("You are " + ch.getLongDescr());
		ch.writeln("A level " + ch.getLevel() + " " + ch.getRace().getName()
				+ ".");

		ch.writelnf("Hit: %3d/%3d Mana: %3d/%3d Move: %3d/%3d", stats.getHit(),
				stats.getMaxHit(), stats.getMana(), stats.getMaxMana(),
				stats.getMove(), stats.getMaxMove());

		if (ch.isPlayer()) {
			Player p = (Player) ch;

			p.writelnf("You are a %s.", p.getProfession().getName());

			if (p.getTerminal().isCompressing())
				p.writelnf("Your compression ratio is %1$.2f%%.", p
						.getTerminal().compressionRatio());
		}

		ch.writeln("You are " + ch.getPosition().toString().toLowerCase()
				+ " in " + ch.getRoom().getName());

		return 0;
	}
}
