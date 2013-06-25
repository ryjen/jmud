/**
 * Project: jmudserver
 * Date: 2009-09-23
 * Package: net.arg3.jmud.updates
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.updates;

import net.arg3.jmud.Jmud;
import net.arg3.jmud.interfaces.ITickable;
import net.arg3.jmud.model.Hint;
import net.arg3.jmud.model.Player;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class HintUpdate implements ITickable {
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.updates.Update#getPeriod()
	 */
	@Override
	public int getPeriod() {
		return 45 * SECOND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public boolean tick() {
		for (Player p : Player.getPlaying()) {
			if (!p.getFlags().has(Player.HINTS)) {
				continue;
			}

			Hint h = Hint.list.get(Jmud.Rand.nextInt(Hint.list.size()));

			if (h == null) {
				continue;
			}

			p.writeln(ColorHelper.randomColorizeText("HINT") + ": "
					+ h.getValue());
		}
		return false;
	}
}
