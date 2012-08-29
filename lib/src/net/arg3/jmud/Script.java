/**
 * 
 */
package net.arg3.jmud;

import javax.persistence.Column;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

/**
 * @author Ryan
 * 
 */
public abstract class Script {
	private String code;

	public void execute() {
		if (Jmud.isNullOrEmpty(code))
			return;

		LuaState state = LuaStateFactory.newLuaState();

		state.openLibs();

		pushGlobals(state);

		state.LdoString(code);

		state.close();
	}

	@Column(name = "code", nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String value) {
		code = value;
	}

	protected abstract void pushGlobals(LuaState state);
}
