package net.arg3.jmud.interfaces;

import java.io.IOException;

public interface ITerminal extends IWritable {

	public boolean colorOnOff();

	public double compressionRatio();

	public int getColumns();

	public int getRows();

	public boolean isCompressing();

	public void page(java.lang.Object obj) throws IOException;

	public void setColor(boolean value);

	public void setEcho(boolean value) throws IOException;
}
