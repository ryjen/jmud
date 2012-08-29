package net.arg3.jmud.tests;

import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class TestLua extends TestCase {

	LuaState state;

	@Override
	@Before
	public void setUp() throws Exception {
		state = LuaStateFactory.newLuaState();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		state.close();
	}

	@Test
	public void testMore() {
		state.newTable();
		state.pushValue(-1);
		state.setGlobal("eg");

		state.pushString("example");

		try {
			state.pushJavaFunction(new JavaFunction(state) {
				/**
				 * Example for loadLib. Prints the time and the first parameter,
				 * if any.
				 */
				@Override
				public int execute() throws LuaException {
					System.out.println(new Date().toString());

					if (L.getTop() > 1) {
						System.out.println(getParam(2));
					}

					return 0;
				}
			});
		} catch (LuaException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}

		state.setTable(-3);

		state.LdoString("eg.example(3)");
	}

	@Test
	public void testScript() {
		int err = state
				.LdoString("if true then val=1 else val=2 end return val");

		if (err == LuaState.LUA_ERRRUN)
			System.out.print("run error");
		else if (err == LuaState.LUA_ERRSYNTAX)
			System.out.println("syntax error");

		Assert.assertTrue(err == 0);

		int num = state.toInteger(-1);

		Assert.assertTrue(num == 1);
	}
}
