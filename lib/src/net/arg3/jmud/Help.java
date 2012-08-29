/**
 * Project: jMUD
 * Date: 2009-09-07
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "help")
public class Help implements IDataObject<Integer>, IFormatible {

	private static final long serialVersionUID = 1L;
	private static Set<Help> list = null;

	public static Help find(String keyword) {
		if (Jmud.isNullOrEmpty(keyword)) {
			return null;
		}

		for (Help h : getList()) {
			for (String k : h.getKeywords()) {
				if (Jmud.isPrefix(k, keyword)) {
					return h;
				}
			}
		}
		return null;
	}

	public static Set<Help> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Help>());
			list = new HashSet<Help>();
			list.addAll(Persistance.getAll(Help.class));
		}
		return list;
	}

	private String text;
	private int id;
	private String syntax;
	private Set<String> keywords;
	private int level;

	public Help() {
		keywords = new HashSet<String>();
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
		if (!(obj instanceof Help)) {
			return false;
		}
		Help other = (Help) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "help_id", columnDefinition = "int")
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @return a read-only list of keywords
	 */
	// @OneToMany(cascade = CascadeType.ALL, targetEntity = HelpKeyword.class,
	// mappedBy = "id.help", fetch = FetchType.EAGER)
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@ElementCollection
	@CollectionTable(name = "help_keyword", joinColumns = @JoinColumn(name = "help_id"))
	@Column(name = "value")
	public Set<String> getKeywords() {
		return keywords;
	}

	@Column(name = "level")
	public int getLevel() {
		return level;
	}

	/**
	 * @return the syntax
	 */
	@Column(name = "syntax")
	public String getSyntax() {
		return syntax;
	}

	/**
	 * @return the text
	 */
	@Column(name = "text", columnDefinition = "text")
	public String getText() {
		return text;
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

	public void setLevel(int value) {
		this.level = value;
	}

	/**
	 * @param syntax
	 *            the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String k : keywords) {
			sb.append("," + k);
		}
		if (sb.length() > 0) {
			sb.delete(0, 1);
		}
		return sb.toString();
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return toString();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + getId() + "," + toString() + "]";
		default:
			return toString();
		}
	}

	protected void setKeywords(Set<String> value) {
		keywords = value;
	}
}
