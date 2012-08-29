/**
 * 
 */
package net.arg3.jmud.view;

/**
 * @author Ryan
 * 
 */
public class ArrayView extends AbstractCollectionView {
	private static final long serialVersionUID = 1L;
	java.lang.Object[] data;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.view.AbstractCollectionView#add(java.lang.Object)
	 */
	@Override
	protected void add(Object obj) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.view.AbstractCollectionView#getValues()
	 */
	@Override
	protected java.lang.Object[] getValues() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.view.AbstractCollectionView#remove(java.lang.Object)
	 */
	@Override
	protected void remove(Object obj) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.arg3.jmud.view.AbstractCollectionView#setData(java.lang.Object)
	 */
	@Override
	protected void setData(Object obj) {
		data = (java.lang.Object[]) obj;
	}

}
