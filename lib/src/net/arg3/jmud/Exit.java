/**
 * Project: jMUD
 * Date: 2009-09-09
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
public class Exit implements IDataObject<ExitPK>, IFlaggable<Integer>,
		IFormatible {

	private static final long serialVersionUID = 1L;
	private Room toRoom;
	// private Room room;
	// private Direction direction;
	private ExitPK id = new ExitPK();
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
	public int compareTo(IDataObject<ExitPK> o) {
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
	public ExitPK getId() {
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
	public void setId(ExitPK id) {
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
}
