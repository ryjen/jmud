package net.arg3.jmud.interfaces;

public interface IAction<T> {

	boolean performOn(T obj);
}
