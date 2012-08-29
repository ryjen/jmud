package net.arg3.jmud.importer.rom;

import java.io.File;
import java.io.IOException;

import net.arg3.jmud.importer.AbstractImporter;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

public class RomImporter extends AbstractImporter {

	public final AreaSection Area;
	public final MobileSection Mobiles;
	public final ObjectSection Objects;
	public final RoomSection Rooms;
	public final ResetSection Resets;
	public final SpecialSection Specials;
	public final ShopSection Shops;
	public final HelpSection Helps;
	public final SocialSection Socials;

	public RomImporter() {
		Area = new AreaSection(this);
		Mobiles = new MobileSection(this);
		Objects = new ObjectSection(this);
		Rooms = new RoomSection(this);
		Resets = new ResetSection(this);
		Specials = new SpecialSection(this);
		Shops = new ShopSection(this);
		Socials = new SocialSection(this);
		Helps = new HelpSection(this);
	}

	@Override
	public int commit() {
		try {
			Area.commit();
			Mobiles.commit();
			Objects.commit();
			Rooms.commit();
			Resets.commit();
			Specials.commit();
			Shops.commit();
			return Area.Loaded.size();
		} catch (Exception ex) {
			log("Unable to commit: ", ex.toString());
			return 0;
		}
	}

	@Override
	protected Integer doInBackground() {
		try {
			if (reader.getFile().getName().endsWith(".lst"))
				loadList();
			else
				loadArea(reader);

		} catch (Exception ex) {
			log("caught exception: ", ex.getMessage(), ", line ",
					Integer.toString(reader.getLine()));
		}
		return Area.Loaded.size();
	}

	protected void loadArea(FileReader reader) throws IOException,
			ImportException {
		String word;
		while (!reader.EOF()) {
			if (reader.readLetter() != '#') {
				publish("# not found.");
				return;
			}

			word = reader.readWord();

			if (word.charAt(0) == '$') {
				return;
			} else if (word.equalsIgnoreCase("AREA")) {
				Area.setNewFormat(false);
				Area.load(reader);
			} else if (word.equalsIgnoreCase("AREADATA")) {
				Area.setNewFormat(true);
				Area.load(reader);
			} else if (word.equalsIgnoreCase("HELPS")) {
				Helps.load(reader);
			} else if (word.equalsIgnoreCase("MOBOLD")) {
				Mobiles.setNewFormat(false);
				Mobiles.load(reader);
			} else if (word.equalsIgnoreCase("MOBILES")) {
				Mobiles.setNewFormat(true);
				Mobiles.load(reader);
			} else if (word.equalsIgnoreCase("OBJOLD")) {
				Objects.setNewFormat(false);
				Objects.load(reader);
			} else if (word.equalsIgnoreCase("OBJECTS")) {
				Objects.setNewFormat(true);
				Objects.load(reader);
			} else if (word.equalsIgnoreCase("RESETS")) {
				Resets.load(reader);
			} else if (word.equalsIgnoreCase("ROOMS")) {
				Rooms.load(reader);
			} else if (word.equalsIgnoreCase("SHOPS")) {
				Shops.load(reader);
			} else if (word.equalsIgnoreCase("SOCIALS")) {
				Socials.load(reader);
			} else if (word.equalsIgnoreCase("SPECIALS")) {
				Specials.load(reader);
			} else {
				log("bad section name.");
			}
		}
	}

	protected void loadList() {
		for (;;) {
			String file = reader.getFile().getParent() + File.separator
					+ reader.readWord();

			if (file.endsWith("$"))
				break;

			FileReader areaReader = null;

			try {
				areaReader = new FileReader(file);

				loadArea(areaReader);

			} catch (Exception ex) {
				if (areaReader != null)
					log("caught exception: ", ex.toString(), ", line ",
							Integer.toString(areaReader.getLine()));
				else
					log("unable to load area: ", ex.toString());
			}
		}
	}

}
