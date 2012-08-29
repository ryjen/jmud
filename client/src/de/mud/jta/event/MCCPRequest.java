package de.mud.jta.event;

import de.mud.jta.PluginListener;
import de.mud.jta.PluginMessage;

public class MCCPRequest implements PluginMessage {

	protected boolean mccp = false;

	public MCCPRequest(boolean mccp) {
		this.mccp = mccp;
	}

	@Override
	public Object firePluginMessage(PluginListener pl) {
		if (pl instanceof MCCPListener) {
			((MCCPListener) pl).setMCCP(mccp);
		}
		return null;
	}

}
