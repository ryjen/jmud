package net.arg3.jmud;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.arg3.jmud.interfaces.IFlaggable;
import net.arg3.jmud.interfaces.IFormatible;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import com.mchange.v2.lang.ObjectUtils;

public class Flag implements IFlaggable<Long>, UserType, IFormatible, Cloneable {

	private long value;

	private static final int[] SQL_TYPES = new int[] { Types.BIGINT };

	public Flag() {
		value = 0;
	}

	public Flag(long value) {
		this.value = value;
	}

	public Flag(Flag other) {
		this.value = other.getValue();
	}

	@Override
	public Flag clone() throws CloneNotSupportedException {
		Flag f = (Flag) super.clone();

		return f;
	}

	@Override
	public java.lang.Object assemble(Serializable arg0, java.lang.Object arg1)
			throws HibernateException {
		return arg0;
	}

	@Override
	public java.lang.Object deepCopy(java.lang.Object arg0)
			throws HibernateException {
		return arg0;
	}

	@Override
	public Serializable disassemble(java.lang.Object arg0)
			throws HibernateException {
		return (Serializable) arg0;
	}

	@Override
	public boolean equals(java.lang.Object arg0, java.lang.Object arg1)
			throws HibernateException {
		return ObjectUtils.eqOrBothNull(arg0, arg1);
	}

	@Override
	public boolean has(Long bit) {
		return (value & bit) != 0;
	}

	@Override
	public int hashCode(java.lang.Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public java.lang.Object nullSafeGet(ResultSet arg0, String[] arg1, SessionImplementor session,
			java.lang.Object arg2) throws HibernateException, SQLException {
		Long value = StandardBasicTypes.LONG.nullSafeGet(arg0, arg1[0], session);
		if (value == null)
			return null;

		return new Flag(value);
	}

	@Override
	public void nullSafeSet(PreparedStatement arg0, java.lang.Object arg1,
			int arg2, SessionImplementor session) throws HibernateException, SQLException {
		if (arg1 != null) {
			StandardBasicTypes.LONG.nullSafeSet(arg0, ((Flag) arg1).getValue(),
					arg2, session);
		}
	}

	@Override
	public void remove(Long bit) {
		value &= ~bit;
	}

	protected long getValue() {
		return value;
	}

	@Override
	public java.lang.Object replace(java.lang.Object arg0,
			java.lang.Object arg1, java.lang.Object arg2)
			throws HibernateException {
		return arg0;
	}

	@Override
	public Class<?> returnedClass() {
		return Flag.class;
	}

	@Override
	public void set(Long bit) {
		value |= bit;
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public void toggle(Long bit) {
		value ^= bit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.interfaces.IFormatible#toString(java.lang.String)
	 */
	@Override
	public String toString(String format) {
		if (Jmud.isNullOrEmpty(format))
			return toString();

		try {
			Class<?> type = ClassLoader.getSystemClassLoader()
					.loadClass(format);

			return Jmud.toCommaSeparatedValues(Jmud.getFlagNames(type));
		} catch (ClassNotFoundException e) {
			return toString();
		}
	}

	@Override
	public String toString() {
		List<Integer> bits = new ArrayList<Integer>();

		for (int i = 0; i < Long.SIZE; i++) {
			if ((value & (1L << i)) != 0)
				bits.add(i);
		}
		return Jmud.toCommaSeparatedValues(bits);
	}
}
