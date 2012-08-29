/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.enums.Alignment;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.Ethos;
import net.arg3.jmud.enums.Position;
import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.enums.Size;
import net.arg3.jmud.enums.WearLocation;
import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IEnvironmental;
import net.arg3.jmud.interfaces.IFormatible;
import net.arg3.jmud.interfaces.IWritable;
import net.arg3.jmud.objects.ArmorObject;
import net.arg3.jmud.objects.WeaponObject;

import org.hibernate.annotations.Proxy;
import org.hibernate.event.PostLoadEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "`character`")
@Inheritance(strategy = InheritanceType.JOINED)
@Proxy(lazy = false)
public abstract class Character implements IDataObject<Long>, IWritable,
		IFormatible, IAffectable, IEnvironmental, PostLoadEventListener,
		Cloneable {

	@Embeddable
	@Access(AccessType.PROPERTY)
	public class Vitals implements Cloneable {
		int hit;
		int maxHit;
		int mana;
		int maxMana;
		int move;
		int maxMove;

		public Vitals() {
			setHit(100);
			setMaxHit(100);
			setMana(100);
			setMaxMana(100);
			setMove(100);
			setMaxMove(100);
		}

		@Override
		protected Vitals clone() throws CloneNotSupportedException {
			Vitals v = (Vitals) super.clone();

			return v;
		}

		public void adjustHit(int value) {
			this.hit += value;
		}

		public void adjustMana(int value) {
			this.mana += value;
		}

		public void adjustMove(int value) {
			this.move += value;
		}

		@Column(name = "hit", nullable = false)
		public int getHit() {
			return this.hit;
		}

		@Column(name = "mana", nullable = false)
		public int getMana() {
			return this.mana;
		}

		@Column(name = "max_hit", nullable = false)
		public int getMaxHit() {
			return this.maxHit;
		}

		@Column(name = "max_mana", nullable = false)
		public int getMaxMana() {
			return this.maxMana;
		}

		@Column(name = "max_move", nullable = false)
		public int getMaxMove() {
			return this.maxMove;
		}

		@Column(name = "move", nullable = false)
		public int getMove() {
			return this.move;
		}

		public void setHit(int hit) {
			this.hit = hit;
		}

		public void setMana(int mana) {
			this.mana = mana;
		}

		public void setMaxHit(int maxHit) {
			this.maxHit = maxHit;
		}

		public void setMaxMana(int maxMana) {
			this.maxMana = maxMana;
		}

		public void setMaxMove(int maxMove) {
			this.maxMove = maxMove;
		}

		public void setMove(int move) {
			this.move = move;
		}
	}

	private static final long serialVersionUID = 1L;
	private static Set<Character> list = null;

	private static final Logger log = LoggerFactory.getLogger(Character.class);

	public static Set<Character> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Character>());
			list = new HashSet<Character>();
		}
		return list;
	}

	private long id;
	private Race race;
	private String name;
	private String description;
	private Vitals currentVitals;
	private Set<Object> carrying;
	private Room room;
	private Character fighting;
	private Set<Affect> affects;
	private float size;
	private int alignment;
	private int ethos;
	private Money money;
	private int level;
	private Position position;
	private Sex sex;
	private Flag affectedBy;
	private int hitroll;
	private int weight;
	private Map<String, String> extraDescr;
	private Attributes attributes;
	private Attributes baseAttributes;

	public Character() {
		setDescription("");
		setPosition(Position.STANDING);
		setAlignment(Alignment.Neutral.getValue());
		setEthos(Ethos.Neutral.getValue());
		setLevel(1);
		setMoney(new Money());
		setAffectedBy(new Flag());
		setExtraDescriptions(new HashMap<String, String>());
		setAffects(new HashSet<Affect>());
		setBaseAttributes(new Attributes());
		setVitals(new Vitals());
		getList().add(this);
	}

	public Character(String name) {
		this();
		setName(name);
	}

	@Override
	protected Character clone() throws CloneNotSupportedException {
		Character ch = (Character) super.clone();

		for (Object o : carrying) {
			ch.carrying.add(o.clone());
		}
		ch.currentVitals = currentVitals.clone();

		for (Affect a : affects) {
			ch.affects.add(a.clone());
		}
		ch.money = money.clone();
		ch.affectedBy = affectedBy.clone();

		ch.attributes = attributes.clone();
		ch.baseAttributes = baseAttributes.clone();
		return ch;
	}

	@Override
	public void addAffect(Affect af) {
		affects.add(af);
	}

	public void adjustResistance(DamType type, int value) {
		attributes.adjustResistance(type, value);
	}

	@Override
	public int compareTo(IDataObject<Long> o) {
		return getId().compareTo(o.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Character)) {
			return false;
		}
		Character other = (Character) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	@Transient
	public Object getEq(WearLocation location) {
		for (Object obj : getCarrying())
			if (obj.getWearLocation() == location)
				return obj;

		return null;
	}

	public boolean takeOff(Object obj, WearLocation loc, boolean fReplace) {
		if (getEq(loc) == null)
			return true;
		if (!fReplace)
			return false;

		if (obj.getFlags().has(Object.NOREMOVE)) {
			format("You can't remoe {0}.", obj);
			return false;
		}

		unequip(obj);
		getRoom().format("{0} stops using {1}.", this, obj);
		format("You stop using {0}.", obj);
		return true;
	}

	public void unequip(Object obj) {
		if (obj.getWearLocation() == WearLocation.NONE) {
			log.warn("object not equipped");
			return;
		}

		if (obj instanceof ArmorObject) {
			ArmorObject o = (ArmorObject) obj;

			for (DamType type : DamType.values())
				adjustResistance(type, -o.getResistance(type));
		}

		for (Affect paf : obj.getAffects()) {
			paf.undo(this, false);
		}
	}

	public void equip(Object obj, WearLocation loc) {
		if (getEq(loc) != null) {
			log.error("character already equiped " + loc);
			return;
		}

		if ((obj.getFlags().has(Object.ANTIEVIL) && Alignment
				.isEvil(getAlignment()))
				|| (obj.getFlags().has(Object.ANTIGOOD) && Alignment
						.isGood(getAlignment()))
				|| (obj.getFlags().has(Object.ANTINEUTRAL) && Alignment
						.isNeutral(getAlignment()))) {
			format("You are ~?zapped~x by {0} and drop it.", obj);
			getRoom().format("{0} is ~?zapped~x by {1} and drops it.", this,
					obj);
			getCarrying().remove(obj);
			getRoom().getObjects().add(obj);
			return;
		}

		obj.setWearLocation(loc);

		if (obj instanceof ArmorObject) {
			ArmorObject o = (ArmorObject) obj;

			for (DamType type : DamType.values())
				adjustResistance(type, o.getResistance(type));
		}

		for (Affect paf : obj.getAffects()) {
			paf.apply(this, false);
		}
	}

	public void wear(Object obj, boolean fReplace) {

		if (!getCarrying().contains(obj))
			getCarrying().add(obj);

		if (obj.getLevel() > getLevel()) {
			getRoom().format("{0} tries to use {1} but is too inexperienced.",
					this, obj);
			format("You try to use {0}, but are too inexperienced.", obj);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.ARMS)) {
			if (!takeOff(obj, WearLocation.ARMS, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} arms.", this, obj);
			format("You wear {0} on your arms.", obj);
			equip(obj, WearLocation.ARMS);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.BACK)) {
			if (!takeOff(obj, WearLocation.ARMS, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} back.", this, obj);
			format("You wear {0} on your back.", obj);
			equip(obj, WearLocation.BACK);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.CHEST)) {
			if (!takeOff(obj, WearLocation.CHEST, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} chest.", this, obj);
			format("You wear {0} on your chest.", obj);
			equip(obj, WearLocation.CHEST);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.EAR)) {
			if (getEq(WearLocation.LEFT_EAR) != null
					&& getEq(WearLocation.RIGHT_EAR) != null
					&& !takeOff(obj, WearLocation.LEFT_EAR, fReplace)
					&& !takeOff(obj, WearLocation.RIGHT_EAR, fReplace))
				return;

			if (getEq(WearLocation.LEFT_EAR) == null) {
				getRoom().format("{0} wears {1} on {0:S} left ear.", this, obj);
				format("You wear {0} on your left ear.", obj);
				equip(obj, WearLocation.LEFT_EAR);
				return;
			}

			if (getEq(WearLocation.RIGHT_EAR) == null) {
				getRoom()
						.format("{0} wears {1} on {0:S} right ear.", this, obj);
				format("You wear {0} on your right ear.", obj);
				equip(obj, WearLocation.RIGHT_EAR);
				return;
			}

			log.error("character already equiped on ears");
			writeln("You are already wearing two ear items.");
			return;
		}

		if (obj.getWearFlags().has(WearFlags.FACE)) {
			if (!takeOff(obj, WearLocation.FACE, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} face.", this, obj);
			format("You wear {0} on your face.", obj);
			equip(obj, WearLocation.FACE);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.FEET)) {
			if (!takeOff(obj, WearLocation.FEET, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} feet.", this, obj);
			format("You wear {0} on your feet.", obj);
			equip(obj, WearLocation.FEET);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.FINGER)) {
			if (getEq(WearLocation.LEFT_FINGER) != null
					&& getEq(WearLocation.RIGHT_FINGER) != null
					&& !takeOff(obj, WearLocation.LEFT_FINGER, fReplace)
					&& !takeOff(obj, WearLocation.RIGHT_FINGER, fReplace))
				return;

			if (getEq(WearLocation.LEFT_FINGER) == null) {
				getRoom().format("{0} wears {1} on {0:S} left finger.");
				format("You wear {0} on your left finger.", obj);
				equip(obj, WearLocation.LEFT_FINGER);
				return;
			}

			if (getEq(WearLocation.RIGHT_FINGER) == null) {
				getRoom().format("{0} wears {1} on {0:S} right finger.");
				format("You wear {0} on your right finger.", obj);
				equip(obj, WearLocation.RIGHT_FINGER);
				return;
			}

			log.error("character already equiped on fingers");
			writeln("You are already wearing two finger items.");
		}

		if (obj.getWearFlags().has(WearFlags.FLOAT)) {
			if (!takeOff(obj, WearLocation.FLOAT, fReplace))
				return;

			getRoom().format("{0} releases {1} to float next to {0:M}.", this,
					obj);
			format("You release {0} to float next to you.", obj);
			equip(obj, WearLocation.FLOAT);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.HANDS)) {
			if (!takeOff(obj, WearLocation.HANDS, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} hands.", this, obj);
			format("You wear {0} on your hands.", obj);
			equip(obj, WearLocation.HANDS);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.HEAD)) {
			if (!takeOff(obj, WearLocation.HEAD, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} head.", this, obj);
			format("You wear {0} on your head.", obj);
			equip(obj, WearLocation.HEAD);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.LEGS)) {
			if (!takeOff(obj, WearLocation.LEGS, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} legs.", this, obj);
			format("You wear {0} on your legs.", obj);
			equip(obj, WearLocation.LEGS);
		}

		if (obj.getWearFlags().has(WearFlags.LIGHT)) {
			if (!takeOff(obj, WearLocation.LIGHT, fReplace))
				return;

			getRoom().format("{0} uses {1} as a light.", this, obj);
			format("You use {0} as a light.", obj);
			equip(obj, WearLocation.LIGHT);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.NECK)) {
			if (!takeOff(obj, WearLocation.NECK, fReplace))
				return;

			getRoom().format("{0} wears {1} around {0:S} neck.", this, obj);
			format("You wear {0} around your neck.", obj);
			equip(obj, WearLocation.NECK);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.SHOULDERS)) {
			if (!takeOff(obj, WearLocation.SHOULDERS, fReplace))
				return;

			getRoom().format("{0} wears {1} on {0:S} shoulders.", this, obj);
			format("You wear {0} on your shoulders.", obj);
			equip(obj, WearLocation.SHOULDERS);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.WAIST)) {
			if (!takeOff(obj, WearLocation.WAIST, fReplace))
				return;

			getRoom().format("{0} wears {1} around {0:S} waist.", this, obj);
			format("You wear {0} around your waist.", obj);
			equip(obj, WearLocation.WAIST);
			return;
		}

		if (obj.getWearFlags().has(WearFlags.WRIST)) {
			if (getEq(WearLocation.LEFT_WRIST) != null
					&& getEq(WearLocation.RIGHT_WRIST) != null
					&& !takeOff(obj, WearLocation.LEFT_WRIST, fReplace)
					&& !takeOff(obj, WearLocation.RIGHT_WRIST, fReplace))
				return;

			if (getEq(WearLocation.LEFT_WRIST) == null) {
				getRoom().format("{0} wears {1} on {0:S} left wrist.", this,
						obj);
				format("You wear {0} on your left wrist.", obj);
				equip(obj, WearLocation.LEFT_WRIST);
				return;
			}
			if (getEq(WearLocation.RIGHT_WRIST) == null) {
				getRoom().format("{0} wears {1} on {0:S} right wrist.", this,
						obj);
				format("You wear {0} on your right wrist.", obj);
				equip(obj, WearLocation.RIGHT_WRIST);
				return;
			}
			log.error("character already equiped on wrists");
			writeln("You are already wearing two wrist items.");
			return;
		}

		if (obj.getWearFlags().has(WearFlags.SHIELD)) {
			if (!takeOff(obj, WearLocation.SHIELD, fReplace))
				return;

			Object weapon = getEq(WearLocation.WIELD);
			if (weapon != null
					&& (weapon instanceof WeaponObject)
					&& getSize() < Size.Large.getValue()
					&& ((WeaponObject) weapon)
							.hasWeaponFlag(WeaponObject.TWO_HANDED)) {
				writeln("Your hands are tied up with your weapon!");
				return;
			}
		}

		if (obj.getWearFlags().has(WearFlags.WIELD)) {

			boolean twoHanded = (obj instanceof WeaponObject)
					&& ((WeaponObject) obj)
							.hasWeaponFlag(WeaponObject.TWO_HANDED);

			if (obj.getWeight() > getStats().getMaxWieldWeight()) {
				writeln("It is too heavy for you to wield.");
				return;
			}

			if (twoHanded) {
				if (!takeOff(obj, WearLocation.WIELD, fReplace))
					return;

				if (twoHanded && getSize() < Size.Large.getValue()
						&& getEq(WearLocation.SHIELD) != null) {
					writeln("You need two hands free for that weapon.");
					return;
				}

				getRoom().format("{0} wields {1}.", this, obj);
				format("You wield {0}.", obj);

				equip(obj, WearLocation.WIELD);
			}

			if (getEq(WearLocation.WIELD) != null
					&& getEq(WearLocation.SHIELD) != null
					&& !takeOff(obj, WearLocation.WIELD, fReplace)
					&& !takeOff(obj, WearLocation.SHIELD, fReplace))
				return;

			if (getEq(WearLocation.WIELD) != null) {
				getRoom().format("{0} wields {1}.", this, obj);
				format("You wield {0}.", obj);
				equip(obj, WearLocation.WIELD);
				return;
			}

			if (getEq(WearLocation.SHIELD) != null) {
				getRoom().format("{0} wields {1} in {0:S} offhand.", this, obj);
				format("You wield {0} in your offhand.", obj);
				equip(obj, WearLocation.SHIELD);
				return;
			}

			writeln("You already using both hands.");
			log.error("character already wielding.");
			return;
		}
	}

	public void format(String fmt, java.lang.Object... args) {
		String str = Format.parse(fmt, args);

		writeln(str);
	}

	@Transient
	public abstract Attack getAttack();

	@Column(name = "alignment")
	public int getAlignment() {
		return alignment;
	}

	@Embedded
	public Attributes getBaseAttributes() {
		return baseAttributes;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "carriedBy", targetEntity = Object.class)
	public Set<Object> getCarrying() {
		return carrying;
	}

	@Transient
	public abstract int getDamroll();

	@Column(name = "description", columnDefinition = "text")
	public String getDescription() {
		return this.description;
	}

	@Column(name = "ethos")
	public int getEthos() {
		return ethos;
	}

	@Override
	@Transient
	public String getExtraDescr(String keyword) {
		return extraDescr.get(keyword);
	}

	@ElementCollection
	@CollectionTable(name = "char_extra_descr", joinColumns = @JoinColumn(name = "char_id"))
	@MapKeyColumn(name = "keyword")
	@Column(name = "value", columnDefinition = "text")
	public Map<String, String> getExtraDescriptions() {
		return extraDescr;
	}

	@Transient
	public Character getFighting() {
		return fighting;
	}

	@Column(name = "hitroll", nullable = false)
	public int getHitroll() {
		return hitroll;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "char_id", unique = true, nullable = false)
	@Override
	public Long getId() {
		return id;
	}

	@Override
	@Column(name = "level", nullable = false)
	public int getLevel() {
		return this.level;
	}

	@Transient
	public abstract String getLongDescr();

	@Embedded
	public Money getMoney() {
		return money;
	}

	@Override
	@Column(name = "name", nullable = false, length = 75)
	public String getName() {
		return this.name;
	}

	/**
	 * @return the position
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "position", columnDefinition = "tinyint", nullable = false)
	public Position getPosition() {
		return position;
	}

	@ManyToOne(targetEntity = Race.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "race_id")
	public Race getRace() {
		return this.race;
	}

	public int getResistance(DamType type) {
		return attributes.getResistance(type);
	}

	@Transient
	public Room getRoom() {
		return room;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "sex", columnDefinition = "tinyint", nullable = false)
	public Sex getSex() {
		return sex;
	}

	@Transient
	public abstract String getShortDescr();

	@Column(name = "size")
	public float getSize() {
		return size;
	}

	@Column(name = "affected_by")
	public Flag getAffectedBy() {
		return affectedBy;
	}

	@Transient
	public Stats getStats() {
		return attributes.getStats();
	}

	@Transient
	public abstract int getThac0();

	@Transient
	public abstract int getThac32();

	@Transient
	public Vitals getVitals() {
		return currentVitals;
	}

	@Column(name = "weight", nullable = false)
	public int getWeight() {
		return weight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getId() ^ (getId() >>> 32));
		return result;
	}

	@Transient
	public boolean isPlayer() {
		return this instanceof Player;
	}

	public void page(java.lang.Object arg) {
		if (isPlayer()) {
			try {
				toPlayer().getTerminal().page(arg);
			} catch (IOException ex) {
				log.error(ex.getMessage());
			}
		}
	}

	@Override
	public void removeAffect(Affect af) {
		affects.remove(af);
	}

	public void setAlignment(int value) {
		alignment = value;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEthos(int value) {
		ethos = value;
	}

	@Override
	public void setExtraDescr(String keyword, String value) {
		extraDescr.put(keyword, value);
	}

	public void setExtraDescriptions(Map<String, String> value) {
		extraDescr = value;
	}

	public void setFighting(Character value) {
		fighting = value;
	}

	public void setHitroll(int value) {
		hitroll = value;
	}

	public abstract void setDamroll(int value);

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public void setRoom(Room room) {
		this.room = room;
		room.getCharacters().add(this);
	}

	public void setSex(Sex value) {
		this.sex = value;
	}

	public abstract void setShortDescr(String value);

	public void setSize(float value) {
		size = value;
	}

	public void setWeight(int value) {
		weight = value;
	}

	public Player toPlayer() {
		try {
			return (Player) this;
		} catch (ClassCastException ex) {
			return null;
		}
	}

	@Override
	public String toString(String format) {

		if (Jmud.isNullOrEmpty(format))
			return getShortDescr();

		switch (format.charAt(0)) {
		case 'n':
			return getName();
		case 'N':
			return getShortDescr();
		case 'L':
			return getLongDescr();
		case 'M':
			return getSex().getHimHer();
		case 'S':
			return getSex().getHisHers();
		case 'E':
			return getSex().getHeShe();
		case 'Z':
			return "[" + getId() + "," + getShortDescr() + "]";
		default:
			return toString();
		}
	}

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = Affect.class)
	@JoinTable(name = "char_affect", joinColumns = @JoinColumn(name = "char_id"), inverseJoinColumns = @JoinColumn(name = "affect_id"))
	public Set<Affect> getAffects() {
		return Collections.unmodifiableSet(affects);
	}

	@Override
	public Affect findAffect(long type) {
		for (Affect a : getAffects()) {
			if (a.getExtra() == type)
				return a;
		}
		return null;
	}

	protected void setAffectedBy(Flag value) {
		affectedBy = value;
	}

	protected void setAffects(Set<Affect> value) {
		affects = value;
	}

	protected void setBaseAttributes(Attributes value) {
		baseAttributes = value;
		try {
			attributes = value.clone();
		} catch (Exception ex) {
			log.error("Attributes not cloneable");
		}
	}

	protected void setCarrying(Set<Object> value) {
		carrying = value;
	}

	protected void setMoney(Money value) {
		money = value;
	}

	protected void setVitals(Vitals value) {
		currentVitals = value;
	}

	public boolean savesSpell(int level, DamType type) {
		int save;

		save = 50 + (getLevel() - level) * 5 - getResistance(DamType.MAGIC) * 2;
		if (getAffectedBy().has(Affect.Berserk))
			save += getLevel() / 2;

		if (this.isPlayer() && this.toPlayer().getProfession().manaUser())
			save = 9 * save / 10;
		save = Jmud.range(5, save, 95);
		return Jmud.percent() < save;
	}
}
