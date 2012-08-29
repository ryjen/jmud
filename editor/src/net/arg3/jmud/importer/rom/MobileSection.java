package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Argument;
import net.arg3.jmud.Dice;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.NonPlayer;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.Race;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.Ethos;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MobileSection extends AbstractSection {
	boolean newFormat;

	public final Map<Integer, NonPlayer> Loaded = new HashMap<Integer, NonPlayer>();

	public MobileSection(RomImporter importer) {
		super(importer);
		newFormat = true;
	}

	@Override
	public void commit() throws HibernateException {
		Session s = Persistance.getSession();
		Transaction tx = s.beginTransaction();

		for (Map.Entry<Integer, NonPlayer> entry : Loaded.entrySet()) {

			for (Affect paf : entry.getValue().getAffects()) {
				s.saveOrUpdate(paf);
			}

			// tx.commit();

			// tx = s.beginTransaction();

			s.saveOrUpdate(entry.getValue());
		}

		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " mobiles");
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {

		if (AreaSection.getCurrentArea() == null)
			throw new ImportException("no current area");

		if (newFormat)
			loadNewFormat(reader);
		else
			loadOldFormat(reader);

		log("loaded ", Integer.toString(Loaded.size()), " mobiles");
		return Loaded.size();
	}

	public void setNewFormat(boolean value) {
		newFormat = value;
	}

	private void loadNewFormat(FileReader reader) throws ImportException,
			IOException {
		for (;;) {
			char letter = reader.readLetter();

			if (letter != '#') {
				throw new ImportException("# not found");
			}
			int vnum = reader.readNumber();
			if (vnum == 0)
				break;

			// TODO: check mob exists

			NonPlayer npc = new NonPlayer();

			npc.setArea(AreaSection.getCurrentArea());
			npc.setName(reader.readString());
			npc.setShortDescr(reader.readString());
			npc.setLongDescr(reader.readString());
			npc.setDescription(reader.readString());
			npc.setRace(RomConverter.convertRaceType(reader.readString()));

			npc.getFlags().set(RomConverter.convertNpcFlags(reader.readFlag()));

			npc.getAffectedBy().set(
					RomConverter.convertAffectBits(reader.readFlag())); // affected_by
			npc.setAlignment(reader.readNumber()); // alignment
			npc.setEthos(Jmud.range(Ethos.Lawful.getValue(),
					Ethos.Chaotic.getValue()));
			reader.readNumber(); // group

			npc.setLevel(reader.readNumber());
			npc.setHitroll(reader.readNumber());

			npc.setHitDice(readDice(reader));

			npc.setManaDice(readDice(reader));

			npc.setDamDice(readDice(reader));

			npc.setAttack(RomConverter.convertAttackType(reader.readWord()));

			npc.getBaseAttributes().setResistance(DamType.PIERCE,
					-reader.readNumber());

			npc.getBaseAttributes().setResistance(DamType.BASH,
					-reader.readNumber()); // ac
											// bash;
			npc.getBaseAttributes().setResistance(DamType.SLASH,
					-reader.readNumber()); // ac
											// slash
			npc.getBaseAttributes().setResistance(DamType.MAGIC,
					-reader.readNumber()); // ac
											// exotic

			reader.readFlag(); // offensive flags
			RomConverter.convertImmuneFlags(npc, reader.readFlag()); // immunity
																		// flags
			RomConverter.convertResistanceFlags(npc, reader.readFlag()); // resistance
																			// flags
			RomConverter.convertVulnerabilityFlags(npc, reader.readFlag()); // vulnerability
																			// flags

			npc.setPosition(RomConverter.convertPosition(reader.readWord()));
			npc.setDefaultPosition(RomConverter.convertPosition(reader
					.readWord()));

			npc.setSex(RomConverter.convertSex(reader.readWord()));

			npc.getMoney().addSilver(reader.readNumber()); // wealth

			reader.readFlag(); // form
			reader.readFlag(); // parts

			npc.setSize(RomConverter.convertSize(reader.readWord())); // size
			reader.readWord(); // material

			for (;;) {
				letter = reader.readLetter();

				if (letter == 'F') {
					String word = reader.readWord();
					long vector = reader.readFlag();

					if (word.equalsIgnoreCase("act"))
						npc.getFlags().remove(
								RomConverter.convertNpcFlags(vector));

				} else {
					reader.unRead();
					break;
				}
			}

			Loaded.put(vnum, npc);
		}
	}

	private void loadOldFormat(FileReader reader) throws IOException,
			ImportException {
		for (;;) {
			char letter = reader.readLetter();
			if (letter != '#') {
				throw new ImportException("# not found");
			}
			int vnum = reader.readNumber();
			if (vnum == 0)
				break;

			// TODO: check mob exists

			NonPlayer npc = new NonPlayer();

			npc.setArea(AreaSection.getCurrentArea());
			npc.setName(reader.readString());
			npc.setShortDescr(reader.readString());
			npc.setLongDescr(reader.readString());
			npc.setDescription(reader.readString());

			npc.getFlags().set(RomConverter.convertNpcFlags(reader.readFlag()));

			npc.getAffectedBy().set(
					RomConverter.convertAffectBits(reader.readFlag())); // affected_by
			npc.setAlignment(reader.readNumber()); // alignment
			npc.setEthos(Jmud.range(Ethos.Lawful.getValue(),
					Ethos.Chaotic.getValue()));
			letter = reader.readLetter();

			npc.setLevel(reader.readNumber());

			reader.readNumber();
			reader.readNumber();
			reader.readNumber();
			reader.readLetter();
			reader.readNumber();
			reader.readLetter();
			reader.readNumber();

			reader.readNumber();
			reader.readLetter();
			reader.readNumber();
			reader.readLetter();
			reader.readNumber();

			npc.getMoney().addSilver(reader.readNumber()); // wealth
			reader.readNumber(); // xp?

			npc.setPosition(RomConverter.convertPosition(reader.readNumber())); // start
																				// position
			npc.setPosition(RomConverter.convertPosition(reader.readNumber())); // default
																				// position

			npc.setSex(RomConverter.convertSex(reader.readNumber()));

			Argument arg = new Argument(npc.getName());

			npc.setRace(RomConverter.convertRaceType(arg.getNext()));

			if (npc.getRace() == null) {
				npc.setRace(Race.lookup("unique"));
			}

			if (letter != 'S') {
				throw new ImportException("vnum " + vnum + " bad subformat");
			}

			npc.rollVitals();

			Loaded.put(vnum, npc);
		}
	}

	private Dice readDice(FileReader reader) throws IOException {
		Dice dice = new Dice();

		dice.setNumber(reader.readNumber());
		reader.readLetter();
		dice.setType(reader.readNumber());
		reader.readLetter();
		dice.setBonus(reader.readNumber());

		return dice;

	}
}
