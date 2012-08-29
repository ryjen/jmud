/**
 * Project: jMUD
 * Date: 2009-09-09
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.enums;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public enum Direction {

	NORTH, EAST, SOUTH, WEST, UP, DOWN;

	public static Direction fromOrdinal(int value) {
		for (Direction d : Direction.values()) {
			if (d.ordinal() == value)
				return d;
		}
		throw new IllegalArgumentException("value is not a direction");
	}

	public Direction Reverse() {
		switch (this) {
		default:
		case NORTH:
			return SOUTH;
		case EAST:
			return WEST;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		}
	}
}
