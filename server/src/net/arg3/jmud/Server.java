/*
 * jChat
 * Date: July 2009
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

import net.arg3.jmud.model.Character;
import net.arg3.jmud.updates.Updater;
import net.wimpi.telnetd.BootException;
import net.wimpi.telnetd.TelnetD;
import net.wimpi.telnetd.io.terminal.ColorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for chat connections
 * 
 * @author Ryan JenningS <c0der78@gmail.com>
 */
public class Server {

	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	private static final String JMUD_PROPERTIES = "jmud.properties";
	private static final String LOGGING_PROPERTIES = "logging.properties";

	/**
	 * @return singleton implementation
	 */
	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	/**
	 * main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Server.getInstance().start();
	}

	private Properties config;
	// ServerSocket serverSocket;
	static Server instance;
	// ArrayList<Connection> connections;
	// public static int PORT = 4444;
	TelnetD telnetd;

	/**
	 * default constructor
	 */
	private Server() {
		try {
			LogManager.getLogManager().readConfiguration(
					new FileInputStream(LOGGING_PROPERTIES));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		try {
			config = new Properties();
			config.load(new FileInputStream(JMUD_PROPERTIES));
			telnetd = TelnetD.createTelnetD(config);
		} catch (IOException e) {
			logger.warn(e.getMessage());
		} catch (BootException e) {
			logger.error(e.getMessage());
		}
	}

	public void announce(Character ch, String str) {

		StringBuilder buf = new StringBuilder();
		buf.append(ColorHelper.boldcolorizeText("ANNOUNCE", ColorHelper.GREEN,
				ColorHelper.BLUE));
		buf.append(": ");
		buf.append(str);

		writeToAll(Format.parse(buf.toString(), ch));
	}

	public Properties getConfig() {
		return config;
	}

	/**
	 * starts listening and handling connections
	 */
	public void start() {

		telnetd.start();

		Updater.initialize();
		
		logger.info("Now accepting connections on port " + getConfig().getProperty("jmud.port"));
	}

	/**
	 * stops the server
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {

		telnetd.stop();
	}

	public void writeToAll(String message) {
		for (MudShell sh : MudShell.getList()) {
			sh.write(message);
		}
	}
}
