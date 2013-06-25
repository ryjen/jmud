package net.arg3.jmud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.arg3.jmud.Jmud;
import net.arg3.jmud.enums.ApplyType;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.StatType;
import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

@Entity
@Table(name = "affect")
public class Affect implements IDataObject<Long>, IFormatible, Cloneable {

	private static final long serialVersionUID = 1L;
	private long id;
	private int duration;
	private int level;
	private int modifier;
	private ApplyType location;
	private long flag;
	private int extra;

	public static final long Sanctuary = (1L << 0);
	public static final long ProtectGood = (1L << 1);
	public static final long ProtectEvil = (1L << 2);
	public static final long Blindness = (1L << 3);
	public static final long Invisibility = (1L << 4);
	public static final long DetectEvil = (1L << 5);
	public static final long DetectGood = (1L << 6);
	public static final long DetectInvis = (1L << 7);
	public static final long DetectMagic = (1L << 8);
	public static final long DetectHidden = (1L << 9);
	public static final long FaerieFire = (1L << 10);
	public static final long Curse = (1L << 11);
	public static final long Poison = (1L << 12);
	public static final long Sleep = (1L << 13);
	public static final long Sneak = (1L << 14);
	public static final long Hide = (1L << 15);
	public static final long Charm = (1L << 16);
	public static final long Flying = (1L << 17);
	public static final long PassDoor = (1L << 18);
	public static final long Haste = (1L << 19);
	public static final long Plague = (1L << 20);
	public static final long Calm = (1L << 21);
	public static final long Weaken = (1L << 22);
	public static final long Berserk = (1L << 23);
	public static final long Regeneration = (1L << 24);
	public static final long Slow = (1L << 25);

	public Affect() {
	}

	@Override
	protected Affect clone() throws CloneNotSupportedException {
		Affect clone = (Affect) super.clone();

		return clone;
	}

	public void apply(IAffectable to, boolean silent) {
		to.addAffect(this);

		if (to instanceof Character)
			adjust((Character) to, getModifier());
	}

	@Override
	public int compareTo(IDataObject<Long> arg0) {
		return getId().compareTo(arg0.getId());
	}

	@Column(name = "duration")
	public int getDuration() {
		return duration;
	}

	@Column(name = "location")
	@Enumerated(EnumType.STRING)
	public ApplyType getLocation() {
		return location;
	}

	@Column(name = "extra")
	public int getExtra() {
		return extra;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "affect_id")
	public Long getId() {
		return id;
	}

	@Column(name = "level")
	public int getLevel() {
		return level;
	}

	@Column(name = "modifier")
	public int getModifier() {
		return modifier;
	}

	@Column(name = "flag")
	public long getFlag() {
		return flag;
	}

	public boolean hasExpired() {
		return (duration <= 0);
	}

	public void setDuration(int value) {
		duration = value;
	}

	public void setLocation(ApplyType value) {
		location = value;
	}

	public void setExtra(int value) {
		extra = value;
	}

	@Override
	public void setId(Long value) {
		id = value;
	}

	public void setLevel(int value) {
		level = value;
	}

	public void setModifier(int value) {
		modifier = value;
	}

	public void setFlag(long value) {
		flag = value;
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return toString();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + getId() + "]";
		default:
			return toString();
		}
	}

	private void adjust(Character to, int modifier) {
		switch (location) {
		case Con:
			to.getStats().adjustStat(StatType.Constitution, modifier);
			break;
		case Str:
			to.getStats().adjustStat(StatType.Strength, modifier);
			break;
		case Wis:
			to.getStats().adjustStat(StatType.Wisdom, modifier);
			break;
		case Int:
			to.getStats().adjustStat(StatType.Intelligence, modifier);
			break;
		case Dex:
			to.getStats().adjustStat(StatType.Dexterity, modifier);
			break;
		case Luck:
			to.getStats().adjustStat(StatType.Luck, modifier);
			break;
		case Damroll:
			to.setDamroll(to.getDamroll() + modifier);
			break;
		case Hitroll:
			to.setHitroll(to.getHitroll() + modifier);
			break;
		case Resistance:
			to.adjustResistance(DamType.values()[getExtra()], modifier);
			break;
		case Hit:
			to.getVitals().adjustHit(modifier);
			break;
		case Mana:
			to.getVitals().adjustMana(modifier);
			break;
		case Move:
			to.getVitals().adjustMove(modifier);
			break;
		default:
			break;
		}
	}

	public void undo(IAffectable to, boolean silent) {
		to.removeAffect(this);

		if (to instanceof Character) {
			adjust((Character) to, -getModifier());
		}
	}

	public void update() {
		--duration;
	}
}
