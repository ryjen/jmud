package net.arg3.jmud.importer.rom;

import java.io.IOException;

import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

import org.hibernate.HibernateException;

public abstract class AbstractSection {

	protected final RomImporter importer;

	public AbstractSection(RomImporter importer) {
		this.importer = importer;
	}

	public abstract void commit() throws HibernateException, ImportException;

	public abstract Integer load(FileReader reader) throws ImportException,
			IOException;

	public void log(String... str) {
		importer.log(str);
	}
}
