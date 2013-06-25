/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.arg3.jmud.interfaces.IFormatible;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Room;

/**
 * 
 * @author ryan
 */
public final class Format {

	public static String parse(String str, java.lang.Object... objs) {
		if (str == null)
			return null;

		StringBuffer buf = new StringBuffer();
		Pattern pattern = Pattern.compile("\\{[0-9:a-zA-Z]+\\}");
		Matcher matcher = pattern.matcher(str);

		boolean result = matcher.find();
		while (result) {

			String match = matcher.group().replaceAll("[{}]", "");
			String[] formats = match.split(":");
			int index = Integer.parseInt(formats[0]);
			if (index >= objs.length) {
				throw new IllegalArgumentException("only " + objs.length
						+ " arguments specified");
			}
			java.lang.Object obj = objs[index];
			String format = null;
			if (formats.length > 1 && obj instanceof IFormatible) {
				format = ((IFormatible) obj).toString(formats[1]);
			} else {
				format = obj.toString();
			}
			matcher.appendReplacement(buf, format);
			result = matcher.find();
		}
		matcher.appendTail(buf);

		return buf.toString();
	}

	public static void toChar(Character ch, String format,
			java.lang.Object... args) {
		String buf = parse(format, args);

		ch.writeln(buf);
	}

	public static void toOthers(Room room, String format,
			java.lang.Object... args) {
		String buf = parse(format, args);
		for (Character ch : room.getCharacters()) {
			if (ch == args[0] || (args.length > 1 && ch == args[1]))
				continue;

			ch.writeln(buf);
		}
	}

	public static void toRoom(Room room, String format,
			java.lang.Object... args) {
		String buf = parse(format, args);

		for (Character ch : room.getCharacters()) {
			if (ch == args[0])
				continue;

			ch.writeln(buf);
		}
	}
}
