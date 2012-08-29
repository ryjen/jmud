package de.mud.jta.event;

import de.mud.jta.PluginListener;

public interface MCCPListener extends PluginListener {
	public void setMCCP(boolean on);
}
