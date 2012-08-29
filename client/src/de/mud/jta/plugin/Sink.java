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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mud.jta.FilterPlugin;
import de.mud.jta.Plugin;
import de.mud.jta.PluginBus;
import de.mud.jta.event.OnlineStatusListener;

/**
 * The terminal plugin represents the actual terminal where the data will be
 * displayed and the gets the keyboard input to sent back to the remote host.
 * <P>
 * <B>Maintainer:</B> Matthias L. Jugel
 * 
 * @version $Id: Sink.java 499 2005-09-29 08:24:54Z leo $
 * @author Matthias L. Jugel, Marcus Mei�ner
 */
public class Sink extends Plugin implements FilterPlugin, Runnable {

	private final static Logger log = LoggerFactory.getLogger(Sink.class);

	private Thread reader = null;

	protected FilterPlugin source;

	public Sink(final PluginBus bus, final String id) {
		super(bus, id);
		// register an online status listener
		bus.registerPluginListener(new OnlineStatusListener() {
			@Override
			public void offline() {
				log.debug("offline");
				if (reader != null)
					reader = null;
			}

			@Override
			public void online() {
				log.debug("online " + reader);
				if (reader == null) {
					reader = new Thread();
					reader.start();
				}
			}
		});
	}

	@Override
	public FilterPlugin getFilterSource() {
		return source;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return source.read(b);
	}

	/**
	 * Continuously read from our back end and drop the data.
	 */
	@Override
	public void run() {
		byte[] b = new byte[256];
		int n = 0;
		while (n >= 0)
			try {
				n = read(b);
				/* drop the bytes into the sink :) */
			} catch (IOException e) {
				reader = null;
				break;
			}
	}

	@Override
	public void setFilterSource(FilterPlugin source) {
		this.source = source;
	}

	@Override
	public void write(byte[] b) throws IOException {
		source.write(b);
	}
}
