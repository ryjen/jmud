package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.arg3.jmud.Attack;
import net.arg3.jmud.Object;
import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.WeaponClass;

@Entity
@DiscriminatorValue("weapon")
public class WeaponObject extends Object {

	@FlagValue
	public static final int TWO_HANDED = (1 << 0);
	@FlagValue
	public static final int FLAMING = (1 << 1);
	@FlagValue
	public static final int FROST = (1 << 2);
	@FlagValue
	public static final int VAMPIRIC = (1 << 3);
	@FlagValue
	public static final int SHARP = (1 << 4);
	@FlagValue
	public static final int VORPAL = (1 << 5);
	@FlagValue
	public static final int SHOCKING = (1 << 6);
	@FlagValue
	public static final int POISON = (1 << 7);

	private static final long serialVersionUID = 1L;
	int diceNumber;
	int diceType;
	int weaponFlags;
	WeaponClass weaponClass;
	DamType damType;
	String noun;

	@Transient
	public int getDiceNumber() {
		return diceNumber;
	}

	@Transient
	public int getDiceType() {
		return diceType;
	}

	@Transient
	public WeaponClass getWeaponClass() {
		return weaponClass;
	}

	@Transient
	public Attack getAttack() {
		return new Attack(damType, noun);
	}

	@Transient
	public boolean hasWeaponFlag(int bit) {
		return (weaponFlags & bit) != 0;
	}

	public void setDiceNumber(int value) {
		diceNumber = value;
	}

	public void setDiceType(int value) {
		diceType = value;
	}

	public void setWeaponFlags(int value) {
		weaponFlags = value;
	}

	public void setWeaponClass(WeaponClass value) {
		weaponClass = value;
	}

	public void setAttack(Attack a) {
		damType = a.getDamType();
		noun = a.getNoun();
	}

	public void setDamType(DamType type) {
		damType = type;
	}

	@Override
	@Transient
	protected java.lang.Object[] getValues() {
		return new java.lang.Object[] { diceNumber, diceType, weaponFlags,
				weaponClass, noun, damType };
	}

	@Override
	public void setValues(java.lang.Object[] values) {
		assert (values.length == 6);

		diceNumber = (Integer) values[0];
		diceType = (Integer) values[1];
		weaponFlags = (Integer) values[2];
		weaponClass = (WeaponClass) values[3];
		noun = (String) values[4];
		damType = (DamType) values[5];
	}
}
