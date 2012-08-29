package net.arg3.jmud.interfaces;

public interface IEnvironmental extends IAffectable {
	String getExtraDescr(String keyword);

	int getLevel();

	String getName();

	void setExtraDescr(String keyword, String value);

	void setLevel(int value);

	void setName(String value);
}
