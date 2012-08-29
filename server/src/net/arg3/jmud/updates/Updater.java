/**
 * Project: jmudserver
 * Date: 2009-09-23
 * Package: net.arg3.jmud.updates
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.updates;

import java.util.Timer;
import java.util.TimerTask;

import net.arg3.jmud.interfaces.ITickable;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public final class Updater {

	static Timer timer = new Timer(true);

	public static void Add(final ITickable ticker) {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (ticker.tick()) {
					this.cancel();
				}
			}
		}, 0, ticker.getPeriod());
	}

	public static void initialize() {
		Add(new HintUpdate());
		Add(new AreaUpdate());
	}
}
