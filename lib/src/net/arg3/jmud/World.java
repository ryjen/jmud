/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "world")
public class World implements IDataObject<Integer>, IFormatible {

	private static final long serialVersionUID = 1L;
	private static World instance = null;
	public static final int MAX_LEVEL = 101;
	public static final int IMMORTAL = 101;
	public static final int HERO = 100;

	public static World getInstance() {
		if (instance == null) {
			instance = Persistance.queryOne(World.class);
		}
		return instance;
	}

	private String name;
	private long flags;
	private int id;

	private World() {

	}

	@Override
	public int compareTo(IDataObject<Integer> arg0) {
		return getId().compareTo(arg0.getId());
	}

	/**
	 * @return the flags
	 */
	@Column(name = "flags")
	public long getFlags() {
		return flags;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "world_id")
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
	 * @param flags
	 *            the flags to set
	 */
	public void setFlags(long flags) {
		this.flags = flags;
	}

	public void setId(int value) {
		id = value;
	}

	@Override
	public void setId(Integer value) {
		id = value;
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
