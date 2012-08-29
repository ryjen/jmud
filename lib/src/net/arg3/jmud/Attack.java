/**
 * 
 */
package net.arg3.jmud;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.arg3.jmud.enums.DamType;

/**
 * @author Ryan
 * 
 */
@Embeddable
public class Attack {
	DamType damType;
	String noun;
	boolean isHit;

	public Attack() {
	}

	public Attack(DamType type, String noun) {
		damType = type;
		this.noun = noun;
	}

	@Column(name = "dam_type")
	public DamType getDamType() {
		return damType;
	}

	@Column(name = "attack_noun")
	public String getNoun() {
		return noun;
	}

	@Override
	public String toString() {
		return noun;
	}

	@Transient
	public boolean isHit() {
		return isHit;
	}

	public void setDamType(DamType value) {
		damType = value;
	}

	public void setNoun(String value) {
		noun = value;
	}

	public void setIsHit(boolean value) {
		isHit = value;
	}
}
