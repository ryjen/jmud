/**
 * Project: jChat Server
 * Date: 2009-07-24
 * Package: net.jennings.ryan.jChat.tests
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.jennings.ryan.jMUD.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestServer.class);
		// $JUnit-END$
		return suite;
	}
}
