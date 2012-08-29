package net.arg3.jmud.enums;

public enum Alignment {
	Good(500), Neutral(0), Evil(-500);

	final int value;

	Alignment(int value) {
		this.value = value;
	}

	public final Alignment fromValue(int value) {
		if (value >= Good.getValue())
			return Good;
		if (value > Evil.getValue())
			return Neutral;

		return Evil;
	}

	public final int getValue() {
		return value;
	}

	public static boolean isGood(int value) {
		return value >= Good.getValue();
	}

	public static boolean isNeutral(int value) {
		return value < Good.getValue() && value > Evil.getValue();
	}

	public static boolean isEvil(int value) {
		return value <= Evil.getValue();
	}
}
