package net.arg3.jmud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.arg3.jmud.annotations.FlagValue;
import net.arg3.jmud.interfaces.IAction;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.model.AbstractObject;
import net.arg3.jmud.model.Character;
import net.arg3.jmud.model.Room;

import org.slf4j.LoggerFactory;

public final class Jmud {
	public static final Random Rand = new Random();

	public static final String CRLF = "\r\n";
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static String toCommaSeparatedValues(Iterable<?> values) {
		return deliminate(",", values);
	}

	public static String repeat(String orig, int times) {
		StringBuilder buf = new StringBuilder(orig);

		for (int i = 0; i < times; i++)
			buf.append(orig);

		return orig.toString();
	}

	public static <T> void forEach(Collection<T> collection, IAction<T> action) {
		for (T obj : collection) {
			action.performOn(obj);
		}
	}

	public static <T> T find(Collection<T> collection, IAction<T> action) {
		for (T obj : collection) {
			if (action.performOn(obj))
				return obj;
		}
		return null;
	}
	
	public static AbstractObject findObj(IAction<AbstractObject> action) {
		return find(AbstractObject.getList(), action);
	}
	
	public static Room findRoom(IAction<Room> action) {
		return find(Room.getList(), action);
	}
	
	public static Character findChar(IAction<Character> action) {
		return find(Character.getList(), action);
	}

	public static String deliminate(String delimiter, Iterable<?> values) {
		StringBuilder buf = new StringBuilder();
		for (java.lang.Object value : values) {
			buf.append(value.toString());
			buf.append(delimiter);
		}
		int pos = buf.lastIndexOf(delimiter);
		if (pos != -1)
			buf.delete(pos, pos + delimiter.length());

		return buf.toString();
	}

