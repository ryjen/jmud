package net.arg3.jmud.importer.rom;

import java.io.IOException;

import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

import org.hibernate.HibernateException;

public class SpecialSection extends AbstractSection {

	public SpecialSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException {
		log("did not commit any specials");
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		for (;;) {
			char letter = reader.readLetter();
			switch (letter) {
			default:
				throw new ImportException("bad special " + letter);
			case 'S':

				log("skipped specials section");
				return 0;
			case '*':
				break;
			case 'M':
				reader.readNumber();
				reader.readWord();
				break;
			}
			reader.readToEol();
		}

	}

}
