package net.arg3.jmud;

public class Argument {

	String argument;

	public Argument() {
		argument = "";
	}

	public Argument(String arg) {
		argument = arg;
	}

	public String getNext() {

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

			argument = argument.substring(endPos + 1);

		} else {
			arg = argument.substring(pos);
			argument = null;
		}
		return arg;
	}

	public boolean isNullOrEmpty() {
		return Jmud.isNullOrEmpty(argument);
	}

	public String peekNext() {
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
		} else {
			arg = argument.substring(pos);
		}
		return arg;
	}

	public boolean startsWith(String arg) {
		return Jmud.isPrefix(arg, argument);
	}

	@Override
	public String toString() {
		return argument;
	}
}
