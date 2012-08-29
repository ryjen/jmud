package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Object;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.ScrollPotionPillObject;
import net.arg3.jmud.WandStaffObject;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;
import net.arg3.jmud.objects.ArmorObject;
import net.arg3.jmud.objects.ContainerObject;
import net.arg3.jmud.objects.FoodObject;
import net.arg3.jmud.objects.FurnitureObject;
import net.arg3.jmud.objects.LightObject;
import net.arg3.jmud.objects.LiquidContainerObject;
import net.arg3.jmud.objects.MoneyObject;
import net.arg3.jmud.objects.PortalObject;
import net.arg3.jmud.objects.WeaponObject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ObjectSection extends AbstractSection {
	boolean newFormat;
	public final Map<Integer, Object> Loaded = new HashMap<Integer, Object>();

	public ObjectSection(RomImporter importer) {
		super(importer);
		this.newFormat = true;
	}

	@Override
	public void commit() throws HibernateException {
		Session s = Persistance.getSession();
		Transaction tx = s.beginTransaction();

		for (Map.Entry<Integer, Object> entry : Loaded.entrySet()) {

			for (Affect paf : entry.getValue().getAffects())
				s.saveOrUpdate(paf);

			s.saveOrUpdate(entry.getValue());
		}

		tx.commit();

		log("commited ", Integer.toString(Loaded.size()), " objects");
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		if (AreaSection.getCurrentArea() == null)
			throw new ImportException("no current area");

		if (newFormat)
			loadNewFormat(reader);
		else
			loadOldFormat(reader);

		log("loaded ", Integer.toString(Loaded.size()), " objects");
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

			if (importer.Objects.Loaded.containsKey(vnum))
				throw new ImportException("object vnum already exists " + vnum);

			String name = reader.readString();
			String shortDescr = reader.readString();
			String longDescr = reader.readString();

			reader.readString(); // material

			Object obj = RomConverter.createObjectType(reader.readWord());

			obj.setArea(AreaSection.getCurrentArea());

			obj.setName(name);
			obj.setShortDescr(shortDescr);
			obj.setLongDescr(longDescr);
			obj.getFlags().set(RomConverter.convertObjFlags(reader.readFlag())); // extra
																					// flags
			obj.getWearFlags().set(
					RomConverter.convertWearFlags(reader.readFlag())); // wear
																		// flags

			readObjValues(obj, reader);

			obj.setLevel(reader.readNumber());
			obj.setWeight((short) reader.readNumber());
			obj.getCost().addSilver(reader.readNumber());

			letter = reader.readLetter();

			switch (letter) {
			default:
			case 'P':
				obj.setCondition(100);
				break;
			case 'G':
				obj.setCondition(90);
				break;
			case 'A':
				obj.setCondition(75);
				break;
			case 'W':
				obj.setCondition(50);
				break;
			case 'D':
				obj.setCondition(25);
				break;
			case 'B':
				obj.setCondition(10);
				break;
			case 'R':
				obj.setCondition(0);
				break;
			}

			for (;;) {
				letter = reader.readLetter();

				if (letter == 'A') {
					// TODO: add affects
					Affect paf = new Affect();

					paf.setLocation(RomConverter.convertApplyType(reader
							.readNumber()));
					paf.setModifier(reader.readNumber());

					paf.setLevel(obj.getLevel());
					paf.setDuration(-1);
					obj.addAffect(paf);

				} else if (letter == 'F') {
					letter = reader.readLetter();

					reader.readNumber();

					Affect paf = new Affect();
					paf.setModifier(reader.readNumber());
					paf.setFlag(RomConverter.convertAffectBits(reader
							.readFlag()));

					paf.setLevel(obj.getLevel());
					paf.setDuration(-1);
					obj.addAffect(paf);

				} else if (letter == 'E') {
					obj.setExtraDescr(reader.readString(), reader.readString());
				} else {
					reader.unRead();
					break;
				}
			}
			Loaded.put(vnum, obj);
		}
	}

	private void readObjValues(Object obj, FileReader reader)
			throws ImportException, IOException {
		if (obj instanceof WeaponObject) {
			WeaponObject o = (WeaponObject) obj;

			o.setWeaponClass(RomConverter.convertWeaponClass(reader.readWord()));
			o.setDiceNumber(reader.readNumber());
			o.setDiceType(reader.readNumber());
			o.setAttack(RomConverter.convertAttackType(reader.readWord()));
			o.setWeaponFlags(RomConverter.convertWeaponFlags(reader.readFlag()));
		} else if (obj instanceof ContainerObject) {
			ContainerObject o = (ContainerObject) obj;

			o.setMaxWeight(reader.readNumber());
			o.setContainerFlags(RomConverter.convertContainerFlags(reader
					.readFlag()));
			o.setKey(reader.readNumber());
			reader.readFlag();
			reader.readFlag();
		} else if (obj instanceof LiquidContainerObject) {

			LiquidContainerObject o = (LiquidContainerObject) obj;

			o.setLiquidTotal(reader.readNumber());
			o.setLiquidLeft(reader.readNumber());
			o.setLiquid(reader.readWord());
			o.setPoisoned(reader.readNumber() != 0 ? true : false);
			reader.readFlag();
		} else if (obj instanceof WandStaffObject) {
			WandStaffObject o = (WandStaffObject) obj;

			o.setSpellLevel(reader.readNumber());
			o.setChargesTotal(reader.readNumber());
			o.setChargesRemaining(reader.readNumber());
			o.setSpell(RomConverter.convertSpellName(reader.readWord()));
			reader.readFlag();
		} else if (obj instanceof ScrollPotionPillObject) {
			ScrollPotionPillObject o = (ScrollPotionPillObject) obj;

			o.setSpellLevel(reader.readNumber());
			o.addSpell(RomConverter.convertSpellName(reader.readWord()));
			o.addSpell(RomConverter.convertSpellName(reader.readWord()));
			o.addSpell(RomConverter.convertSpellName(reader.readWord()));
			o.addSpell(RomConverter.convertSpellName(reader.readWord()));
		} else if (obj instanceof LightObject) {
			LightObject o = (LightObject) obj;

			o.setLightRemaining((int) reader.readFlag());
			o.setLightTotal((int) reader.readFlag());

			reader.readFlag();
			reader.readFlag();
			reader.readFlag();

			if (o.getLightTotal() < o.getLightRemaining())
				o.setLightTotal(o.getLightRemaining());
		} else if (obj instanceof ArmorObject) {
			ArmorObject o = (ArmorObject) obj;

			o.setResistance(DamType.PIERCE, (int) -reader.readFlag());
			o.setResistance(DamType.BASH, (int) -reader.readFlag());
			o.setResistance(DamType.SLASH, (int) -reader.readFlag());
			reader.readFlag();
			reader.readFlag();
		} else if (obj instanceof FoodObject) {
			FoodObject o = (FoodObject) obj;

			o.setFillValue((int) reader.readFlag());
			o.setPoisoned(reader.readFlag() != 0 ? true : false);
			reader.readFlag();
			reader.readFlag();
			reader.readFlag();
		} else if (obj instanceof FurnitureObject) {
			FurnitureObject o = (FurnitureObject) obj;

			reader.readFlag();
			reader.readFlag();
			o.setFurnitureFlags(RomConverter.covertFurnitureFlag(reader
					.readFlag()));
			o.setHealRate((int) reader.readFlag());
			o.setManaRate((int) reader.readFlag());
		} else if (obj instanceof MoneyObject) {
			MoneyObject o = (MoneyObject) obj;

			o.getCost().addSilver(reader.readFlag());
			o.getCost().addGold(reader.readFlag());
			reader.readFlag();
			reader.readFlag();
			reader.readFlag();
		} else if (obj instanceof PortalObject) {
			PortalObject o = (PortalObject) obj;

			o.setCharges((int) reader.readFlag());
			o.setExitFlags(RomConverter.convertExitFlags((int) reader
					.readFlag()));
			o.setGateFlags(RomConverter.convertGateFlags((int) reader
					.readFlag()));
			o.setLocation(reader.readFlag());
			o.setKey(reader.readFlag());
		} else {
			reader.readFlag();
			reader.readFlag();
			reader.readFlag();
			reader.readFlag();
			reader.readFlag();
		}

	}

	private void loadOldFormat(FileReader reader) throws ImportException,
			IOException {
		for (;;) {
			char letter = reader.readLetter();
			if (letter != '#') {
				throw new ImportException("# not found");
			}

			int vnum = reader.readNumber();

			if (vnum == 0)
				break;

			// TODO: check obj exists

			String name = reader.readString();
			String shortDescr = reader.readString();
			String longDescr = reader.readString();
			reader.readString();

			Object obj = RomConverter.createObjectType(reader.readNumber());

			obj.setName(name);
			obj.setShortDescr(shortDescr);
			obj.setLongDescr(longDescr);

			obj.setArea(AreaSection.getCurrentArea());

			obj.getFlags().set(RomConverter.convertObjFlags(reader.readFlag()));
			obj.getWearFlags().set(
					RomConverter.convertWearFlags(reader.readFlag()));

			reader.readNumber();
			reader.readNumber();
			reader.readNumber();
			reader.readNumber();

			obj.setWeight((short) reader.readNumber());
			obj.getCost().addSilver(reader.readNumber());

			reader.readNumber();

			for (;;) {
				letter = reader.readLetter();

				if (letter == 'A') {
					Affect paf = new Affect();
					paf.setLocation(RomConverter.convertApplyType(reader
							.readNumber()));
					paf.setModifier(reader.readNumber());

					paf.setLevel(obj.getLevel());
					paf.setDuration(-1);
					obj.addAffect(paf);

				} else if (letter == 'E') {
					obj.setExtraDescr(reader.readString(), reader.readString());
				} else {
					reader.unRead();
					break;
				}
			}

			Loaded.put(vnum, obj);
		}
	}
}
