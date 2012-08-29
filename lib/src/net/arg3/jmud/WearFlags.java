/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud;

import net.arg3.jmud.annotations.FlagValue;

/**
 * 
 * @author Ryan
 */
public final class WearFlags {

	@FlagValue
	public static final long TAKE = (1L << 0);
	@FlagValue
	public static final long HEAD = (1L << 1);
	@FlagValue
	public static final long FACE = (1L << 2);
	@FlagValue
	public static final long EAR = (1L << 3);
	@FlagValue
	public static final long NECK = (1L << 4);
	@FlagValue
	public static final long SHOULDERS = (1L << 5);
	@FlagValue
	public static final long ARMS = (1L << 6);
	@FlagValue
	public static final long WRIST = (1L << 7);
	@FlagValue
	public static final long HANDS = (1L << 8);
	@FlagValue
	public static final long FINGER = (1L << 9);
	@FlagValue
	public static final long CHEST = (1L << 10);
	@FlagValue
	public static final long WAIST = (1L << 11);
	@FlagValue
	public static final long LEGS = (1L << 12);
	@FlagValue
	public static final long FEET = (1L << 13);
	@FlagValue
	public static final long SHIELD = (1L << 14);
	@FlagValue
	public static final long BACK = (1L << 15);
	@FlagValue
	public static final long WIELD = (1L << 16);
	@FlagValue
	public static final long LIGHT = (1L << 17);
	@FlagValue
	public static final long NO_SAC = (1L << 18);
	@FlagValue
	public static final long FLOAT = (1L << 19);

	private WearFlags() {
	}
}
