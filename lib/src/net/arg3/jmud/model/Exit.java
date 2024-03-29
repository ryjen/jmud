/**
 * Project: jMUD
 * Date: 2009-09-09
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.arg3.jmud.Jmud;
import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFlaggable;
import net.arg3.jmud.interfaces.IFormatible;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
@Entity
@Table(name = "room_exit")
public class Exit implements IDataObject<Exit.PK>, IFlaggable<Integer>,
		IFormatible {

	private static final long serialVersionUID = 1L;
	private Room toRoom;
	// private Room room;
	// private Direction direction;
	private PK id = new PK();
	private String description;
	private String keywords;
	private long key;
	private int saveInfo;
	private int currentInfo;

	@FlagValue
	public static final int DOOR = (1 << 0);
	@FlagValue
	public static final int CLOSED = (1 << 1);
	@FlagValue
	public static final int LOCKED = (1 << 2);
	@FlagValue
	public static final int SECURE = (1 << 3);
	@FlagValue
	public static final int NOLOCK = (1 << 4);

	@Override
	public int compareTo(IDataObject<PK> o) {
		return getId().compareTo(o.getId());
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	@Transient
	public Direction getDirection() {
		return id.getDirection();
	}

	@Override
	@Id
	public PK getId() {
		return id;
	}

	@Column(name = "key_id", nullable = true)
	public long getKey() {
		return key;
	}

	@Column(name = "keyword")
	public String getKeyword() {
		return keywords;
	}

	@Transient
	public Room getRoom() {
		return id.getRoom();
	}

	/**
	 * @return the toRoom
	 */
	@ManyToOne(targetEntity = Room.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "to_room")
	public Room getToRoom() {
		return toRoom;
	}

	@Override
	public boolean has(Integer bit) {
		return (currentInfo & bit) != 0;
	}

	@Override
	public void remove(Integer bit) {
		currentInfo &= ~bit;
	}

	@Override
	public void set(Integer bit) {
		currentInfo |= bit;
	}

	public void setDescription(String value) {
		description = value;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Direction direction) {
		// this.direction = direction;
		id.setDirection(direction);
	}

	@Override
	public void setId(PK id) {
		this.id = id;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public void setKeyword(String value) {
		keywords = value;
	}

	/**
	 * @param room
	 *            the room to set
	 */
	public void setRoom(Room room) {
		// this.room = room;
		id.setRoom(room);
	}

	public void setSaveInfo(int value) {
		currentInfo = saveInfo = value;
	}

	/**
	 * @param toRoom
	 *            the toRoom to set
	 */
	public void setToRoom(Room toRoom) {
		this.toRoom = toRoom;
	}

	@Override
	public void toggle(Integer bit) {
		currentInfo ^= bit;
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return toString();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + getId() + ","
					+ (toRoom == null ? "?" : toRoom.getId()) + "]";
		default:
			return toString();
		}
	}

	@Column(name = "flags")
	protected int getSaveInfo() {
		return saveInfo;
	}
	

/**
 * 
 * @author Ryan
 */
@Embeddable
public static class PK implements Serializable, Comparable<PK> {

	private static final long serialVersionUID = 1L;
	private Room room;
	private Direction direction;

	@Override
	public int compareTo(PK o) {
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
		final PK other = (PK) obj;
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

}
