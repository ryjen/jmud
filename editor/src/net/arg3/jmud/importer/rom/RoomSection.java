package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.arg3.jmud.Persistance;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;
import net.arg3.jmud.model.Exit;
import net.arg3.jmud.model.Room;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RoomSection extends AbstractSection {

	public final Map<Integer, Room> Loaded = new HashMap<Integer, Room>();
	public final Map<Exit, Integer> Exits = new HashMap<Exit, Integer>();

	public RoomSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException {
		Session s = Persistance.getSession();

		Transaction tx = s.beginTransaction();

		for (Map.Entry<Exit, Integer> entry : Exits.entrySet()) {

			if (!Loaded.containsKey(entry.getValue())) {
				log("no exit for vnum ", Integer.toString(entry.getValue()));
				continue;
			}
			Room toRoom = Loaded.get(entry.getValue());

			Exit exit = entry.getKey();

			exit.setToRoom(toRoom);
		}

		for (Map.Entry<Integer, Room> entry : Loaded.entrySet()) {
			s.saveOrUpdate(entry.getValue());
		}

		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " rooms");
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		if (AreaSection.getCurrentArea() == null) {
			throw new ImportException("no current area");
		}

		for (;;) {
			char letter = reader.readLetter();

			if (letter != '#') {
				throw new ImportException("# not found");
			}

			int vnum = reader.readNumber();

			if (vnum == 0)
				break;

			// TODO: Check if room exists

			Room room = new Room();
			room.setArea(AreaSection.getCurrentArea());
			room.setName(reader.readString());

			room.setDescription(reader.readString());
			reader.readNumber();

			room.getFlags().set(
					RomConverter.convertRoomFlags(reader.readFlag()));

			room.setSector(RomConverter.convertSector(reader.readNumber()));

			for (;;) {
				letter = reader.readLetter();

				if (letter == 'S')
					break;

				if (letter == 'H') {
					// TODO: add heal rate
					reader.readNumber();
				} else if (letter == 'M') {
					// TODO: add mana rate
					reader.readNumber();
				} else if (letter == 'C') {
					// TODO: add clan
					reader.readString();
				} else if (letter == 'D') {
					int door = reader.readNumber();
					if (door < 0 || door > 5) {
						throw new ImportException("vnum " + vnum
								+ " has bad door number");
					}

					Exit exit = new Exit();
					exit.setDirection(RomConverter.convertDirection(door));
					exit.setDescription(reader.readString());
					exit.setKeyword(reader.readString());
					int locks = reader.readNumber();
					exit.setKey(reader.readNumber());
					int toRoomId = reader.readNumber();
					exit.setRoom(room);

					Exits.put(exit, toRoomId);

					room.getExits().put(exit.getDirection(), exit);

					switch (locks) {
					case 0:
						break;
					case 1:
						exit.setSaveInfo(Exit.DOOR | Exit.CLOSED);
						break;
					case 2:
						exit.setSaveInfo(Exit.DOOR | Exit.CLOSED | Exit.LOCKED);
						break;
					case 3:
					case 4:
					case 5:
						exit.setSaveInfo(Exit.DOOR | Exit.CLOSED | Exit.LOCKED
								| Exit.SECURE);
						break;
					}

				} else if (letter == 'E') {
					// TODO: extra descriptions
					room.setExtraDescr(reader.readString(), reader.readString()); // description
				} else if (letter == 'O') {
					reader.readString(); // owner
				} else {
					throw new ImportException("vnum " + vnum
							+ " has invalid subsection");
				}
			}
			Loaded.put(vnum, room);
		}

		log("loaded ", Integer.toString(Loaded.size()), " rooms");
		return Loaded.size();
	}
}
