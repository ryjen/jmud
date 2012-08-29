/**
 * Project: jMUD
 * Date: 2009-09-09
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.enums.Sector;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IEnvironmental;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "room")
public class Room implements IDataObject<Long>, IFormatible, IEnvironmental {

	private static final long serialVersionUID = 1L;
	private static Set<Room> list = null;

	@FlagValue
	public static final long SAFE = (1L << 0);

	public static Room getDefault() {
		if (getList().size() > 0) {
			return getList().iterator().next();
		}

		Room room = new Room();

		Area a = Area.createDefault();
		room.setArea(a);
		a.addRoom(room);

		room.setDescription("You are floating in darkness...");

		room.setName("The Void");

		Persistance.save(room);

		getList().add(room);

		return room;
	}

	public static Set<Room> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Room>());
			list = new HashSet<Room>();
		}
		return list;
	}

	private long id;
	private String name;
	private String description;
	private Area area;
	private Map<Direction, Exit> exits;
	private Map<String, String> extraDescr;
	private Flag flags;
	private final Set<Character> characters;
	private final Set<Object> objects;
	private Sector sector;
	private Reset reset;
	private int level;
	private Set<Affect> affects;

	public Room() {
		// characters = Collections.synchronizedSet(new HashSet<Character>());
		// objects = Collections.synchronizedSet(new HashSet<Object>());
		characters = new HashSet<Character>();
		objects = new HashSet<Object>();
		exits = new HashMap<Direction, Exit>();
		affects = new HashSet<Affect>();
		flags = new Flag();
		extraDescr = new HashMap<String, String>();
	}

	@Override
	public void addAffect(Affect paf) {
		affects.add(paf);
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
		if (!(obj instanceof Room)) {
			return false;
		}
		Room other = (Room) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	public void format(String str, java.lang.Object... args) {
		String buf = Format.parse(str, args);

		for (Character ch : getCharacters()) {
			if (args.length > 0 && ch == args[0])
				continue;

			if (args.length > 1 && ch == args[1])
				continue;

			ch.writeln(buf);
		}
	}

	/**
	 * @return the area
	 */
	@ManyToOne(targetEntity = Area.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "area_id")
	public Area getArea() {
		return area;
	}

	public Character getChar(String argument) {
		for (Character ch : getCharacters()) {
			if (Jmud.isName(ch.getName(), argument))
				return ch;
		}

		return null;
	}

	@Transient
	public Set<Character> getCharacters() {
		return characters;
	}

	/**
	 * @return the description
	 */
	@Column(name = "description", columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public Exit getExit(Direction dir) {
		return exits.get(dir);
	}

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Exit.class, mappedBy = "id.room", fetch = FetchType.EAGER)
	@MapKey(name = "id.direction")
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Map<Direction, Exit> getExits() {
		return exits;
	}

	@Override
	@Transient
	public String getExtraDescr(String keyword) {
		return extraDescr.get(keyword);
	}

	@ElementCollection
	@CollectionTable(name = "room_extra_descr", joinColumns = @JoinColumn(name = "room_id"))
	@MapKeyColumn(name = "keyword")
	@Column(name = "value", columnDefinition = "text")
	public Map<String, String> getExtraDescriptions() {
		return extraDescr;
	}

	@Column(name = "flags")
	public Flag getFlags() {
		return flags;
	}

	/**
	 * @return the id
	 */
	@Id
	@Column(name = "room_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Override
	public Long getId() {
		return id;
	}

	@Override
	@Column(name = "level", nullable = false)
	public int getLevel() {
		return level;
	}

	/**
	 * @return the name
	 */
	@Override
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public Object getObj(String argument) {
		for (Object obj : getObjects()) {
			if (Jmud.isName(obj.getName(), argument))
				return obj;
		}
		return null;
	}

	@Transient
	public Set<Object> getObjects() {
		return objects;
	}

	@ManyToOne(targetEntity = Reset.class, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "reset_id")
	public Reset getResetScript() {
		return reset;
	}

	@Column(name = "sector", columnDefinition = "tinyint")
	@Enumerated(EnumType.ORDINAL)
	public Sector getSector() {
		return sector;
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
	public void removeAffect(Affect paf) {
		affects.remove(paf);
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setExits(Map<Direction, Exit> value) {
		exits = value;
	}

	@Override
	public void setExtraDescr(String keyword, String value) {
		extraDescr.put(keyword, value);
	}

	public void setExtraDescriptions(Map<String, String> value) {
		extraDescr = value;
	}

	@Override
	public void setId(Long value) {
		id = value;
	}

	@Override
	public void setLevel(int value) {
		level = value;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setResetScript(Reset value) {
		reset = value;
	}

	public void setSector(Sector value) {
		sector = value;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return getName();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + id + "," + name + "]";
		case 'D':
			return getDescription();
		default:
			return toString();
		}
	}

	@ManyToMany(targetEntity = Affect.class)
	@JoinTable(name = "room_affect", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "affect_id"))
	public Set<Affect> getAffects() {
		return Collections.unmodifiableSet(affects);
	}

	protected void setAffects(Set<Affect> value) {
		affects = value;
	}

	protected void setFlags(Flag bits) {
		flags = bits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.interfaces.IAffectable#findAffect(net.arg3.jmud.enums.
	 * SpellAffects)
	 */
	@Override
	public Affect findAffect(long type) {
		for (Affect paf : getAffects()) {
			if (paf.getExtra() == type)
				return paf;
		}
		return null;
	}

}
