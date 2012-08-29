/**
 * Project: jChat Server
 * Date: 2009-07-23
 * Package: net.jennings.ryan.jChat
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.tests;

import junit.framework.TestCase;
import net.arg3.jmud.Server;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class TestServer extends TestCase {

	/**
	 * Test method for {@link net.jennings.ryan.Server.ChatServer#isStopped()}.
	 * 
	 * @throws Exception
	 */
	public void testIsStopped() throws Exception {
		Server.getInstance().stop();

		/*
		 * if (!Server.getInstance().isStopped())
		 * fail("isStopped still thinks the server is running");
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				Server.getInstance().start();
			}
		}).start();

		Thread.sleep(1000);

		/*
		 * if (Server.getInstance().isStopped())
		 * fail("isStopped thinks the server is stopped");
		 */
	}

	/**
	 * Test method for {@link net.jennings.ryan.Server.ChatServer#start()}.
	 */
	public void testStart() {
		/*
		 * if (Server.getInstance().isStopped())
		 * fail("ChatServer unable to start");
		 */
	}

	/**
	 * Test method for {@link net.jennings.ryan.Server.ChatServer#stop()}.
	 * 
	 * @throws Exception
	 */
	public void testStop() throws Exception {
		Server.getInstance().stop();
		/*
		 * if (!Server.getInstance().isStopped())
		 * fail("unable to stop the ChatServer");
		 */
	}

	/**
	 * Test method for
	 * {@link net.jennings.ryan.Server.ChatServer#update(java.util.Observable, java.lang.Object)}
	 * .
	 */
	public void testUpdate() {
		/*
		 * try {
		 * 
		 * Socket s1 = new Socket("localhost", PORT); Socket s2 = new
		 * Socket("localhost", PORT); BufferedReader br1 = new
		 * BufferedReader(new InputStreamReader(s1 .getInputStream()));
		 * BufferedReader br2 = new BufferedReader(new InputStreamReader(s2
		 * .getInputStream())); PrintWriter pw1 = new
		 * PrintWriter(s1.getOutputStream(), true); PrintWriter pw2 = new
		 * PrintWriter(s2.getOutputStream(), true);
		 * 
		 * pw1.println("test1"); pw2.println("test2");
		 * 
		 * pw1.println("test1"); pw2.println("test2");
		 * 
		 * String line;
		 * 
		 * if (!(line = br1.readLine()).contains("test2"))
		 * fail("Socket1 did not read Socket2 message: " + line);
		 * 
		 * if (!(line = br2.readLine()).contains("test1"))
		 * fail("Socket2 did not read Socket1 message: " + line);
		 * 
		 * s1.close(); s2.close(); } catch (UnknownHostException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	@Override
	protected void setUp() throws Exception {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Server.getInstance().start();
			}
		}).start();
		Thread.sleep(1000);

	}

	@Override
	protected void tearDown() throws Exception {
		Server.getInstance().stop();
	}
}
