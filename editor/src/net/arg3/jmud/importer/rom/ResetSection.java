package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.ArrayList;

import net.arg3.jmud.Persistance;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;
import net.arg3.jmud.model.Reset;
import net.arg3.jmud.model.Room;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ResetSection extends AbstractSection {

	class TempReset {
		public int arg1;
		public int arg2;
		public int arg3;
		public int arg4;
		public char command;
		public final ArrayList<TempReset> subResets = new ArrayList<TempReset>();
	}

	public final ArrayList<TempReset> Loaded = new ArrayList<TempReset>();

	public ResetSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException, ImportException {
		Session s = Persistance.getSession();
		Transaction tx = s.beginTransaction();

		for (TempReset temp : Loaded) {

			Reset reset = new Reset();

			StringBuilder buf = new StringBuilder();

			switch (temp.command) {
			case 'M':
				if (!importer.Mobiles.Loaded.containsKey(temp.arg1)) {
					log("bad reset for mobile ", Integer.toString(temp.arg1));
					continue;
				}
				buf.append("npc(");
				buf.append(importer.Mobiles.Loaded.get(temp.arg1).getId());
				buf.append(",");
				buf.append(temp.arg4); // max room
				buf.append(")\n");
				break;
			case 'O':
				if (!importer.Objects.Loaded.containsKey(temp.arg1)) {
					log("bad reset for object " + Integer.toString(temp.arg1));
					continue;
				}

				buf.append("obj(");
				buf.append(importer.Objects.Loaded.get(temp.arg1).getId());
				buf.append(")\n");
				break;
			case 'D':
			case 'R':
			default:
				throw new ImportException("invalid top level reset");
			}

			if (!importer.Rooms.Loaded.containsKey(temp.arg3)) {
				log("bad reset for room ", Integer.toString(temp.arg3));
				continue;
			}

			Room room = importer.Rooms.Loaded.get(temp.arg3);

			for (TempReset sub : temp.subResets) {
				switch (sub.command) {
				case 'P':
					if (!importer.Objects.Loaded.containsKey(sub.arg1)) {
						throw new ImportException("bad put reset for object "
								+ sub.arg1);
					}
					buf.append("put(");
					buf.append(importer.Objects.Loaded.get(sub.arg1).getId());
					buf.append(")\n");
					break;
				case 'G':
					if (!importer.Objects.Loaded.containsKey(sub.arg1)) {
						throw new ImportException("bad give reset for object "
								+ sub.arg1);
					}
					buf.append("give(");
					buf.append(importer.Objects.Loaded.get(sub.arg1).getId());
					buf.append(")\n");
					break;
				case 'E':
					if (!importer.Objects.Loaded.containsKey(sub.arg1)) {
						throw new ImportException("bad equip reset for object "
								+ sub.arg1);
					}
					buf.append("equip(");
					buf.append(importer.Objects.Loaded.get(sub.arg1).getId());
					buf.append(")\n");
					break;
				}
			}

			reset.setCode(buf.toString());

			reset.setRoom(room);

			room.setResetScript(reset);

			s.saveOrUpdate(reset);

			s.saveOrUpdate(room);
		}
		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " resets");
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		if (AreaSection.getCurrentArea() == null)
			throw new ImportException("no current area");

		TempReset iLastObj = null;
		TempReset iLastRoom = null;

		for (;;) {
			char letter = reader.readLetter();
			if (letter == 'S')
				break;

			if (letter == '*') {
				reader.readToEol();
				continue;
			}
			TempReset temp = new TempReset();

			temp.command = letter;

			reader.readNumber(); // unused
			temp.arg1 = reader.readNumber();
			temp.arg2 = reader.readNumber();
			temp.arg3 = (letter == 'G' || letter == 'R') ? 0 : reader
					.readNumber();
			temp.arg4 = (letter == 'P' || letter == 'M') ? reader.readNumber()
					: 0;

			reader.readToEol();

			switch (letter) {
			default:
				log("bad type " + letter);
				break;
			case 'M':
				if (!importer.Mobiles.Loaded.containsKey(temp.arg1))
					throw new ImportException("invalid mobile for reset "
							+ temp.arg1);
				if (!importer.Rooms.Loaded.containsKey(temp.arg3))
					throw new ImportException("invalid room for reset "
							+ temp.arg3);

				iLastRoom = temp;
				break;
			case 'O':
				if (!importer.Objects.Loaded.containsKey(temp.arg1))
					throw new ImportException("invalid object for reset "
							+ temp.arg1);
				if (!importer.Rooms.Loaded.containsKey(temp.arg3))
					throw new ImportException("invalid room for reset "
							+ temp.arg3);

				iLastObj = temp;
				break;
			case 'P':
				if (!importer.Objects.Loaded.containsKey(temp.arg1))
					throw new ImportException("invalid put for reset "
							+ temp.arg1);

				if (iLastObj == null)
					throw new ImportException(
							"no previous object for put reset");

				iLastObj.subResets.add(temp);
				break;
			case 'G':
			case 'E':
				if (!importer.Objects.Loaded.containsKey(temp.arg1))
					throw new ImportException("invalid give/equip for reset"
							+ temp.arg1);

				if (iLastRoom == null)
					throw new ImportException(
							"no previous reset for give/equip");

				iLastRoom.subResets.add(temp);
				iLastObj = iLastRoom;
				break;

			case 'D':
				// TODO: random exits
				break;
			case 'R':
				// TODO: random exits
				break;
			}

			if (temp.command == 'M' || temp.command == 'O')
				Loaded.add(temp);
		}
		log("loaded ", Integer.toString(Loaded.size()), " resets");
		return Loaded.size();
	}
}
