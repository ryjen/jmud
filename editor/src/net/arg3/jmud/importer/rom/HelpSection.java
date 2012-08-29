package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.ArrayList;

import net.arg3.jmud.Argument;
import net.arg3.jmud.Help;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HelpSection extends AbstractSection {
	public final ArrayList<Help> Loaded = new ArrayList<Help>();

	public HelpSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException {
		Transaction tx = null;

		Session s = Persistance.getSession();

		tx = s.beginTransaction();
		for (Help help : Loaded) {
			s.saveOrUpdate(help);
		}

		log("commited ", Integer.toString(Loaded.size()), " helps");
		tx.commit();

	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		for (;;) {
			Help help = new Help();

			help.setLevel(reader.readNumber());
			Argument keywords = new Argument(reader.readString());

			if (keywords.toString().charAt(0) == '$')
				break;

			while (!keywords.isNullOrEmpty())
				help.getKeywords().add(keywords.getNext());

			help.setText(reader.readString());

			Loaded.add(help);
		}
		log("loaded ", Integer.toString(Loaded.size()), " helps");
		return Loaded.size();
	}
}
