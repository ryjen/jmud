package net.arg3.jmud.interfaces;

public interface IFlaggable<T> {

	public boolean has(T bit);

	public void remove(T bit);

	public void set(T bit);

	public void toggle(T bit);
}
