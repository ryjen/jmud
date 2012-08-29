package net.arg3.jmud.tests;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.Reset;
import net.arg3.jmud.Room;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestResets extends TestCase {

	@BeforeClass
	public static void beforeClass() {
		Persistance.getSession();
	}

	@Test
	public void testExecute() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.println("npc(2)");
		pw.println("give(1)");
		pw.println("put(1)");
		pw.println("obj(1)");
		pw.println("npc(2)");
		pw.println("equip(1, 'head')");

		Reset reset = new Reset();
		reset.setId(1L);

		Room room = new Room();
		room.setId(1L);

		reset.setRoom(room);

		reset.setCode(sw.toString());

		reset.execute();
	}

}