	public static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static String camelToHuman(String arg) {
		if (arg == null || arg.length() == 0) {
			return arg;
		}

		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < arg.length(); i++) {
			if (java.lang.Character.isUpperCase(arg.charAt(i))) {
				if (i != 0) {
					buf.append(" ");
				}
			}

			buf.append(arg.charAt(i));
		}
		return buf.toString();
	}

	public static String capitalize(String arg) {
		if (arg == null || arg.length() == 0) {
			return arg;
		}
		return arg.substring(0, 1).toUpperCase() + arg.substring(1);
	}

	public static <T, U extends IDataObject<T>> U find(Set<U> list, T id) {
		for (U item : list) {
			if (item.getId() == id)
				return item;
		}
		return null;
	}

	public static int fuzzy(int number) {
		return range(number - 1, number + 1);
	}

	public static List<String> getFlagNames(Class<?> type) {
		ArrayList<String> names = new ArrayList<String>();

		for (Field field : type.getDeclaredFields()) {
			if (field.getAnnotation(FlagValue.class) == null
					|| !Modifier.isStatic(field.getModifiers())
					|| !field.getDeclaringClass().isAssignableFrom(Flag.class)) {
				continue;
			}

			names.add(field.getName());
		}
		return Collections.unmodifiableList(names);
	}

	public static List<String> getFlagNames(Class<?> type, Long checkOn) {
		ArrayList<String> names = new ArrayList<String>();

		for (Field field : type.getDeclaredFields()) {
			if (field.getAnnotation(FlagValue.class) == null
					|| !Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			try {
				long value = field.getLong(null);

				if ((checkOn & value) != 0)
					names.add(field.getName());
			} catch (Exception e) {
				LoggerFactory.getLogger(type).warn(
						field.getName() + " has @FlagValue but is not a Long");
			}
		}
		return Collections.unmodifiableList(names);
	}

	public static List<Long> getFlagValues(Class<?> type) {
		ArrayList<Long> values = new ArrayList<Long>();

		for (Field field : type.getDeclaredFields()) {
			if (field.getAnnotation(FlagValue.class) == null
					|| !Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			try {
				values.add(field.getLong(null));
			} catch (IllegalAccessException ex) {
				LoggerFactory.getLogger(type).error(ex.getMessage());
			}
		}
		return Collections.unmodifiableList(values);
	}

	public static int interpolate(int level, int value_00, int value_32) {
		return value_00 + level * (value_32 - value_00) / 32;
	}

	public static boolean isName(String name, String argument) {
		StringTokenizer token = new StringTokenizer(name);

		while (token.hasMoreTokens()) {
			StringTokenizer token2 = new StringTokenizer(argument);

			while (token2.hasMoreTokens()) {
				if (isPrefix(token.nextToken(), token2.nextToken()))
					return true;
			}
		}
		return false;
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public static int range(int low, int high) {
		return Math.min(low, high) + Rand.nextInt(Math.abs(high - low));
	}

	public static int range(int a, int b, int c) {
		return ((b) < (a) ? (a) : ((b) > (c) ? (c) : (b)));
	}

	public static int percent() {
		return range(0, 100);
	}

	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	/*
	 * Roll some dice.
	 */
	public static int Roll(int number, int size) {
		int idice;
		int sum;

		switch (size) {
		case 0:
			return 0;
		case 1:
			return number;
		}

		for (idice = 0, sum = 0; idice < number; idice++)
			sum += range(1, size);

		return sum;
	}

	public static boolean isPrefix(String arg1, String arg2) {
		if (arg1 == null || arg2 == null) {
			return true;
		}
		return arg1.toLowerCase().startsWith(arg2.toLowerCase());
	}

	public static boolean isInfix(String astr, String bstr) {
		int sstr1;
		int sstr2;
		int ichar;
		char c0;

		if (Jmud.isNullOrEmpty(astr))
			return true;
		c0 = java.lang.Character.toLowerCase(astr.charAt(0));

		sstr1 = astr.length();
		sstr2 = bstr.length();

		for (ichar = 0; ichar <= sstr2 - sstr1; ichar++) {
			if (c0 == java.lang.Character.toLowerCase(bstr.charAt(ichar))
					&& !isPrefix(astr, bstr.substring(ichar)))
				return true;
		}

		return false;
	}

	/*
	 * Compare strings, case insensitive, for suffix matching. Return TRUE if
	 * astr not a suffix of bstr (compatibility with historical functions).
	 * 
	 * Taken from Rivers of MUD / Diku RJ
	 */
	public static boolean isSuffix(String bstr, String astr) {
		int sstr1;
		int sstr2;

		sstr1 = astr.length();
		sstr2 = bstr.length();
		if (sstr1 <= sstr2
				&& !astr.equalsIgnoreCase(bstr.substring(sstr2 - sstr1)))
			return false;
		else
			return true;
	}

	public static String validEmail(String input) {
		if (isNullOrEmpty(input)) {
			return "An address must be entered.";
		}
		// Checks for email addresses starting with
		// inappropriate symbols like dots or @ signs.
		Pattern p = Pattern.compile("^\\.|^\\@");
		Matcher m = p.matcher(input);
		if (m.find()) {
			return "Email addresses don't start" + " with dots or @ signs.";
		}
		// Checks for email addresses that start with
		// www. and prints a message if it does.
		p = Pattern.compile("^www\\.");
		m = p.matcher(input);
		if (m.find()) {
			return "Email addresses don't start"
					+ " with \"www.\", only web pages do.";
		}
		p = Pattern.compile("[^A-Za-z0-9\\.\\@_\\-~#]+");
		m = p.matcher(input);
		//StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		boolean deletedIllegalChars = false;

		while (result) {
			deletedIllegalChars = true;
			//m.appendReplacement(sb, "");
			result = m.find();
		}

		// Add the last segment of input to the new String
		//m.appendTail(sb);

		//input = sb.toString();

		if (deletedIllegalChars) {
			return "Address contains incorrect characters"
					+ " , such as spaces or commas.";
		}

		return null;
	}

	public static boolean isNumber(String arg) {
		try {
			Double.parseDouble(arg);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static World world() {
		return World.getInstance();
	}
}
