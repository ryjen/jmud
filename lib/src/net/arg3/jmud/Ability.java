package net.arg3.jmud;

// default package
// Generated 12-Sep-2009 1:43:25 AM by Hibernate Tools 3.2.5.Beta
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IEnvironmental;
import net.arg3.jmud.interfaces.IExecutable;

/**
 * Ability generated by hbm2java
 */
@Table(name = "ability")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
public abstract class Ability implements Serializable, IExecutable,
		IDataObject<Integer> {

	int id;

	public enum TargetType {
		Character, Object, Room
	}

	private static final long serialVersionUID = 1L;
	private static Set<Ability> list = null;

	public static Set<Ability> getList() {
		if (list == null) {
			// list = Collections.synchronizedSet(new HashSet<Ability>());
			list = new HashSet<Ability>();
			list.addAll(Persistance.getAll(Ability.class));
		}
		return list;
	}

	private String name;

	private int level;

	private TargetType targetType;

	@Column(name = "level")
	public int getLevel() {
		return this.level;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ability_id")
	public Integer getId() {
		return id;
	}

	@Override
	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public IEnvironmental getTarget(Character ch, String argument) {

		IEnvironmental target = null;

		switch (getTargetType()) {
		case Character:

			target = ch.getRoom().getChar(argument);

			break;
		case Object:
			target = ch.getRoom().getObj(argument);

			break;
		case Room:
			target = ch.getRoom();
			break;
		}

		return target;
	}

	@Column(name = "target")
	@Enumerated(EnumType.ORDINAL)
	public TargetType getTargetType() {
		return targetType;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setId(Integer value) {
		id = value;
	}

	public void setTargetType(TargetType value) {
		targetType = value;
	}

	@Override
	public int compareTo(IDataObject<Integer> o) {
		return getId().compareTo(o.getId());
	}
}