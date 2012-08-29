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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "continent")
public class Continent implements IDataObject<Integer>, IFormatible {

	private static final long serialVersionUID = 1L;
	private static Set<Continent> list = null;

	public static Continent createDefault() {
		if (getList().size() > 0) {
			return getList().iterator().next();
		}

		Continent c = new Continent();

		c.setName("Big Mountain Plains");

		Persistance.save(c);

		getList().add(c);

		return c;
	}

	public static Set<Continent> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Continent>());
			list = new HashSet<Continent>();
			list.addAll(Persistance.getAll(Continent.class));
		}
		return list;
	}

	private int id;
	private String name;
	private Set<Area> areas;

	@Override
	public int compareTo(IDataObject<Integer> arg0) {

		return getId().compareTo(arg0.getId());
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
		if (!(obj instanceof Continent)) {
			return false;
		}
		Continent other = (Continent) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	/**
	 * @return the areas
	 */
	@OneToMany(cascade = CascadeType.ALL, targetEntity = Area.class, mappedBy = "continent", fetch = FetchType.EAGER)
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Set<Area> getAreas() {
		return areas;
	}

	/**
	 * @return the id
	 */
	@Id
	@Column(name = "continent_id", columnDefinition = "int")
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

	public void setAreas(Set<Area> value) {
		areas = value;

	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
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

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return getName();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + getId() + "," + getName() + "]";
		default:
			return toString();
		}
	}
}
