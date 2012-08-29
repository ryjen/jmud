package net.arg3.jmud.importer.rom;

import java.util.ArrayList;

import net.arg3.jmud.Persistance;
import net.arg3.jmud.Social;
import net.arg3.jmud.importer.FileReader;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SocialSection extends AbstractSection {
	public final ArrayList<Social> Loaded = new ArrayList<Social>();

	public SocialSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException {
		Session s = Persistance.getSession();

		Transaction tx = s.beginTransaction();

		for (Social soc : Loaded) {
			s.saveOrUpdate(soc);
		}

		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " socials");
	}

	@Override
	public Integer load(FileReader reader) {

		Social soc = null;

		for (;;) {
			if (soc != null) {
				Loaded.add(soc);
			}
			String word = reader.readWord();

			if (word.equals("#0"))
				break;

			soc = new Social();
			soc.setName(word);
			reader.readToEol();

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}

			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setCharNoArg(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}

			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setOthersNoArg(word);
			}

			word = reader.readStringEol();
			if (word.equals("#")) {
				continue;
			}

			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 1, 0);
				soc.setCharFound(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}

			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setOthersFound(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}
			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setVictFound(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}
			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setCharNotFound(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}
			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setCharAuto(word);
			}

			word = reader.readStringEol();

			if (word.equals("#")) {
				continue;
			}
			if (!word.equals("$")) {
				word = RomConverter.convertActFormat(word, 0, 1);
				soc.setOthersAuto(word);
			}
		}

		log("loaded ", Integer.toString(Loaded.size()), " socials");
		return Loaded.size();
	}
}
