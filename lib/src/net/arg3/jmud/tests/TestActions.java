package net.arg3.jmud.tests;

import junit.framework.TestCase;
import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.NonPlayer;

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
