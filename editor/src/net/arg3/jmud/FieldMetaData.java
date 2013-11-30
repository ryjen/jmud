/**
 * Project: jmudeditor
 * Date: 2009-09-28
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.arg3.jmud.editors.AbstractCollectionEditor;
import net.arg3.jmud.editors.AbstractEditor;
import net.arg3.jmud.editors.ArrayEditor;
import net.arg3.jmud.editors.CollectionEditor;
import net.arg3.jmud.editors.DoubleEditor;
import net.arg3.jmud.editors.EnumEditor;
import net.arg3.jmud.editors.FloatEditor;
import net.arg3.jmud.editors.IntegerEditor;
import net.arg3.jmud.editors.LongEditor;
import net.arg3.jmud.editors.MapEditor;
import net.arg3.jmud.editors.SelectionEditor;
import net.arg3.jmud.editors.StringEditor;
import net.arg3.jmud.editors.TextEditor;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.enums.Position;
import net.arg3.jmud.enums.Sector;
import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.enums.WearLocation;
import net.arg3.jmud.model.Area;
import net.arg3.jmud.model.Continent;
import net.arg3.jmud.model.Help;
import net.arg3.jmud.model.Race;
import net.arg3.jmud.model.Room;

import org.hibernate.collection.internal.PersistentSet;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class FieldMetaData {

	protected static final HashMap<Class<?>, Class<? extends AbstractEditor>> availableEditors;

	static {
		availableEditors = new HashMap<Class<?>, Class<? extends AbstractEditor>>();
		availableEditors.put(int.class, IntegerEditor.class);
		availableEditors.put(String.class, StringEditor.class);
		availableEditors.put(float.class, FloatEditor.class);
		availableEditors.put(double.class, DoubleEditor.class);
		availableEditors.put(List.class, CollectionEditor.class);
		availableEditors.put(long.class, LongEditor.class);
		availableEditors.put(Map.class, MapEditor.class);
		availableEditors.put(Set.class, CollectionEditor.class);
		availableEditors.put(Position.class, EnumEditor.class);
		availableEditors.put(Sex.class, EnumEditor.class);
		availableEditors.put(Sector.class, EnumEditor.class);
		availableEditors.put(PersistentSet.class, CollectionEditor.class);
		availableEditors.put(Direction.class, EnumEditor.class);
		availableEditors.put(Room.class, SelectionEditor.class);
		availableEditors.put(Area.class, SelectionEditor.class);
		availableEditors.put(Race.class, SelectionEditor.class);
		availableEditors.put(WearLocation.class, EnumEditor.class);
		availableEditors.put(Continent.class, SelectionEditor.class);
		availableEditors.put(Help.class, SelectionEditor.class);

	}
	// String field;
	Method getter;
	AbstractEditor editor;
	boolean generated;
	java.lang.Object from;
	Class<?> returnType;
	Vector<FieldMetaData> embeddedFields;

	String altName;

	public FieldMetaData(java.lang.Object from, Method getter) {
		this.from = from;
		this.getter = getter;

		returnType = getter.getReturnType();

		setEditor();
	}

	private void setEditor() {

		Class<? extends AbstractEditor> editorType = availableEditors
				.get(returnType);

		if (editorType == null) {
			if (returnType.isArray()) {
				editor = new ArrayEditor();
			} else {
				editor = new StringEditor();
			}
		} else if (editorType == MapEditor.class) {
			editor = new MapEditor(Persistance.getMapKey(getter));
		} else if (editorType == StringEditor.class
				&& Persistance.isTextValue(getter)) {
			editor = new TextEditor();
		} else {
			try {
				editor = editorType.newInstance();
			} catch (InstantiationException e1) {
				editor = new StringEditor();
			} catch (IllegalAccessException e1) {
				editor = new StringEditor();
			}
		}

		if (editor instanceof AbstractCollectionEditor) {
			if (editor instanceof ArrayEditor) {
				editor.setReadOnly(true);
			}
			AbstractCollectionEditor ed = (AbstractCollectionEditor) editor;
			try {
				ed.setTargetEntity(Persistance.getTargetEntity(getter));
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		} else if (editor instanceof EnumEditor) {
			((EnumEditor) editor).setType(returnType);
		}
		if (Persistance.isGeneratedValue(getter)) {
			editor.setReadOnly(true);
			generated = true;
		}
	}

	public AbstractEditor getEditor() {
		return editor;
	}

	public static boolean isColumn(Method method) {
		if (!method.getName().startsWith("get"))
			return false;
		if (Modifier.isAbstract(method.getModifiers()))
			return false;
		if (Modifier.isStatic(method.getModifiers()))
			return false;
		if (method.getParameterTypes().length != 0)
			return false;
		if (void.class.equals(method.getReturnType()))
			return false;
		if (method.getName().equals("getClass"))
			return false;
		if (method.getAnnotation(Transient.class) != null)
			return false;

		return true;
	}

	public List<FieldMetaData> getEmbeddedFields() {
		if (!isEmbedded()) {
			return null;
		}
		if (embeddedFields != null) {
			return embeddedFields;
		}
		embeddedFields = new Vector<FieldMetaData>();
		for (Method f : returnType.getMethods()) {
			if (!isColumn(f))
				continue;

			FieldMetaData fmd;
			try {
				fmd = new FieldMetaData(getter.invoke(from), f);

				embeddedFields.add(fmd);
			} catch (Exception e) {
				continue;
			}

			AttributeOverrides ao = getter
					.getAnnotation(AttributeOverrides.class);

			if (ao == null)
				continue;

			for (AttributeOverride at : ao.value()) {
				if (!Jmud.isSuffix(f.getName(), at.name())
						&& !Jmud.isNullOrEmpty(at.column().name()))
					continue;

				fmd.altName = at.column().name();
			}

		}
		return embeddedFields;
	}

	public String getFieldName() {
		if (altName != null) {
			return Jmud.camelToHuman(altName);
		}

		return getter.getName().replace("get", "");
	}

	public java.lang.Object getValue(java.lang.Object... params) {
		try {
			return getter.invoke(from, params);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isEmbedded() {
		return returnType.getAnnotation(Embeddable.class) != null;
	}

	public void setValue() {
		if (from == null) {
			editor.setValue(null);
			return;
		}
		try {
			editor.setValue(getter.invoke(from));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private Method getSetter(Class<?> type) {
		try {
			return type.getMethod(getter.getName().replace("get", "set"),
					returnType);

		} catch (Exception e) {
			try {
				return type.getDeclaredMethod(
						getter.getName().replace("get", "set"), returnType);

			} catch (Exception e1) {
			}

		}
		return null;
	}

	public void updateValue(java.lang.Object value) {
		from = value;
		returnType = from.getClass();
		setValue();
	}

	public void save() {
		if (generated) {
			return;
		}

		Class<?> type = from.getClass();
		Method setter = getSetter(type);
		while (setter == null && (type = type.getSuperclass()) != null) {
			setter = getSetter(type);
		}
		if (setter == null) {
			LoggerFactory.getLogger(FieldMetaData.class).warn(
					"unable to get setter for " + getter.getName());
			return;
		}
		setter.setAccessible(true);
		try {
			setter.invoke(from, editor.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
