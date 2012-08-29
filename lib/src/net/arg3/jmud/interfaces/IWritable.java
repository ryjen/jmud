package net.arg3.jmud.interfaces;

public interface IWritable {
	public void write(java.lang.Object value);

	public void writef(String fmt, java.lang.Object... args);

	public void writeln();

	public void writeln(java.lang.Object line);

	public void writelnf(String fmt, java.lang.Object... args);
}
