package net.arg3.jmud;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IExecutable;
import net.arg3.jmud.interfaces.IFormatible;

@Entity
@Table(name = "social")
public class Social implements IDataObject<Integer>, IExecutable, IFormatible {
	private static final long serialVersionUID = 1L;

	private static Set<Social> list;

	public static Set<Social> getList() {
		if (list == null) {
			list = new HashSet<Social>();
			list.addAll(Persistance.getAll(Social.class));
		}
		return list;
	}

	public static Social lookup(String arg) {
		for (Social soc : getList()) {
			if (Jmud.isPrefix(soc.getName(), arg)) {
				return soc;
			}
		}
		return null;
	}

	private int id;
	private String name;
	private String charNoArg;
	private String othersNoArg;
	private String charSelf;
	private String otherSelf;
	private String charFound;
	private String othersFound;
	private String victFound;
	private String charNotFound;
	private String charAuto;
	private String othersAuto;
	private String charObjFound;

	private String othersObjFound;

	@Override
	public int compareTo(IDataObject<Integer> arg0) {
		return getId().compareTo(arg0.getId());
	}

	@Override
	public int execute(Character ch, Argument argument) {
		String arg = argument.getNext();
		Character victim = null;

		if (Jmud.isNullOrEmpty(arg)) {
			ch.getRoom().format(getOthersNoArg(), ch);
			ch.format(getCharNoArg(), ch);
		} else if ((victim = ch.getRoom().getChar(arg)) == null) {
			ch.writeln("They aren't here.");
		} else if (victim == ch) {
			ch.getRoom().format(getOthersAuto(), ch);
			ch.format(getCharAuto(), ch);
		} else {
			ch.getRoom().format(getOthersFound(), ch, victim);
			ch.format(getCharFound(), victim);
			victim.format(getVictFound(), ch);

			if (!ch.isPlayer() && victim.isPlayer()) {
				switch (Jmud.Rand.nextInt(12)) {
				case 0:

				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					ch.getRoom().format(getOthersFound(), ch, victim);
					victim.format(getCharFound(), ch);
					ch.format(getVictFound(), victim);
					break;

				case 9:
				case 10:
				case 11:
				case 12:
					ch.getRoom().format("{0} slaps {1}.", ch, victim);
					victim.format("You slap {0}.", ch);
					ch.format("{0} slaps you.", victim);
					break;
				}
			}
		}
		return 0;
	}

	@Column(name = "char_auto")
	public String getCharAuto() {
		return charAuto;
	}

	@Column(name = "char_found")
	public String getCharFound() {
		return charFound;
	}

	@Column(name = "char_no_arg")
	public String getCharNoArg() {
		return charNoArg;
	}

	@Column(name = "char_not_found")
	public String getCharNotFound() {
		return charNotFound;
	}

	@Column(name = "char_obj_found")
	public String getCharObjFound() {
		return charObjFound;
	}

	@Column(name = "char_self")
	public String getCharSelf() {
		return charSelf;
	}

	@Override
	@Column(name = "social_id")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	@Override
	@Column(name = "name")
	public String getName() {
		return name;
	}

	@Column(name = "others_auto")
	public String getOthersAuto() {
		return othersAuto;
	}

	@Column(name = "others_self")
	public String getOtherSelf() {
		return otherSelf;
	}

	@Column(name = "others_found")
	public String getOthersFound() {
		return othersFound;
	}

	@Column(name = "others_no_arg")
	public String getOthersNoArg() {
		return othersNoArg;
	}

	@Column(name = "others_obj_found")
	public String getOthersObjFound() {
		return othersObjFound;
	}

	@Column(name = "vict_found")
	public String getVictFound() {
		return victFound;
	}

	public void setCharAuto(String charAuto) {
		this.charAuto = charAuto;
	}

	public void setCharFound(String charFound) {
		this.charFound = charFound;
	}

	public void setCharNoArg(String charNoArg) {
		this.charNoArg = charNoArg;
	}

	public void setCharNotFound(String charNotFound) {
		this.charNotFound = charNotFound;
	}

	public void setCharObjFound(String charObjFound) {
		this.charObjFound = charObjFound;
	}

	public void setCharSelf(String charSelf) {
		this.charSelf = charSelf;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setId(Integer value) {
		id = value;
	}

	public void setName(String value) {
		name = value;
	}

	public void setOthersAuto(String othersAuto) {
		this.othersAuto = othersAuto;
	}

	public void setOtherSelf(String otherSelf) {
		this.otherSelf = otherSelf;
	}

	public void setOthersFound(String othersFound) {
		this.othersFound = othersFound;
	}

	public void setOthersNoArg(String othersNoArg) {
		this.othersNoArg = othersNoArg;
	}

	public void setOthersObjFound(String othersObjFound) {
		this.othersObjFound = othersObjFound;
	}

	public void setVictFound(String victFound) {
		this.victFound = victFound;
	}

	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return getName();

		switch (format.charAt(0)) {
		case 'Z':
			return "[" + getId() + "," + getName() + "]";
		default:
			return toString();
		}
	}
}
