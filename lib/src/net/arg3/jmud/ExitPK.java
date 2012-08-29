/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.arg3.jmud.enums.Direction;

/**
 * 
 * @author Ryan
 */
@Embeddable
public class ExitPK implements Serializable, Comparable<ExitPK> {

	private static final long serialVersionUID = 1L;
	private Room room;
	private Direction direction;

	@Override
	public int compareTo(ExitPK o) {
		return getDirection().compareTo(o.getDirection());
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ExitPK other = (ExitPK) obj;
		if (this.getRoom() != other.getRoom()
				&& (this.getRoom() == null || !this.getRoom().equals(
						other.getRoom()))) {
			return false;
		}
		if (this.getDirection() != other.getDirection()) {
			return false;
		}
		return true;
	}

	@Column(name = "direction", columnDefinition = "tinyint")
	public Direction getDirection() {
		return direction;
	}

	@ManyToOne(targetEntity = Room.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "room_id")
	public Room getRoom() {
		return room;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash
				+ (this.getRoom() != null ? this.getRoom().hashCode() : 0);
		hash = 89 * hash + this.getDirection().hashCode();
		return hash;
	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return (direction == null ? "?" : direction.toString());
	}
}
