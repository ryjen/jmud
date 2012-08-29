package net.arg3.jmud.tests;

import junit.framework.TestCase;
import net.arg3.jmud.Character;
import net.arg3.jmud.NonPlayer;
import net.arg3.jmud.enums.Sex;

import org.junit.Test;

public class TestActions extends TestCase {

	@Test
	public void testNewParse() {
		Character ch = new NonPlayer();
		ch.setShortDescr("Tester");

		Character smelly = new NonPlayer();
		smelly.setShortDescr("Smelly");
		smelly.setSex(Sex.Female);

		ch.format("{1} says {1:S} friend {0} smells.", ch, smelly);

		// System.out.println(result);
	}
}
