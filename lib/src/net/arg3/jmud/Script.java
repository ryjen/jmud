/**
 * 
 */
package net.arg3.jmud;

import javax.persistence.Column;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * @author Ryan
 * 
 */
public abstract class Script {
	private String code;
	private String name;

	public void execute() {
		if (Jmud.isNullOrEmpty(code))
			return;

		try {
			Context context = Context.enter();

			Scriptable scope = context.initStandardObjects();
	
			Object result = context.evaluateString(scope, code, name, 0, null);
			
			if(result != null)
				System.out.println(Context.toString(result));
		}
		finally
		{
			Context.exit();
		}
	}

	@Column(name = "code", nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String value) {
		code = value;
	}

	@Column(name = "name", nullable = false)
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	protected abstract void pushGlobals(Context state);
}
