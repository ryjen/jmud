/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud.world
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
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

import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.WearLocation;
import net.arg3.jmud.interfaces.IAffectable;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IEnvironmental;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "object")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Object implements IDataObject<Long>, IFormatible,
		IAffectable, IEnvironmental, Cloneable {

	@FlagValue
	public static final long INVIS = (1L << 0);
	@FlagValue
	public static final long MAGIC = (1L << 1);
	@FlagValue
	public static final long NODROP = (1L << 2);
	@FlagValue
	public static final long NOREMOVE = (1L << 3);
	@FlagValue
	public static final long ROTDEATH = (1L << 4);
	@FlagValue
	public static final long BURNPROOF = (1L << 5);
	@FlagValue
	public static final long ANTIEVIL = (1L << 6);
	@FlagValue
	public static final long ANTINEUTRAL = (1L << 7);
	@FlagValue
	public static final long ANTIGOOD = (1L << 8);

	private static final long serialVersionUID = 1L;
	private static Set<Object> list = null;

	public static Set<Object> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Object>());
			list = new HashSet<Object>();
		}
		return list;
	}

	private long id;
	private String name;
	private String shortDescr;
	private String longDescr;
	private short weight;
	private Money cost;
	private Flag flags;
	private Area area;
	private Character carriedBy;
	private WearLocation wearLocation;
	private Set<Object> contents;
	private Object inside;
	private Room room;
	private int level;
	private int condition;
	private Set<Affect> affects;
	private Map<String, String> extraDescr;
	private Flag wearFlags;
	protected List<java.lang.Object> values;

	public Object() {
		getList().add(this);
		flags = new Flag();
		wearFlags = new Flag();
		extraDescr = new HashMap<String, String>();
		affects = new HashSet<Affect>();
		cost = new Money();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		Object clone = (Object) super.clone();

		clone.cost = cost.clone();
		clone.flags = flags.clone();
		for (Object c : contents) {
			clone.contents.add(c.clone());
		}
		for (Affect a : affects) {
			clone.affects.add(a.clone());
		}
		clone.wearFlags = wearFlags.clone();
		for (java.lang.Object v : values)
			clone.values.add(v);

		return clone;

	}

	@Override
	public void addAffect(Affect af) {
		affects.add(af);
	}

	@Override
	public int compareTo(IDataObject<Long> o) {
		return getId().compareTo(o.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Item#equals(java.lang.Item)
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Object)) {
			return false;
		}
		Object other = (Object) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	/**
	 * @return the area
	 */
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Area.class)
	@JoinColumn(name = "area_id")
	public Area getArea() {
		return area;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true, targetEntity = Character.class)
	@JoinColumn(name = "carried_by")
	public Character getCarriedBy() {
		return carriedBy;
	}

	@Column(name = "`condition`")
	public int getCondition() {
		return condition;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "inside", targetEntity = Object.class, fetch = FetchType.EAGER)
	public Set<Object> getContents() {
		return contents;
	}

	/**
	 * @return the cost
	 */
	@Column(name = "cost")
	public Money getCost() {
		return cost;
	}

	@Override
	@Transient
	public String getExtraDescr(String keyword) {
		return extraDescr.get(keyword);
	}

	@ElementCollection
	@CollectionTable(name = "object_extra_descr", joinColumns = @JoinColumn(name = "object_id"))
	@MapKeyColumn(name = "keyword")
	@Column(name = "value", columnDefinition = "text")
	public Map<String, String> getExtraDescriptions() {
		return extraDescr;
	}

	/**
	 * @return the flags
	 */
	@Column(name = "flags")
	public Flag getFlags() {
		return flags;
	}

	@Column(name = "wear_flags")
	public Flag getWearFlags() {
		return wearFlags;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "object_id")
	@Override
	public Long getId() {
		return id;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true, targetEntity = Object.class)
	@JoinColumn(name = "inside_id")
	public Object getInside() {
		return inside;
	}

	@Override
	@Column(name = "level")
	public int getLevel() {
		return level;
	}

	/**
	 * @return the longDescr
	 */
	@Column(name = "long_descr")
	public String getLongDescr() {
		return longDescr;
	}

	/**
	 * @return the name
	 */
	@Override
	@Column(name = "name", length = 155)
	public String getName() {
		return name;
	}

	@Transient
	public Room getRoom() {
		return room;
	}

	/**
	 * @return the shortDescr
	 */
	@Column(name = "short_descr")
	public String getShortDescr() {
		return shortDescr;
	}

	@Column(name = "`values`", columnDefinition = "blob")
	protected abstract java.lang.Object[] getValues();

	/**
	 * @return the wearLocation
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "wear_loc", columnDefinition = "tinyint")
	public WearLocation getWearLocation() {
		return wearLocation;
	}

	/**
	 * @return the weight
	 */
	@Column(name = "weight")
	public short getWeight() {
		return weight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Item#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getId() ^ (getId() >>> 32));
		return result;
	}

	@Override
	public void removeAffect(Affect af) {
		affects.remove(af);
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	public void setCarriedBy(Character value) {
		this.carriedBy = value;
	}

	public void setCondition(int value) {
		this.condition = value;
	}

	public void setContents(Set<Object> value) {
		contents = value;
	}

	/**
	 * @param cost
	 *            the cost to set
	 */
	protected void setCost(Money cost) {
		this.cost = cost;
	}

	@Override
	public void setExtraDescr(String keyword, String value) {
		extraDescr.put(keyword, value);
	}

	protected void setExtraDescriptions(Map<String, String> value) {
		extraDescr = value;
	}

	/**
	 * @param flags
	 *            the flags to set
	 */
	protected void setFlags(Flag flags) {
		this.flags = flags;
	}

	protected void setWearFlags(Flag flags) {
		this.wearFlags = flags;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setInside(Object value) {
		inside = value;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param longDescr
	 *            the longDescr to set
	 */
	public void setLongDescr(String longDescr) {
		this.longDescr = longDescr;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setRoom(Room value) {
		this.room = value;
		this.room.getObjects().add(this);
	}

	/**
	 * @param shortDescr
	 *            the shortDescr to set
	 */
	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}

	protected abstract void setValues(java.lang.Object[] values);

	/**
	 * @param wearLocation
	 *            the wearLocation to set
	 */
	public void setWearLocation(WearLocation wearLocation) {
		this.wearLocation = wearLocation;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(short weight) {
		this.weight = weight;
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
		case 'Z':
			return "[" + getId() + "," + getShortDescr() + "]";
		default:
			return toString();
		}
	}

	@ManyToMany(targetEntity = Affect.class)
	@JoinTable(name = "object_affect", joinColumns = @JoinColumn(name = "object_id"), inverseJoinColumns = @JoinColumn(name = "affect_id"))
	public Set<Affect> getAffects() {
		return Collections.unmodifiableSet(affects);
	}

	protected void setAffects(Set<Affect> value) {
		affects = value;
	}

	@Override
	public Affect findAffect(long type) {
		for (Affect paf : getAffects()) {
			if (paf.getExtra() == type)
				return paf;
		}
		return null;
	}
}
