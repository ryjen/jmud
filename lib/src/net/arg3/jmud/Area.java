/**
 * Project: jMUD
 * Date: 2009-09-09
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "area")
public class Area implements IDataObject<Integer>, IFormatible {

	private static final Logger log = LoggerFactory.getLogger(Area.class);
	private static final long serialVersionUID = 1L;
	private static Set<Area> list = null;

	public static Area createDefault() {
		if (getList().size() > 0) {
			return getList().iterator().next();
		}

		Area area = new Area();

		Continent c = Continent.createDefault();
		area.setContinent(c);
		c.getAreas().add(area);

		area.setName("Limbo");

		Persistance.save(area);
		getList().add(area);

		return area;
	}

	public static Set<Area> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Area>());
			list = new HashSet<Area>();
			list.addAll(Persistance.getAll(Area.class));

			for (Area area : list) {
				for (Room room : area.getRooms()) {
					Room.getList().add(room);
				}
			}
		}
		return list;
	}

	private int id;
	private String name;
	private Continent continent;
	private Set<Room> rooms;
	private Flag flags;
	private int age;
	private String credits;
	private Set<Object> objects;
	private Set<NonPlayer> npcs;

	public Area() {
		flags = new Flag();
		rooms = new HashSet<Room>();
		objects = new HashSet<Object>();
		npcs = new HashSet<NonPlayer>();
	}

	/**
	 * @param room
	 *            the room to add
	 */
	public void addRoom(Room room) {
		rooms.add(room);
	}

	@Override
	public int compareTo(IDataObject<Integer> o) {
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
		if (!(obj instanceof Area)) {
			return false;
		}
		Area other = (Area) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	@Transient
	public int getAge() {
		return age;
	}

	/**
	 * @return the continent
	 */
	@ManyToOne(targetEntity = Continent.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "continent_id")
	public Continent getContinent() {
		return continent;
	}

	@Column(name = "credits")
	public String getCredits() {
		return credits;
	}

	@Column(name = "flags")
	public Flag getFlags() {
		return flags;
	}

	/**
	 * @return the id
	 */
	@Id
	@Column(name = "area_id", columnDefinition = "int")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	@Column(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * @return the rooms
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "area", targetEntity = Room.class, fetch = FetchType.EAGER)
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Set<Room> getRooms() {
		return rooms;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "area", targetEntity = Object.class, fetch = FetchType.EAGER)
	public Set<Object> getObjects() {
		return objects;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "area", targetEntity = NonPlayer.class, fetch = FetchType.EAGER)
	public Set<NonPlayer> getNpcs() {
		return npcs;
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
		result = prime * result + getId();
		return result;
	}

	/**
	 * @param room
	 *            the room to remove
	 */
	public void removeRoom(Room room) {
		rooms.remove(room);
	}

	public void setAge(int value) {
		age = value;
	}

	/**
	 * @param continent
	 *            the continent to set
	 */
	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	public void setCredits(String value) {
		credits = value;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	protected void setRooms(Set<Room> value) {
		rooms = value;
	}

	protected void setObjects(Set<Object> value) {
		objects = value;
	}

	protected void setNpcs(Set<NonPlayer> value) {
		npcs = value;
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return getName();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + id + "," + name + "]";
		default:
			return toString();
		}
	}

	public void update() {
		/*
		 * int age = getAge() + 1;
		 * 
		 * if (age < 3) { setAge(age); return; }
		 * 
		 * if (age >= 31) {
		 */
		setAge(Jmud.Rand.nextInt(3));

		for (Room room : getRooms()) {
			Reset reset = room.getResetScript();
			if (reset != null) {
				reset.execute();
			}
		}
		log.debug("Updated " + getName());
		// }
		return;
	}

	protected void setFlags(Flag bits) {
		flags = bits;
	}
}
