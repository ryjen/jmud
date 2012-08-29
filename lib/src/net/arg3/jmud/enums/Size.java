package net.arg3.jmud.enums;

public enum Size {
	Miniscule(1), Tiny(5), Small(10), Medium(15), Large(25), Giant(35);

	final float value;

	private Size(float value) {
		this.value = value;
	}

	public final float getValue() {
		return value;
	}
}
