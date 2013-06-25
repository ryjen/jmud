/**
 * Project: jMUD
 * Date: 2009-09-12
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.Attack;
import net.arg3.jmud.Dice;
import net.arg3.jmud.Flag;
import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.Position;
import net.arg3.jmud.interfaces.IDataObject;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Proxy;
import org.hibernate.event.PostLoadEvent;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "non_player")
@Proxy(lazy = false)
@OnDelete(action = OnDeleteAction.CASCADE)
public class NonPlayer extends Character implements IDataObject<Long>,
		Cloneable {

	@FlagValue
	public static final long SENTINEL = (1L << 0);
	@FlagValue
	public static final long AGGRESSIVE = (1L << 1);
	@FlagValue
	public static final long STAY_AREA = (1L << 2);
	@FlagValue
	public static final long WIMPY = (1L << 3);
	@FlagValue
	public static final long SCAVENGER = (1L << 4);

	private static final long serialVersionUID = 1L;
	private Area area;
	private String shortDescr;
	private String longDescr;
	private Flag flags;
	private int thac0;
	private int thac32;
	private Position defaultPos;
	private Shop shop;
	private Dice damDice;
	private Dice hitDice;
	private Dice manaDice;
	private Attack attack;

	public NonPlayer() {
		damDice = new Dice();
		hitDice = new Dice();
		manaDice = new Dice();
		defaultPos = Position.STANDING;
		flags = new Flag();
	}

	@Override
	protected NonPlayer clone() throws CloneNotSupportedException {
		NonPlayer npc = (NonPlayer) super.clone();

		npc.flags = flags.clone();
		npc.damDice = damDice.clone();
		npc.hitDice = hitDice.clone();
		npc.manaDice = manaDice.clone();

		return npc;
	}

	/**
	 * @return the area
	 */
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Area.class)
	@JoinColumn(name = "area_id")
	public Area getArea() {
		return area;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "type", column = @Column(name = "dam_value")),
			@AttributeOverride(name = "number", column = @Column(name = "dam_num")),
			@AttributeOverride(name = "bonus", column = @Column(name = "dam_bonus")) })
	public Dice getDamDice() {
		return damDice;
	}

	@Override
	@Transient
	public int getDamroll() {
		return damDice.getBonus();
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "default_pos", columnDefinition = "tinyint", nullable = false)
	public Position getDefaultPosition() {
		return defaultPos;
	}

	@Column(name = "flags", nullable = false)
	public Flag getFlags() {
		return flags;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "type", column = @Column(name = "hit_value")),
			@AttributeOverride(name = "number", column = @Column(name = "hit_num")),
			@AttributeOverride(name = "bonus", column = @Column(name = "hit_bonus")) })
	public Dice getHitDice() {
		return hitDice;
	}

	/**
	 * @return the longDescr
	 */
	@Override
	@Column(name = "long_descr", nullable = false)
	public String getLongDescr() {
		return longDescr;
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "type", column = @Column(name = "mana_value")),
			@AttributeOverride(name = "number", column = @Column(name = "mana_num")),
			@AttributeOverride(name = "bonus", column = @Column(name = "mana_bonus")) })
	public Dice getManaDice() {
		return manaDice;
	}

	@OneToOne(fetch = FetchType.EAGER, targetEntity = Shop.class)
	@JoinColumn(name = "shop_id")
	public Shop getShop() {
		return shop;
	}

	/**
	 * @return the shortDescr
	 */
	@Column(name = "short_descr", nullable = false, length = 155)
	@Override
	public String getShortDescr() {
		return shortDescr;
	}

	@Override
	@Column(name = "thac0", columnDefinition = "int not null default 1")
	public int getThac0() {
		return thac0;
	}

	@Override
	@Column(name = "thac32", columnDefinition = "int not null default 8")
	public int getThac32() {
		return thac32;
	}

	@Override
	@Embedded
	public Attack getAttack() {
		return attack;
	}

	@Column
	@Override
	public void onPostLoad(PostLoadEvent arg0) {
		rollVitals();
	}

	public void rollVitals() {
		int value = getHitDice().roll();
		getVitals().setHit(value);
		getVitals().setMaxHit(value);
		value = getManaDice().roll();
		getVitals().setMana(value);
		getVitals().setMaxMana(value);
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	public void setDamDice(Dice value) {
		damDice = value;
	}

	public void setDefaultPosition(Position value) {
		defaultPos = value;
	}

	public void setHitDice(Dice value) {
		hitDice = value;
	}

	/**
	 * @param longDescr
	 *            the longDescr to set
	 */
	public void setLongDescr(String longDescr) {
		this.longDescr = longDescr;
	}

	public void setManaDice(Dice value) {
		manaDice = value;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	/**
	 * @param shortDescr
	 *            the shortDescr to set
	 */
	@Override
	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}

	public void setThac0(int value) {
		thac0 = value;
	}

	public void setAttack(Attack value) {
		attack = value;
	}

	public void setThac32(int value) {
		thac32 = value;
	}

	@Override
	public void setDamroll(int value) {
		damDice.setBonus(value);
	}

	@Override
	public String toString() {
		return getShortDescr();
	}

	@Override
	public void write(java.lang.Object value) {
		// TODO Auto-generated method stub
		// send to scripts expecting response
	}

	@Override
	public void writef(String fmt, java.lang.Object... args) {
		// TODO Auto-generated method stub
		// send to scripts expecting response
	}

	@Override
	public void writeln() {

	}

	@Override
	public void writeln(java.lang.Object line) {
		// TODO Auto-generated method stub
		// send to scripts expecting response
	}

	@Override
	public void writelnf(String fmt, java.lang.Object... args) {
		// TODO Auto-generated method stub

	}

	protected void setFlags(Flag bits) {
		flags = bits;
	}
}
