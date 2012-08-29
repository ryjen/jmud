package net.arg3.jmud.interfaces;

import java.io.Serializable;

public interface IDataObject<ID> extends Serializable,
		Comparable<IDataObject<ID>> {

	public ID getId();

	public void setId(ID value);
}
