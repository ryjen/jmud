package net.arg3.jmud;

// default package
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import net.arg3.jmud.enums.Size;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;

/**
 * Race generated by hbm2java
 */
@Entity
@Table(name = "race")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
public abstract class Race implements IDataObject<Integer>, IFormatible {

	protected static final long serialVersionUID = 1L;
	private static Set<Race> list;

	public static Set<Race> getList() {
		if (list == null) {
			list = new HashSet<Race>();
			list.addAll(Persistance.getAll(Race.class));
			if (list.size() == 0)
				createDefaultRaces();
		}
		return list;
	}

	private static void createDefaultRaces() {
		Transaction tx = null;
		try {
			Class<?>[] races = Jmud.getClasses("net.arg3.jmud.races");

			Session s = Persistance.getSession();
			tx = s.beginTransaction();
			for (Class<?> cls : races) {

				Constructor<?> ct = cls.getConstructor();
				java.lang.Object race = ct.newInstance();
				s.saveOrUpdate(race);
			}
			tx.commit();
		} catch (Exception ex) {
			if (tx != null)
				tx.rollback();
			LoggerFactory.getLogger(Race.class).error(
					"unable to create default races", ex);
		}

	}

	public static Race lookup(String arg) {
		for (Race race : getList()) {
			if (Jmud.isPrefix(race.getName(), arg))
				return race;
		}
		return null;
	}

	private int raceId;

	protected String name;

	protected Size size;

	@Override
	public int compareTo(IDataObject<Integer> o) {
		return getId() - o.getId();
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
		if (!(obj instanceof Race)) {
			return false;
		}
		Race other = (Race) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "race_id", unique = true, nullable = false, columnDefinition = "int")
	@Override
	public Integer getId() {
		return this.raceId;
	}

	@Column(name = "name", length = 45, updatable = false, insertable = false)
	public abstract String getName();

	@SuppressWarnings("unused")
	private void setName(String value) {
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "size", updatable = false, insertable = false)
	public abstract Size getSize();

	@SuppressWarnings("unused")
	private void setSize(Size value) {
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
		raceId = id;
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
