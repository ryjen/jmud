package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.arg3.jmud.Persistance;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.model.Area;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AreaSection extends AbstractSection {
	public static Area getCurrentArea() {
		return area;
	}

	boolean newFormat;
	static Area area;

	final ConcurrentLinkedQueue<Area> Loaded = new ConcurrentLinkedQueue<Area>();

	public long upperVnum;

	public long lowerVnum;

	public AreaSection(RomImporter importer) {
		super(importer);
		this.newFormat = true;
	}

	@Override
	public void commit() throws HibernateException {

		Session s = Persistance.getSession();
		Transaction tx = s.beginTransaction();

		for (Area a : Loaded)
			s.saveOrUpdate(a);

		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " areas");
	}

	@Override
	public Integer load(FileReader reader) throws IOException {

		if (newFormat)
			area = loadArea(reader);
		else
			area = loadOldArea(reader);

		Loaded.add(area);

		log("loading ", area.getName());

		return 1;
	}

	public void setNewFormat(boolean value) {
		newFormat = value;
	}

	private Area loadArea(FileReader reader) throws IOException {
		Area area = new Area();
		for (;;) {
			String word = reader.EOF() ? "End" : reader.readWord();

			switch (Character.toUpperCase(word.charAt(0))) {
			case 'C':
				if (word.equalsIgnoreCase("Credits"))
					area.setCredits(reader.readString());
				break;
			case 'N':
				if (word.equalsIgnoreCase("Name"))
					area.setName(reader.readString());
				break;
			case 'S':
				if (word.equalsIgnoreCase("Security"))
					reader.readNumber();
				break;
			case 'V':
				if (word.equalsIgnoreCase("VNUMs")) {
					lowerVnum = reader.readNumber();
					upperVnum = reader.readNumber();
				}
				break;
			case 'E':
				if (word.equalsIgnoreCase("End")) {
					return area;
				}
				break;
			case 'B':
				if (word.equalsIgnoreCase("Builders"))
					reader.readString();
				break;
			}
		}
	}

	private Area loadOldArea(FileReader reader) throws IOException {
		Area area = new Area();

		reader.readString();

		area.setName(reader.readString());

		area.setCredits(reader.readString());

		lowerVnum = reader.readNumber();
		upperVnum = reader.readNumber();

		return area;

	}
}
