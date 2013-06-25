package net.arg3.jmud;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.PROPERTY)
public class Dice implements Cloneable {
	int number;
	int type;
	int bonus;

	@Override
	public Dice clone() throws CloneNotSupportedException {
		return (Dice) super.clone();
	}

	@Column(nullable = false)
	public int getBonus() {
		return bonus;
	}

	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	@Column(nullable = false)
	public int getType() {
		return type;
	}

	public int roll() {
		return Jmud.Roll(number, type) + bonus;
	}

	public void setBonus(int value) {
		bonus = value;
	}

	public void setNumber(int value) {
		number = value;
	}

	public void setType(int value) {
		type = value;
	}
}
