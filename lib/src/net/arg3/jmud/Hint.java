/**
 * Project: jmudlib
 * Date: 2009-09-23
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
@Table(name = "hint")
public class Hint implements IDataObject<Integer>, IFormatible {

	private static final long serialVersionUID = 1L;
	public static final List<Hint> list = Collections
			.synchronizedList(new ArrayList<Hint>());
	private int id;
	private String value;

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
		if (!(obj instanceof Hint)) {
			return false;
		}
		Hint other = (Hint) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	/**
	 * @return the id
	 */
	@Override
	@Id
	@Column(name = "hint_id", columnDefinition = "int")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	/**
	 * @return the value
	 */
	@Column(name = "value")
	public String getValue() {
		return value;
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

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return getValue();

		switch (format.charAt(0)) {
		case 'Z':
			return "Hint: [" + getId() + "]";
		default:
			return toString();
		}
	}
}
