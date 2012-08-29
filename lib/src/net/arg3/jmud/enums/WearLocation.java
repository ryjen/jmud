/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.enums;

import net.arg3.jmud.Jmud;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public enum WearLocation {

	NONE, HEAD, FACE, LEFT_EAR, RIGHT_EAR, NECK, SHOULDERS, ARMS, LEFT_WRIST, RIGHT_WRIST, HANDS, LEFT_FINGER, RIGHT_FINGER, CHEST, WAIST, LEGS, FEET, SHIELD, BACK, WIELD, LIGHT, FLOAT;

	public static WearLocation lookup(String arg) {

		for (WearLocation loc : values()) {
			if (Jmud.isPrefix(loc.name(), arg))
				return loc;
		}

		try {
			return fromOrdinal(Integer.parseInt(arg));
		} catch (Exception e) {
			return NONE;
		}
	}

	public static WearLocation fromOrdinal(int ordinal) {
		for (WearLocation loc : values()) {
			if (loc.ordinal() == ordinal)
				return loc;
		}
		return NONE;
	}
}
