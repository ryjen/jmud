package net.arg3.jmud.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.arg3.jmud.Jmud;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.Stats;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

// default package
// Generated 12-Sep-2009 1:43:25 AM by Hibernate Tools 3.2.5.Beta
/**
 * Profession generated by hbm2java
 */
@Entity
@Table(name = "profession")
public class Profession implements IDataObject<Integer>, IFormatible {

	private static final long serialVersionUID = 1L;
	private static Set<Profession> list = null;

	public static synchronized Set<Profession> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Profession>());
			list = new HashSet<Profession>();
			list.addAll(Persistance.getAll(Profession.class));
			if (list.size() == 0) {
				list.add(createDefaultProfession());
			}
		}
		return list;
	}

	private static Profession createDefaultProfession() {
		Profession prof = new Profession();
		prof.setName("mage");
		prof.setThac0(0);
		prof.setThac32(10);
		prof.getBaseStats().setCon(10);
		prof.getBaseStats().setDex(12);
		prof.getBaseStats().setInt(15);
		prof.getBaseStats().setLuck(12);
		prof.getBaseStats().setStr(10);
		prof.getBaseStats().setWis(13);
		Persistance.save(prof);

		return prof;
	}

	private int id;
	private String name;

	private int thac0;

	private int thac32;

	private Stats baseStats;

	private boolean manaUser;

	public Profession() {
		baseStats = new Stats();
	}

	public Profession(String name) {
		super();
		this.name = name;
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
		if (!(obj instanceof Profession)) {
			return false;
		}
		Profession other = (Profession) obj;
		if (getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Embedded
	public Stats getBaseStats() {
		return baseStats;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "profession_id", unique = true, nullable = false, columnDefinition = "int")
	@Override
	public Integer getId() {
		return this.id;
	}

	@Column(name = "name", nullable = false, length = 45)
	public String getName() {
		return this.name;
	}

	@Column(name = "thac0", nullable = false, columnDefinition = "smallint")
	public int getThac0() {
		return thac0;
	}

	@Column(name = "thac32", nullable = false, columnDefinition = "smallint")
	public int getThac32() {
		return thac32;
	}

	@Column(name = "mana_user")
	public boolean manaUser() {
		return manaUser;
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

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setThac0(int value) {
		thac0 = value;
	}

	public void setThac32(int value) {
		thac32 = value;
	}

	public void setManaUser(boolean value) {
		manaUser = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
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
			return "[" + getId() + "," + getName() + "]";
		default:
			return toString();
		}
	}

	protected void setBaseStats(Stats stats) {
		baseStats = stats;
	}
}
