package net.arg3.jmud;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.arg3.jmud.enums.StatType;

@Embeddable
@Access(AccessType.PROPERTY)
public class Stats {
	private int strength;
	private int intelligence;
	private int wisdom;
	private int dexterity;
	private int constitution;
	private int luck;

	public void adjustStat(StatType type, int value) {
		switch (type) {
		case Strength:
			strength += value;
			break;
		case Intelligence:
			intelligence += value;
			break;
		case Wisdom:
			wisdom += value;
			break;
		case Dexterity:
			dexterity += value;
			break;
		case Constitution:
			constitution += value;
			break;
		case Luck:
			luck += value;
			break;
		}
	}

	@Column(name = "constitution")
	public int getCon() {
		return constitution;
	}

	@Column(name = "dexterity")
	public int getDex() {
		return dexterity;
	}

	@Column(name = "intelligence")
	public int getInt() {
		return intelligence;
	}

	@Column(name = "luck")
	public int getLuck() {
		return luck;
	}

	@Transient
	public int getStat(StatType type) {
		switch (type) {
		case Strength:
			return strength;
		case Intelligence:
			return intelligence;
		case Wisdom:
			return wisdom;
		case Dexterity:
			return dexterity;
		case Constitution:
			return constitution;
		case Luck:
			return luck;
		default:
			throw new IllegalArgumentException("stat type case not handled");
		}
	}

	@Column(name = "strength")
	public int getStr() {
		return strength;
	}

	@Column(name = "wisdom")
	public int getWis() {
		return wisdom;
	}

	@Transient
	public int getMaxWieldWeight() {
		return getStr() * 10;
	}

	public void setCon(int value) {
		constitution = value;
	}

	public void setDex(int value) {
		dexterity = value;
	}

	public void setInt(int value) {
		intelligence = value;
	}

	public void setLuck(int value) {
		luck = value;
	}

	public void setStat(StatType type, int value) {
		switch (type) {
		case Strength:
			strength = value;
			break;
		case Intelligence:
			intelligence = value;
			break;
		case Wisdom:
			wisdom = value;
			break;
		case Dexterity:
			dexterity = value;
			break;
		case Constitution:
			constitution = value;
			break;
		case Luck:
			luck = value;
			break;
		}
	}

	public void setStr(int value) {
		strength = value;
	}

	public void setWis(int value) {
		wisdom = value;
	}

}
