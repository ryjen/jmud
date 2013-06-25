/**
 * Project: jMUD
 * Date: 2009-09-14
 * Package: net.arg3.jmud.server
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import net.arg3.jmud.commands.Command;
import net.arg3.jmud.commands.LogoffCommand;
import net.arg3.jmud.commands.QuitCommand;
import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.interfaces.IExecutable;
import net.arg3.jmud.interfaces.ITerminal;
import net.arg3.jmud.model.Account;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.PcRace;
import net.arg3.jmud.model.Player;
import net.arg3.jmud.model.Profession;
import net.arg3.jmud.model.Race;
import net.arg3.jmud.model.Room;
import net.arg3.jmud.updates.Updater;
import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.io.terminal.ColorHelper;
import net.wimpi.telnetd.io.toolkit.Pager;
import net.wimpi.telnetd.io.toolkit.Statusbar;
import net.wimpi.telnetd.io.toolkit.Titlebar;
import net.wimpi.telnetd.net.Connection;
import net.wimpi.telnetd.net.ConnectionEvent;
import net.wimpi.telnetd.shell.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class MudShell implements Shell, ITerminal {

	static final Logger log = LoggerFactory.getLogger(MudShell.class);
	private static HashSet<MudShell> list = null;

	public static Shell createShell() {
		MudShell shell = new MudShell();
		shell.id = getList().size() + 1;
		getList().add(shell);
		return shell;
	}

	public static HashSet<MudShell> getList() {
		if (list == null) {
			list = new HashSet<MudShell>();
		}
		return list;
	}

	private int id;
	Connection connection;
	BasicTerminalIO io;
	Pager pager;
	Statusbar statusBar;
	Titlebar titleBar;
	// BufferedReader input;
	Method state;
	Player player;
	Account account;
	private int passwdRetryCount = 1;

	@Override
	public boolean colorOnOff() {
		return io.getTerminal().supportsSGR();
	}

	@Override
	public double compressionRatio() {
		return io.compressionRatio();
	}

	public void confirmAccountPassword(String arg) throws IOException,
			SecurityException, NoSuchMethodException {
		if (Arrays.equals(account.getPassword(),
				Persistance.encryptString(arg, account.getLogin()))) {
			write("Please enter your email address: ");
			setEcho(true);
			setState("getEmail");
		} else {
			writeln("Passwords don't match, please enter your password again.");
			write("Password: ");
			setState("getAccountPassword");
		}
	}

	public void confirmCharName(String arg) throws IOException,
			SecurityException, NoSuchMethodException {
		if (Jmud.isNullOrEmpty(arg)) {
			write("Please enter either (Y)es or (N)o: ");
			return;
		}
		switch (java.lang.Character.toUpperCase(arg.charAt(0))) {
		case 'Y':
			displaySexes();
			write("What is your sex? ");
			setState("getCharSex");
			break;
		case 'N':
			write("What is your character's name then? ");
			setState("getCharName");
			break;
		default:
			write("Please enter either (Y)es or (N)o: ");
			return;
		}
	}

	public void confirmNewAccount(String arg) throws IOException,
			SecurityException, NoSuchMethodException {
		switch (java.lang.Character.toUpperCase(arg.charAt(0))) {
		case 'Y':
			setEcho(false);
			write("Please enter a password for " + account.getLogin() + ": ");
			setState("getAccountPassword");
			return;
		case 'N':
			write("Login: ");
			setState("getLogin");
			return;
		default:
			write("Please enter either (Y)es or (N)o: ");
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.wimpi.telnetd.net.ConnectionListener#connectionIdle(net.wimpi.telnetd
	 * .net.ConnectionEvent)
	 */
	@Override
	public void connectionIdle(ConnectionEvent arg0) {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.wimpi.telnetd.net.ConnectionListener#connectionLogoutRequest(net.
	 * wimpi.telnetd.net.ConnectionEvent)
	 */
	@Override
	public void connectionLogoutRequest(ConnectionEvent arg0) {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.wimpi.telnetd.net.ConnectionListener#connectionSentBreak(net.wimpi
	 * .telnetd.net.ConnectionEvent)
	 */
	@Override
	public void connectionSentBreak(ConnectionEvent arg0) {
		connection.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.wimpi.telnetd.net.ConnectionListener#connectionTimedOut(net.wimpi
	 * .telnetd.net.ConnectionEvent)
	 */
	@Override
	public void connectionTimedOut(ConnectionEvent arg0) {
		connection.close();
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MudShell)) {
			return false;
		}
		MudShell other = (MudShell) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public void getAccountPassword(String arg) throws SecurityException,
			NoSuchMethodException, IOException {

		if (account.getPassword() == null) {
			byte[] passwd = Persistance.encryptString(arg, account.getLogin());

			account.setPassword(passwd);

			write("Please enter your password again: ");
			setState("confirmAccountPassword");
		} else if (Arrays.equals(account.getPassword(),
				Persistance.encryptString(arg, account.getLogin()))) {
			setEcho(true);
			displayPlayerMenu();
			setState("selectPlayer");
		} else if (passwdRetryCount <= 3) {
			write("Wrong password, try again (" + passwdRetryCount
					+ "). Password: ");
			passwdRetryCount++;
		} else {
			writeln("Exceeded maximum password retries, closing connection.");
			connection.close();
		}
	}

	public void getCharName(String arg) throws IOException, SecurityException,
			NoSuchMethodException {
		arg = Jmud.capitalize(arg);

		player = new Player(arg);
		write("Create a character named " + arg + " (Y/n)");
		setState("confirmCharName");
	}

	public void getCharProfession(String arg) throws IOException,
			SecurityException, NoSuchMethodException {
		for (Profession p : Profession.getList()) {
			if (Jmud.isPrefix(p.getName(), arg)) {
				player.setProfession(p);

				setPlaying();
				return;
			}
		}

		write("Unknown profession '" + arg + "'. What is your profession? ");
	}

	public void getCharRace(String arg) throws IOException, SecurityException,
			NoSuchMethodException {

		for (Race race : Race.getList()) {
			if (race instanceof PcRace
					&& Jmud.isPrefix(race.getName(), arg)) {
				player.setRace(race);
				displayProfessions();
				write("What is your profession? ");
				setState("getCharProfession");
				return;
			}
		}

		write("Unknown race '" + arg + "'. What is your race? ");
	}

	public void getCharSex(String arg) throws IOException, SecurityException,
			NoSuchMethodException {
		for (Sex sex : Sex.values()) {
			if (Jmud.isPrefix(sex.toString(), arg)) {

				player.setSex(sex);

				displayRaces();

				write("What race would you like to be? ");
				setState("getCharRace");
				return;
			}
		}
		write("Unknown sex '" + arg + "'.  What is your sex? ");
	}

	@Override
	public int getColumns() {
		return io.getColumns();
	}

	public void getEmail(String arg) throws IOException, SecurityException,
			NoSuchMethodException {
		String error = Jmud.validEmail(arg);

		if (error != null) {
			writeln(error);
			write("Please enter your email address: ");
			return;
		}

		account.setEmail(arg);

		displayPlayerMenu();
		setState("selectPlayer");
	}

	public void getLogin(String arg) throws IOException, SecurityException,
			NoSuchMethodException {

		account = Account.getByLogin(arg);

		if (account == null) {
			account = new Account();

			account.setLogin(arg);

			write("Would you like to create an account for " + arg + "? (Y/n) ");

			setState("confirmNewAccount");
		} else {

			setEcho(false);

			write("Password: ");

			setState("getAccountPassword");
		}
	}

	@Override
	public int getRows() {
		return io.getRows();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean isCompressing() {
		return io.isCompressing();
	}

	@Override
	public void page(java.lang.Object obj) throws IOException {
		pager.page(obj.toString());
	}

	public void playing(String arg) throws IOException, SecurityException,
			NoSuchMethodException {
		try {
			Argument argument = new Argument(arg);
			String cmdName = argument.getNext();

			IExecutable cmd = Command.lookup(cmdName);

			if (cmd != null) {
				player.addCommand(cmd, argument);
			} else {
				player.writeln("huh?");
			}

		} catch (QuitCommand.QuitException ex) {
			connection.close();
		} catch (LogoffCommand.LogoffException ex) {
			displayPlayerMenu();
			setState("selectPlayer");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wimpi.telnetd.shell.Shell#run(net.wimpi.telnetd.net.Connection)
	 */
	@Override
	public void run(Connection arg0) {
		connection = arg0;
		io = arg0.getTerminalIO();
		pager = new Pager(io);
		connection.addConnectionListener(this);

		pager.setPrompt("[Use up, down and space to navigate or 'q' to stop]");
		pager.setStopKey('q');
		pager.setShowPosition(true);

		createStatusBar();
		createTitleBar();

		// ConnectionData cd = connection.getConnectionData();

		try {
			setState("getLogin");

			// input = new BufferedReader(new InputStreamReader(cd.getSocket()
			// .getInputStream()));

			write(BasicTerminalIO.CRLF);

			displayGreeting();

			while (connection.isAlive()) {
				String line = io.readLine();

				if (line == null) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
					continue;
				}

				if (player != null && player.getFlags().has(Player.TITLEBAR)) {
					io.storeCursor();

					// erase the previous title bar
					io.setCursor(1, 1);
					io.eraseLine();

					// erase the previous status bar
					io.setCursor(io.getRows(), 1);
					io.eraseLine();

					io.restoreCursor();
				}

				writeln();

				if (!Jmud.isNullOrEmpty(line))
					state.invoke(this, line);

				if (player != null && player.getFlags().has(Player.TITLEBAR)) {

					writeln();

					titleBar.draw();

					statusBar.draw();

					io.setCursor(
							io.getRows(),
							Math.min(statusBar.getStatusText().length() + 2,
									io.getColumns()));
				} else if (state.getName().contentEquals("playing")) // display
																		// prompt
				{
					writeln();
					write("> ");
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (SecurityException e) {
			log.error(e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void selectPlayer(String arg) throws IOException, SecurityException,
			NoSuchMethodException {
		if (Jmud.isNullOrEmpty(arg)) {
			displayPlayerMenu();
			return;
		}

		char ch = java.lang.Character.toUpperCase(arg.charAt(0));

		if (ch == 'H') {
			displayPlayerMenu();
			return;
		}

		if (ch == 'C') {
			if (account.getPlayers().size() >= 10) {
				writeln("You may only create 10 characters.  Please login and delete one first.");
				return;
			}
			write("What is your characters name? ");
			setState("getCharName");
			return;
		}

		int index;

		try {
			index = Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			displayPlayerMenu();
			return;
		}

		if (index < 1 || index > account.getPlayers().size()) {
			writeln("No such option or character!");
			write("Try again: ");
		}
		player = (Player) account.getPlayers().toArray()[index - 1];

		setPlaying();
	}

	@Override
	public void setColor(boolean value) {
		io.getTerminal().setSupportsSGR(value);
	}

	@Override
	public void setEcho(boolean value) throws IOException {
		io.setEcho(value);
	}

	@Override
	public synchronized void write(java.lang.Object value) {
		if (value == null)
			return;

		try {
			io.write(Color.convert(value.toString(), colorOnOff()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void writef(String fmt, java.lang.Object... args) {
		try {
			io.write(Color.convert(String.format(fmt, args), colorOnOff()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void writeln() {
		try {
			io.write(BasicTerminalIO.CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void writeln(java.lang.Object line) {
		if (line == null)
			return;

		try {
			io.write(Color.convert(line.toString(), colorOnOff()));
			io.write(BasicTerminalIO.CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void writelnf(String fmt, java.lang.Object... args) {
		try {
			io.write(Color.convert(String.format(fmt, args), colorOnOff()));
			io.write(BasicTerminalIO.CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createStatusBar() {
		statusBar = new Statusbar(io, "bar1");
		statusBar.setStatusText("<Your prompt here>");
		statusBar.setBackgroundColor(ColorHelper.BLACK);
		statusBar.setForegroundColor(ColorHelper.YELLOW);
		statusBar.setAlignment(Statusbar.ALIGN_LEFT);
	}

	private void createTitleBar() {
		titleBar = new Titlebar(io, "title1");
		titleBar.setTitleText("jMUD v1.0");
		titleBar.setBackgroundColor(ColorHelper.BLUE);
		titleBar.setForegroundColor(ColorHelper.GREEN);
		titleBar.setAlignment(Titlebar.ALIGN_LEFT);
	}

	private void displayGreeting() throws IOException {
		writeln("Welcome to "
				+ Server.getInstance().getConfig().getProperty("jmud.name")
				+ "!");

		write("Login: ");
	}

	private void displayPlayerMenu() throws IOException {
		writeln();
		writeln("Options:");
		writeln("C) Create new character");
		writeln("H) Display this menu");
		writeln();
		if (account.getPlayers().size() > 0) {
			writeln("Characters:");
			int index = 1;
			for (Player p : account.getPlayers()) {
				writeln(index + ") " + p.getName() + " (lvl: " + p.getLevel()
						+ ")");
				index++;
			}
			writeln();
		}
		write("Choose: ");
	}

	private void displayProfessions() throws IOException {
		writeln();
		for (Profession p : Profession.getList()) {
			write(p.getName() + " ");
		}
		writeln();
		writeln();
	}

	private void displayRaces() throws IOException {
		writeln();
		for (Race race : Race.getList()) {
			if (!(race instanceof PcRace))
				continue;
			write(race.getName() + " ");
		}
		writeln();
		writeln();
	}

	private void displaySexes() throws IOException {
		writeln();
		for (Sex sex : Sex.values()) {
			write(sex.toString().toLowerCase() + "  ");
		}
		writeln();
		writeln();
	}

	private void readMOTD() throws IOException {
		writeln("The system time is " + Jmud.now());

	}

	private void setPlaying() throws IOException, SecurityException,
			NoSuchMethodException {
		player.setAccount(account);
		if (player.getRoom() == null)
			player.setRoom(Room.getDefault());
		player.setTerminal(this);
		readMOTD();
		Updater.Add(player);
		Character.getList().add(player);
		Player.getPlaying().add(player);
		setState("playing");
	}

	private void setState(String method) throws SecurityException,
			NoSuchMethodException {
		state = this.getClass().getMethod(method, String.class);
	}
}
