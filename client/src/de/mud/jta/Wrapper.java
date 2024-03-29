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

package de.mud.jta;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mud.telnet.ScriptHandler;

/**
 * To write a program using the wrapper you may use the following piece of code
 * as an example:
 * 
 * <PRE>
 * TelnetWrapper telnet = new TelnetWrapper();
 * try {
 * 	telnet.connect(args[0], 23);
 * 	telnet.login(&quot;user&quot;, &quot;password&quot;);
 * 	telnet.setPrompt(&quot;user@host&quot;);
 * 	telnet.waitfor(&quot;Terminal type?&quot;);
 * 	telnet.send(&quot;dumb&quot;);
 * 	System.out.println(telnet.send(&quot;ls -l&quot;));
 * } catch (java.io.IOException e) {
 * 	e.printStackTrace();
 * }
 * </PRE>
 * 
 * Please keep in mind that the password is visible for anyone who can download
 * the class file. So use this only for public accounts or if you are absolutely
 * sure nobody can see the file.
 * <P>
 * <B>Maintainer:</B> Matthias L. Jugel
 * 
 * @version $Id: Wrapper.java 499 2005-09-29 08:24:54Z leo $
 * @author Matthias L. Jugel, Marcus Mei\u00dfner
 */

public class Wrapper {

	/** debugging level */
	private final static Logger log = LoggerFactory.getLogger(Wrapper.class);

	protected ScriptHandler scriptHandler = new ScriptHandler();
	protected InputStream in;
	protected OutputStream out;
	protected Socket socket;
	protected String host;
	protected int port = 23;
	// protected Vector script = new Vector();

	/**
	 * Set the prompt for the send() method.
	 */
	private String prompt = null;

	/** Connect the socket and open the connection. */
	public void connect(String host, int port) throws IOException {
		log.debug("connect(" + host + "," + port + ")");
		try {
			socket = new java.net.Socket(host, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (Exception e) {
			log.error(e.getMessage());
			disconnect();
			throw ((IOException) e);
		}
	}

	/** Disconnect the socket and close the connection. */
	public void disconnect() throws IOException {
		log.debug("disconnect()");
		if (socket != null)
			socket.close();
	}

	public String getPrompt() {
		return prompt;
	}

	public String getTerminalType() {
		return "dumb";
	}

	public Dimension getWindowSize() {
		return new Dimension(80, 24);
	}

	/**
	 * Login into remote host. This is a convenience method and only works if
	 * the prompts are "login:" and "Password:".
	 * 
	 * @param user
	 *            the user name
	 * @param pwd
	 *            the password
	 */
	public void login(String user, String pwd) throws IOException {
		waitfor("login:"); // throw output away
		send(user);
		waitfor("Password:"); // throw output away
		send(pwd);
	}

	/**
	 * Read data from the socket and use telnet negotiation before returning the
	 * data read.
	 * 
	 * @param b
	 *            the input buffer to read in
	 * @return the amount of bytes read
	 */
	public int read(byte[] b) throws IOException {
		return -1;
	}

	/**
	 * Send a command to the remote host. A newline is appended and if a prompt
	 * is set it will return the resulting data until the prompt is encountered.
	 * 
	 * @param cmd
	 *            the command
	 * @return output of the command or null if no prompt is set
	 */
	public String send(String cmd) throws IOException {
		return null;
	}

	public void setLocalEcho(boolean echo) {
		log.debug("local echo " + (echo ? "on" : "off"));
	};

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String waitfor(String match) throws IOException {
		String[] matches = new String[1];

		matches[0] = match;
		return waitfor(matches);
	}

	/**
	 * Wait for a string to come from the remote host and return all that
	 * characters that are received until that happens (including the string
	 * being waited for).
	 * 
	 * @param match
	 *            the string to look for
	 * @return skipped characters
	 */

	public String waitfor(String[] searchElements) throws IOException {
		ScriptHandler[] handlers = new ScriptHandler[searchElements.length];
		for (int i = 0; i < searchElements.length; i++) {
			// initialize the handlers
			handlers[i] = new ScriptHandler();
			handlers[i].setup(searchElements[i]);
		}

		byte[] b1 = new byte[1];
		int n = 0;
		StringBuffer ret = new StringBuffer();
		String current;

		while (n >= 0) {
			n = read(b1);
			if (n > 0) {
				current = new String(b1, 0, n);
				log.debug(current);
				ret.append(current);
				for (int i = 0; i < handlers.length; i++) {
					if (handlers[i].match(ret.toString().getBytes(),
							ret.length())) {
						return ret.toString();
					} // if
				} // for
			} // if
		} // while
		return null; // should never happen
	}

	/**
	 * Write data to the socket.
	 * 
	 * @param b
	 *            the buffer to be written
	 */
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
}
