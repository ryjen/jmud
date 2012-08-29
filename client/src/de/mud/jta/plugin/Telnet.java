/*
 * This file is part of "JTA - Telnet/SSH for the JAVA(tm) platform".
 *
 * (c) Matthias L. Jugel, Marcus Meißner 1996-2005. All Rights Reserved.
 *
 * Please visit http://javatelnet.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 *
 */

package de.mud.jta.plugin;

import java.awt.Dimension;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mud.jta.FilterPlugin;
import de.mud.jta.Plugin;
import de.mud.jta.PluginBus;
import de.mud.jta.PluginConfig;
import de.mud.jta.event.ConfigurationListener;
import de.mud.jta.event.EndOfRecordRequest;
import de.mud.jta.event.LocalEchoRequest;
import de.mud.jta.event.MCCPRequest;
import de.mud.jta.event.OnlineStatusListener;
import de.mud.jta.event.SetWindowSizeListener;
import de.mud.jta.event.TelnetCommandListener;
import de.mud.jta.event.TerminalTypeRequest;
import de.mud.jta.event.WindowSizeRequest;
import de.mud.telnet.TelnetProtocolHandler;

/**
 * The telnet plugin utilizes a telnet protocol handler to filter telnet
 * negotiation requests from the data stream.
 * <P>
 * <B>Maintainer:</B> Matthias L. Jugel
 * 
 * @version $Id: Telnet.java 503 2005-10-24 07:34:13Z marcus $
 * @author Matthias L. Jugel, Marcus Meissner
 */
public class Telnet extends Plugin implements FilterPlugin {

	protected FilterPlugin source;
	protected TelnetProtocolHandler handler;

	private final static Logger log = LoggerFactory.getLogger(Telnet.class);

	/**
	 * Create a new telnet plugin.
	 */
	public Telnet(final PluginBus bus, String id) {
		super(bus, id);

		// create a new telnet protocol handler
		handler = new TelnetProtocolHandler() {
			/** get the current terminal type */
			@Override
			public String getTerminalType() {
				return (String) bus.broadcast(new TerminalTypeRequest());
			}

			/** get the current window size */
			@Override
			public Dimension getWindowSize() {
				return (Dimension) bus.broadcast(new WindowSizeRequest());
			}

			/** notify about EOR end of record */
			@Override
			public void notifyEndOfRecord() {
				bus.broadcast(new EndOfRecordRequest());
			}

			/** notify about local echo */
			@Override
			public void setLocalEcho(boolean echo) {
				bus.broadcast(new LocalEchoRequest(echo));
			}

			@Override
			public void setMCCPRequest(boolean mccp) {
				bus.broadcast(new MCCPRequest(mccp));
				log.debug("got MCCP request");
			}

			/** write data to our back end */
			@Override
			public void write(byte[] b) throws IOException {
				source.write(b);
			}
		};

		// reset the telnet protocol handler just in case :-)
		bus.registerPluginListener(new OnlineStatusListener() {
			@Override
			public void offline() {
				handler.reset();
				bus.broadcast(new LocalEchoRequest(true));
			}

			@Override
			public void online() {
				handler.reset();
				try {
					handler.startup();
				} catch (java.io.IOException e) {
				}

				bus.broadcast(new LocalEchoRequest(true));
			}
		});

		bus.registerPluginListener(new SetWindowSizeListener() {
			@Override
			public void setWindowSize(int columns, int rows) {
				try {
					handler.setWindowSize(columns, rows);
				} catch (java.io.IOException e) {
					log.error("IO Exception in set window size");
				}
			}
		});

		bus.registerPluginListener(new ConfigurationListener() {
			@Override
			public void setConfiguration(PluginConfig config) {
				configure(config);
			}
		});
		bus.registerPluginListener(new TelnetCommandListener() {
			@Override
			public void sendTelnetCommand(byte command) throws IOException {
				handler.sendTelnetControl(command);
			}
		});

	}

	public void configure(PluginConfig cfg) {
		String crlf = cfg.getProperty("Telnet", id, "crlf"); // on \n
		if (crlf != null)
			handler.setCRLF(crlf);

		String cr = cfg.getProperty("Telnet", id, "cr"); // on \r
		if (cr != null)
			handler.setCR(cr);
	}

	@Override
	public FilterPlugin getFilterSource() {
		return source;
	}

	@Override
	public int read(byte[] b) throws IOException {
		/*
		 * We just don't pass read() down, since negotiate() might call other
		 * functions and we need transaction points.
		 */
		int n;

		/*
		 * clear out the rest of the buffer. loop, in case we have negotiations
		 * (return 0) and date (return > 0) mixed ... until end of buffer or any
		 * data read.
		 */
		do {
			n = handler.negotiate(b);
			if (n > 0)
				return n;
		} while (n == 0);

		/*
		 * try reading stuff until we get at least 1 byte of real data or are at
		 * the end of the buffer.
		 */
		while (true) {
			n = source.read(b);
			if (n <= 0)
				return n;

			handler.inputfeed(b, n);
			n = 0;
			while (true) {
				n = handler.negotiate(b);
				if (n > 0)
					return n;
				if (n == -1) // buffer empty.
					break;
			}
			return 0;
		}
	}

	@Override
	public void setFilterSource(FilterPlugin source) {
		log.debug("connected to: " + source);
		this.source = source;
	}

	@Override
	public void write(byte[] b) throws IOException {
		handler.transpose(b); // transpose 0xff or \n and send data
	}
}
