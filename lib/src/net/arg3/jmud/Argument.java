package net.arg3.jmud;

/**
 * 
 * @author c0der78
 *
 */
public class Argument {

	String argument;

	/**
	 * initializes an empty argument
	 */
	public Argument() {
		argument = "";
	}

	/**
	 * initializes an argument with a string
	 * @param arg
	 */
	public Argument(String arg) {
		argument = arg;
	}
	
	public String getNext() {
		return next(true);
	}

	/**
	 * will modify the value of the argument
	 * @return the next argument
	 */
	private String next(boolean modify) {

		if (Jmud.isNullOrEmpty(argument)) {
			return argument;
		}

		char cEnd = ' ';
		int pos = 0;

		if (argument.charAt(pos) == '\'' || argument.charAt(pos) == '"'
				|| argument.charAt(pos) == '(') {
			if (argument.charAt(pos) == '(') {
				cEnd = ')';
			} else {
				cEnd = argument.charAt(pos);
			}
			++pos;
		}

		int endPos = argument.indexOf(cEnd, pos);

		String arg;

		if (endPos != -1) {
			arg = argument.substring(pos, endPos);

			if(modify)
				argument = argument.substring(endPos + 1);

		} else {
			arg = argument.substring(pos);
			if(modify)
				argument = null;
		}
		return arg;
	}

	/**
	 * 
	 * @return true if argument is null or empty
	 */
	public boolean isNullOrEmpty() {
		return Jmud.isNullOrEmpty(argument);
	}

	/**
	 * will not modify the argument
	 * @return the next argument
	 */
	public String peekNext() {
		return next(false);
	}

	/**
	 * @param arg
	 * @return true if this arguments starts with a given string
	 */
	public boolean startsWith(String arg) {
		return Jmud.isPrefix(arg, argument);
	}

	@Override
	public final String toString() {
		return argument;
	}
}
