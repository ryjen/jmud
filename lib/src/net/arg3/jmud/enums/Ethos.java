package net.arg3.jmud.enums;

public enum Ethos {
	Lawful(500), Neutral(0), Chaotic(-500);

	final int value;

	Ethos(int value) {
		this.value = value;
	}

	public final Ethos fromValue(int value) {
		if (value >= Lawful.getValue())
			return Lawful;
		if (value > Chaotic.getValue())
			return Neutral;

		return Chaotic;
	}

	public final int getValue() {
		return value;
	}
}
