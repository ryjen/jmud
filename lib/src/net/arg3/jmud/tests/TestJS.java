package net.arg3.jmud.tests;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.mozilla.javascript.Context;

public class TestJS extends TestCase {

	Context state;

	@Override
	@Before
	public void setUp() throws Exception {
		state = Context.enter();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		Context.exit();
	}

//	@Test
//	public void testMore() {
//		state.newTable();
//		state.pushValue(-1);
//		state.setGlobal("eg");
//
//		state.pushString("example");
//
//		try {
//			state.pushJavaFunction(new JavaFunction(state) {
//				/**
//				 * Example for loadLib. Prints the time and the first parameter,
//				 * if any.
//				 */
//				@Override
//				public int execute() throws LuaException {
//					System.out.println(new Date().toString());
//
//					if (L.getTop() > 1) {
//						System.out.println(getParam(2));
//					}
//
//					return 0;
//				}
//			});
//		} catch (LuaException e) {
//			// TODO Auto-generated catch block
//			fail(e.getMessage());
//		}
//
//		state.setTable(-3);
//
//		state.LdoString("eg.example(3)");
//	}
//
//	@Test
//	public void testScript() {
//		int err = state
//				.LdoString("if true then val=1 else val=2 end return val");
//
//		if (err == LuaState.LUA_ERRRUN)
//			System.out.print("run error");
//		else if (err == LuaState.LUA_ERRSYNTAX)
//			System.out.println("syntax error");
//
//		Assert.assertTrue(err == 0);
//
//		int num = state.toInteger(-1);
//
//		Assert.assertTrue(num == 1);
//	}
}
