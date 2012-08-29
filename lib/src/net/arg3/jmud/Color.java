package net.arg3.jmud;

import java.util.HashMap;
import java.util.Map;

public class Color {
	public enum ColorType {
		None(0), Black(30), Blue(34), Cyan(36), Green(32), Magenta(35), Red(31), White(
				37), Yellow(33);

		int value;

		ColorType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static final char CODE = '~';

	private static final HashMap<java.lang.Character, Integer> attrMap = new HashMap<java.lang.Character, Integer>();

	private static final HashMap<java.lang.Character, ColorType> colorMap = new HashMap<java.lang.Character, ColorType>();
	static final int Bright = (1 << 0);
	static final int Background = (1 << 1);
	static final int Blink = (1 << 2);
	static final int Reverse = (1 << 3);

	static final int Underscore = (1 << 4);

	static final String ESCAPE = "\033";

	static {
		attrMap.put('!', Background);
		attrMap.put('@', Blink);
		attrMap.put('#', Reverse);
		attrMap.put('%', Underscore);

		colorMap.put('g', ColorType.Green);
		colorMap.put('r', ColorType.Red);
		colorMap.put('b', ColorType.Blue);
		colorMap.put('y', ColorType.Yellow);
		colorMap.put('m', ColorType.Magenta);
		colorMap.put('c', ColorType.Cyan);
		colorMap.put('d', ColorType.Black);
		colorMap.put('w', ColorType.White);
	}

	public static char[] convert(char[] buffer, boolean onOff) {
		String str = convert(new String(buffer), onOff);

		return str.toCharArray();
	}

	public static String convert(String text, boolean onOff) {
		if (text == null)
			return "";

		int index = text.indexOf(CODE, 0);

		if (index == -1)
			return text;

		int lastIndex = 0;
		Color color = new Color();

		StringBuilder buf = new StringBuilder();

		while (index != -1) {
			buf.append(text.substring(lastIndex, index));

			// increment, and check for trailing codes
			if (++index > text.length()) {
				buf.append(CODE);
				break;
			}

			switch (text.charAt(index)) {
			case 'n':
				buf.append(World.getInstance().getName());
				break;
			case 'N':
				buf.append(World.getInstance().getName().toUpperCase());
				break;
			case CODE:
				buf.append(CODE);
				break;
			case 'x':
				if (onOff) {
					color.Clear();
					buf.append(color);
				}
				break;
			default:
				if (onOff) {
					index = color.Parse(text, index);
					buf.append(color);
				}
				break;
			}

			lastIndex = ++index;
			index = text.indexOf(CODE, index);
		}

		buf.append(text.substring(lastIndex));

		return buf.toString();
	}

	int flags;

	ColorType value;

	public Color() {
		value = ColorType.None;
	}

	public Color(ColorType type) {
		value = type;
	}

	public Color(ColorType type, int cFlags) {
		value = type;
		flags = cFlags;
	}

	public void Clear() {
		value = ColorType.None;
		flags = 0;
	}

	public String Format() {
		StringBuilder buf = new StringBuilder();

		for (Map.Entry<java.lang.Character, Integer> entry : attrMap.entrySet()) {
			if ((flags & entry.getValue()) != 0) {
				buf.append(entry.getKey());
			}
		}

		for (Map.Entry<java.lang.Character, ColorType> entry : colorMap
				.entrySet()) {
			if (value != entry.getValue())
				continue;

			if ((flags & Bright) != 0) {
				buf.append(java.lang.Character.toUpperCase(entry.getKey()));
			} else {
				buf.append(entry.getKey());
			}
			break;
		}

		return buf.toString();
	}

	public int getFlags() {
		return flags;
	}

	public ColorType getValue() {
		return value;
	}

	public int Parse(String buf, int index) {
		do {
			if (attrMap.containsKey(buf.charAt(index))) {
				flags |= attrMap.get(buf.charAt(index));
				continue;
			}

			if (java.lang.Character.isLetter(buf.charAt(index))) {
				if (java.lang.Character.isUpperCase(buf.charAt(index)))
					flags |= Bright;
				else
					flags &= ~Bright;

				char key = java.lang.Character.toLowerCase(buf.charAt(index));

				if (colorMap.containsKey(key))
					value = colorMap.get(key);
			} else if (buf.charAt(index) == '?') {
				ColorType[] types = ColorType.values();
				if (Jmud.range(0, 10) <= 5)
					flags |= Bright;
				value = types[Jmud.range(1, types.length)];
			}

			break;
		} while (++index < buf.length());

		return index;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();

		buf.append(ESCAPE);
		buf.append("[");

		if ((flags & Bright) != 0) {
			buf.append("1;");
		} else {
			buf.append("0;");
		}

		if ((flags & Blink) != 0) {
			buf.append("5;");
		}

		if ((flags & Reverse) != 0) {
			buf.append("7;");
		}

		if ((flags & Underscore) != 0) {
			buf.append("4;");
		}

		if (value != ColorType.None) {
			int temp = value.getValue();

			if ((flags & Background) != 0) {
				temp += 10;
			}
			buf.append(temp);
		} else {
			buf.append("0");
		}

		buf.append("m");

		return buf.toString();
	}
}
