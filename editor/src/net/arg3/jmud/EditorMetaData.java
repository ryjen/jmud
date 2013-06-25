/**
 * Project: jmudeditor
 * Date: 2009-09-26
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.model.Ability;
import net.arg3.jmud.model.Account;
import net.arg3.jmud.model.Affect;
import net.arg3.jmud.model.Area;
import net.arg3.jmud.model.Continent;
import net.arg3.jmud.model.Exit;
import net.arg3.jmud.model.Help;
import net.arg3.jmud.model.Hint;
import net.arg3.jmud.model.NonPlayer;
import net.arg3.jmud.model.Profession;
import net.arg3.jmud.model.Race;
import net.arg3.jmud.model.Reset;
import net.arg3.jmud.model.Room;
import net.arg3.jmud.model.Shop;
import net.arg3.jmud.model.Social;
import net.arg3.jmud.view.AbstractDefaultView;
import net.arg3.jmud.view.DefaultView;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class EditorMetaData {

	private static final EditorMetaData[] availableEditors = {
			new EditorMetaData(NonPlayer.class, false),
			new EditorMetaData(Continent.class, false),
			new EditorMetaData(Area.class, false),
			new EditorMetaData(Object.class, false),
			new EditorMetaData(Help.class, false),
			new EditorMetaData(Profession.class, false),
			new EditorMetaData(Race.class, false),
			new EditorMetaData(Shop.class, false),
			new EditorMetaData(Room.class, false),
			new EditorMetaData(Exit.class, true),
			new EditorMetaData(Hint.class, false),
			new EditorMetaData(Reset.class, false),
			new EditorMetaData(Ability.class, false),
			new EditorMetaData(Account.class, false),
			new EditorMetaData(World.class, false),
			new EditorMetaData(Affect.class, true),
			new EditorMetaData(Social.class, false) };

	public static EditorMetaData get(Class<? extends IDataObject<?>> type) {
		for (EditorMetaData emd : availableEditors) {
			if (emd.getType() == type) {
				return emd;
			}
		}
		return null;
	}

	public static EditorMetaData[] getAll() {
		return availableEditors;
	}

	private Class<?> type;
	AbstractDefaultView editor;
	boolean hidden;
	java.lang.Object value;

	private EditorMetaData(Class<?> type, boolean hidden) {
		this.type = type;
		this.editor = new DefaultView(type);
		this.hidden = hidden;
	}

	private EditorMetaData(Class<?> type, AbstractDefaultView editor,
			boolean hidden) {
		this.type = type;
		this.editor = editor;
		this.hidden = hidden;
	}

	/**
	 * @return the editor
	 */
	public AbstractDefaultView getEditor() {
		return editor;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	public java.lang.Object getValue() {
		return value;
	}

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param editor
	 *            the editor to set
	 */
	public void setEditor(AbstractDefaultView editor) {
		this.editor = editor;
	}

	/**
	 * @param hidden
	 *            the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	public void setValue(java.lang.Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return type.getSimpleName();
	}
}
