/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.arg3.jmud.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.arg3.jmud.Jmud;

/**
 * 
 * @author Ryan
 */
public enum DamType {
	NONE(-1), SLASH, BASH, PIERCE, MAGIC, FIRE, COLD, ELECTRICITY, ACID, POISON, DISEASE, WATER, HOLY, ENERGY, NEGATIVE, MENTAL, LIGHT, SOUND;

	DamType(int value) {
		this.value = value;
	}

	DamType() {
		value = ordinal() - 1;
	}

	int value;

	public int getValue() {
		return value;
	}

	public static DamType random() {
		return DamType.values()[Jmud.range(SLASH.ordinal(), NEGATIVE.ordinal())];
	}

	static Map<String, DamType> defaultAttackTypes;

	public static synchronized Set<Entry<String, DamType>> getDefaultAttackTypes() {
		if (defaultAttackTypes == null) {
			defaultAttackTypes = new HashMap<String, DamType>();

			defaultAttackTypes.put("hit", BASH);
			defaultAttackTypes.put("slice", SLASH);
			defaultAttackTypes.put("stab", PIERCE);
			defaultAttackTypes.put("slash", SLASH);
			defaultAttackTypes.put("whip", SLASH);
			defaultAttackTypes.put("claw", SLASH);
			defaultAttackTypes.put("blast", BASH);
			defaultAttackTypes.put("pound", BASH);
			defaultAttackTypes.put("crush", BASH);
			defaultAttackTypes.put("grep", SLASH);
			defaultAttackTypes.put("bite", PIERCE);
			defaultAttackTypes.put("pierce", PIERCE);
			defaultAttackTypes.put("suction", BASH);
			defaultAttackTypes.put("beating", BASH);
			defaultAttackTypes.put("digestion", ACID);
			defaultAttackTypes.put("charge", BASH); /* 15 */
			defaultAttackTypes.put("slap", BASH);
			defaultAttackTypes.put("punch", BASH);
			defaultAttackTypes.put("wrath", ENERGY);
			defaultAttackTypes.put("magic", ENERGY);
			defaultAttackTypes.put("divine power", HOLY); /* 20 */
			defaultAttackTypes.put("cleave", SLASH);
			defaultAttackTypes.put("scratch", PIERCE);
			defaultAttackTypes.put("peck", PIERCE);
			defaultAttackTypes.put("peck", BASH);
			defaultAttackTypes.put("chop", SLASH); /* 25 */
			defaultAttackTypes.put("sting", PIERCE);
			defaultAttackTypes.put("smash", BASH);
			defaultAttackTypes.put("shocking bite", ELECTRICITY);
			defaultAttackTypes.put("flaming bite", FIRE);
			defaultAttackTypes.put("freezing bite", COLD); /* 30 */
			defaultAttackTypes.put("acidic bite", ACID);
			defaultAttackTypes.put("chomp", PIERCE);
			defaultAttackTypes.put("life drain", NEGATIVE);
			defaultAttackTypes.put("thrust", PIERCE);
			defaultAttackTypes.put("slime", ACID);
			defaultAttackTypes.put("shock", ELECTRICITY);
			defaultAttackTypes.put("thwack", BASH);
			defaultAttackTypes.put("flame", FIRE);
			defaultAttackTypes.put("chill", COLD);
		}
		return defaultAttackTypes.entrySet();
	}
}
