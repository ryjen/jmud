package net.arg3.jmud;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.arg3.jmud.exceptions.OutOfMoneyException;
import net.arg3.jmud.interfaces.IFormatible;

@Embeddable
@Access(AccessType.PROPERTY)
public class Money implements Comparable<Money>, IFormatible, Cloneable {
	long copper;
	long silver;
	long gold;

	public Money() {
	}

	@Override
	public Money clone() throws CloneNotSupportedException {
		Money m = (Money) super.clone();

		return m;
	}

	public Money(long gold) {
		this.gold = gold;
	}

	public Money(long gold, long silver, long copper) {
		this.gold = gold;
		this.silver = silver;
		this.copper = copper;
	}

	public Money(long gold, long silver) {
		this.gold = gold;
		this.silver = silver;
	}

	public void addCopper(long value) {
		while (value > 100) {
			addSilver(1);
			value -= 100;
		}

		copper += value;
	}

	public void addGold(long value) {
		gold += value;
	}

	public void addSilver(long value) {
		while (value > 100) {
			addGold(1);
			value -= 100;
		}

		silver += value;
	}

	@Override
	public int compareTo(Money arg0) {
		return getTotal().compareTo(arg0.getTotal());
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Money))
			return false;
		Money m = (Money) obj;
		return gold == m.gold && silver == m.silver && copper == m.copper;
	}

	@Override
	public int hashCode() {
		return (int) (gold + silver + copper);
	}
	
	@Column
	public long getCopper() {
		return copper;
	}

	@Column
	public long getGold() {
		return gold;
	}

	@Column
	public long getSilver() {
		return silver;
	}

	public void subtractCopper(long value) throws OutOfMoneyException {
		while ((copper - value) < 0) {
			subtractSilver(1);
			copper += 100;
		}

		copper -= value;
	}

	public void subtractGold(long value) throws OutOfMoneyException {
		if ((gold - value) < 0)
			throw new OutOfMoneyException();

		gold -= value;
	}

	public void subtractSilver(long value) throws OutOfMoneyException {
		while ((silver - value) < 0) {
			subtractGold(1);
			silver += 100;
		}

		silver -= value;
	}

	@Override
	public String toString() {
		return String.format("%d.%d.%d", gold, silver, copper);
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return toString();

		switch (format.charAt(0)) {
		default:
			return toString();
		case 'G':
			return NumberFormat.getCurrencyInstance().format(getGold());
		case 'S':
			return NumberFormat.getCurrencyInstance().format(getSilver());
		case 'C':
			return NumberFormat.getCurrencyInstance().format(getCopper());
		}
	}

	@Transient
	protected BigDecimal getTotal() {
		BigDecimal total = new BigDecimal(getCopper());

		total = total.add(new BigDecimal(getSilver() * 100));

		total = total.add(new BigDecimal(getGold() * 10000));

		return total;
	}

	protected void setCopper(long value) {
		copper = value;
	}

	protected void setGold(long value) {
		gold = value;
	}

	protected void setSilver(long value) {
		silver = value;
	}
}
