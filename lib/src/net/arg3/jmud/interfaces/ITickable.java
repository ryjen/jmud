package net.arg3.jmud.interfaces;

public interface ITickable {

	public int SECOND = 1000;

	public int getPeriod();

	public boolean tick();
}
