package net.arg3.jmud.view;

import java.util.Collection;

public class CollectionView extends AbstractCollectionView {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	Collection data;

	@SuppressWarnings("unchecked")
	@Override
	protected void add(java.lang.Object obj) {
		data.add(obj);
	}

	@Override
	protected java.lang.Object[] getValues() {
		return data.toArray();
	}

	@Override
	protected void remove(java.lang.Object obj) {
		data.remove(obj);
	}

	@Override
	protected void setData(java.lang.Object obj) {
		data = (Collection<?>) obj;
	}
}
