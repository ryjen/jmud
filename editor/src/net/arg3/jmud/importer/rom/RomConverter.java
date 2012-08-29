/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud.importer.rom;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.arg3.jmud.Affect;
import net.arg3.jmud.Attack;
import net.arg3.jmud.Exit;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.NonPlayer;
import net.arg3.jmud.Object;
import net.arg3.jmud.Race;
import net.arg3.jmud.Room;
import net.arg3.jmud.Spell;
import net.arg3.jmud.WearFlags;
import net.arg3.jmud.enums.ApplyType;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.Direction;
import net.arg3.jmud.enums.Position;
import net.arg3.jmud.enums.Sector;
import net.arg3.jmud.enums.Sex;
import net.arg3.jmud.enums.Size;
import net.arg3.jmud.enums.WeaponClass;
import net.arg3.jmud.importer.ImportException;
import net.arg3.jmud.objects.ArmorObject;
import net.arg3.jmud.objects.ContainerObject;
import net.arg3.jmud.objects.FoodObject;
import net.arg3.jmud.objects.FurnitureObject;
import net.arg3.jmud.objects.KeyObject;
import net.arg3.jmud.objects.LightObject;
import net.arg3.jmud.objects.LiquidContainerObject;
import net.arg3.jmud.objects.MapObject;
import net.arg3.jmud.objects.MoneyObject;
import net.arg3.jmud.objects.PillObject;
import net.arg3.jmud.objects.PortalObject;
import net.arg3.jmud.objects.PotionObject;
import net.arg3.jmud.objects.RaftObject;
import net.arg3.jmud.objects.ScrollObject;
import net.arg3.jmud.objects.StaffObject;
import net.arg3.jmud.objects.TrashObject;
import net.arg3.jmud.objects.TreasureObject;
import net.arg3.jmud.objects.WandObject;
import net.arg3.jmud.objects.WarpStoneObject;
import net.arg3.jmud.objects.WeaponObject;

/**
 * 
 * @author Ryan
 */
public class RomConverter {

	static final long A = 1;
	static final long B = 2;
	static final long C = 4;
	static final long D = 8;
	static final long E = 16;
	static final long F = 32;
	static final long G = 64;
	static final long H = 128;
	static final long I = 256;
	static final long J = 512;
	static final long K = 1024;
	static final long L = 2048;
	static final long M = 4096;
	static final long N = 8192;
	static final long O = 16384;
	static final long P = 32768;
	static final long Q = 65536;
	static final long R = 131072;
	static final long S = 262144;
	static final long T = 524288;
	static final long U = 1048576;
	static final long V = 2097152;
	static final long W = 4194304;
	static final long X = 8388608;
	static final long Y = 16777216;
	static final long Z = 33554432;
	static final long aa = 67108864; /* doubled due to conflicts */
	static final long bb = 134217728;
	static final long cc = 268435456;
	static final long dd = 536870912;
	static final long ee = 1073741824;

	static HashMap<Long, Integer> exitFlagTable;

	static HashMap<Long, DamType> resImmVulnFlags;

	public static String convertActFormat(String str, int arg1, int arg2) {

		if (str.length() < 2) {
			return str;
		}
		int index = str.indexOf('$');
		StringBuilder buf = new StringBuilder();
		int lastIndex = 0;

		while (index != -1) {
			buf.append(str.substring(lastIndex, index++));

			switch (str.charAt(index)) {
			case 'n':
				buf.append("{").append(arg1).append("}");
				break;
			case 'N':
				buf.append("{").append(arg2).append("}");
				break;
			case 'e':
				buf.append("{").append(arg1).append(":E}");
				break;
			case 'E':
				buf.append("{").append(arg2).append(":E}");
				break;
			case 'm':
				buf.append("{").append(arg1).append(":M}");
				break;
			case 'M':
				buf.append("{").append(arg2).append(":M}");
				break;
			case 's':
				buf.append("{").append(arg1).append(":S}");
				break;
			case 'S':
				buf.append("{").append(arg2).append(":S}");
				break;
			case 't':
			case 'p':
				buf.append("{").append(arg1).append("}");
				break;
			case 'T':
			case 'P':
				buf.append("{").append(arg2).append("}");
				break;
			}
			lastIndex = ++index;
			index = str.indexOf('$', index);
		}

		buf.append(str.substring(lastIndex));

		return buf.toString();
	}

	static Map<Long, Long> objFlags;

	public static long convertObjFlags(long flags) {
		if (objFlags == null) {
			objFlags = new HashMap<Long, Long>();

			objFlags.put(F, Object.INVIS);
			objFlags.put(G, Object.MAGIC);
			objFlags.put(H, Object.NODROP);
			objFlags.put(M, Object.NOREMOVE);
			objFlags.put(P, Object.ROTDEATH);
			objFlags.put(Y, Object.BURNPROOF);
		}

		long rval = 0;

		for (Map.Entry<Long, Long> entry : objFlags.entrySet()) {
			if ((flags & entry.getKey()) != 0) {
				rval |= entry.getValue();
			}
		}

		return rval;
	}

	public static Direction convertDirection(int value) {
		return Direction.fromOrdinal(value);
	}

	public static int convertExitFlags(int value) {
		if (exitFlagTable == null) {
			exitFlagTable = new HashMap<Long, Integer>();
			/*
			 * #define EX_ISDOOR (A) #define EX_CLOSED (B) #define EX_LOCKED (C)
			 * #define EX_PICKPROOF (F) #define EX_NOPASS (G) #define EX_EASY
			 * (H) #define EX_HARD (I) #define EX_INFURIATING (J) #define
			 * EX_NOCLOSE (K) #define EX_NOLOCK (L)
			 */
			exitFlagTable.put(A, Exit.DOOR);
			exitFlagTable.put(B, Exit.CLOSED);
			exitFlagTable.put(C, Exit.LOCKED);
			exitFlagTable.put(F, Exit.SECURE);
			exitFlagTable.put(G, Exit.SECURE);
		}
		int info = 0;
		for (Map.Entry<Long, Integer> entry : exitFlagTable.entrySet()) {
			if ((value & entry.getKey()) != 0)
				info |= entry.getValue();
		}
		return info;
	}

	private static void createResImmVulnFlags() {
		resImmVulnFlags = new HashMap<Long, DamType>();

		resImmVulnFlags.put(C, DamType.MAGIC);
		resImmVulnFlags.put(E, DamType.BASH);
		resImmVulnFlags.put(F, DamType.PIERCE);
		resImmVulnFlags.put(G, DamType.SLASH);
		resImmVulnFlags.put(H, DamType.FIRE);
		resImmVulnFlags.put(I, DamType.COLD);
		resImmVulnFlags.put(J, DamType.ELECTRICITY);
		resImmVulnFlags.put(K, DamType.ACID);
		resImmVulnFlags.put(L, DamType.POISON);
		resImmVulnFlags.put(M, DamType.NEGATIVE);
		resImmVulnFlags.put(N, DamType.HOLY);
		resImmVulnFlags.put(O, DamType.ENERGY);
		resImmVulnFlags.put(P, DamType.MENTAL);
		resImmVulnFlags.put(Q, DamType.DISEASE);
		resImmVulnFlags.put(S, DamType.LIGHT);
		resImmVulnFlags.put(T, DamType.SOUND);
	}

	public static void convertImmuneFlags(NonPlayer npc, long flags) {
		if (resImmVulnFlags == null) {
			createResImmVulnFlags();
		}

		for (Map.Entry<Long, DamType> entry : resImmVulnFlags.entrySet()) {
			if ((flags & entry.getKey()) != 0) {
				npc.adjustResistance(entry.getValue(), 200);
			}
		}
	}

	static Map<Long, Long> npcFlags;

	public static long convertNpcFlags(long value) {
		if (npcFlags == null) {
			npcFlags = new HashMap<Long, Long>();
			npcFlags.put(B, NonPlayer.SENTINEL);
			npcFlags.put(C, NonPlayer.SCAVENGER);
			npcFlags.put(F, NonPlayer.AGGRESSIVE);
			npcFlags.put(G, NonPlayer.STAY_AREA);
			npcFlags.put(H, NonPlayer.WIMPY);
		}
		long rval = 0;

		for (Map.Entry<Long, Long> entry : npcFlags.entrySet()) {
			if ((value & entry.getKey()) != 0) {
				rval |= entry.getValue();
			}
		}
		return rval;
	}

	static Map<String, Class<? extends Object>> itemTypes;

	public static Object createObjectType(String name) {
		if (itemTypes == null) {

			itemTypes = new HashMap<String, Class<? extends Object>>();
			itemTypes.put("light", LightObject.class);
			itemTypes.put("scroll", ScrollObject.class);
			itemTypes.put("wand", WandObject.class);
			itemTypes.put("staff", StaffObject.class);
			itemTypes.put("weapon", WeaponObject.class);
			itemTypes.put("armor", ArmorObject.class);
			itemTypes.put("potion", PotionObject.class);
			itemTypes.put("furniture", FurnitureObject.class);
			itemTypes.put("trash", TrashObject.class);
			itemTypes.put("container", ContainerObject.class);
			itemTypes.put("drink", LiquidContainerObject.class);
			itemTypes.put("key", KeyObject.class);
			itemTypes.put("food", FoodObject.class);
			itemTypes.put("money", MoneyObject.class);
			itemTypes.put("boat", RaftObject.class);
			itemTypes.put("fountain", LiquidContainerObject.class);
			itemTypes.put("pill", PillObject.class);
			itemTypes.put("map", MapObject.class);
			itemTypes.put("portal", PortalObject.class);
			itemTypes.put("warp_stone", WarpStoneObject.class);
			itemTypes.put("gem", TreasureObject.class);
			itemTypes.put("jewelry", TreasureObject.class);
			itemTypes.put("treasure", TreasureObject.class);
		}

		for (Map.Entry<String, Class<? extends Object>> entry : itemTypes
				.entrySet()) {
			if (Jmud.isName(entry.getKey(), name)) {
				try {
					Constructor<? extends Object> c = entry.getValue()
							.getConstructor();

					return c.newInstance();
				} catch (Exception e) {

					break;
				}
			}
		}
		System.out.println("could not create object of type " + name);
		return new TrashObject();
	}

	public static Position convertPosition(int value) {
		for (Position pos : Position.values()) {
			if (pos.ordinal() == value) {
				return pos;
			}
		}
		return Position.STANDING;
	}

	public static Position convertPosition(String name) {
		for (Position pos : Position.values()) {
			if (Jmud.isPrefix(pos.name(), name))
				return pos;
		}
		return Position.STANDING;
	}

	public static Race convertRaceType(String name) {
		Race race = Race.lookup(name);

		if (race != null)
			return race;

		if (Jmud.isPrefix(name, "fido"))
			return Race.lookup("dog");

		if (Jmud.isPrefix(name, "cat"))
			return Race.lookup("feline");

		if (Jmud.isPrefix(name, "lizard"))
			return Race.lookup("reptile");

		return Race.lookup("unique");
	}

	public static void convertResistanceFlags(NonPlayer npc, long flags) {
		if (resImmVulnFlags == null) {
			createResImmVulnFlags();
		}

		for (Map.Entry<Long, DamType> entry : resImmVulnFlags.entrySet()) {
			if ((flags & entry.getKey()) != 0) {
				npc.adjustResistance(entry.getValue(), 100);
			}
		}
	}

	static Map<Long, Long> roomFlags;

	public static long convertRoomFlags(long value) {
		if (roomFlags == null) {
			roomFlags = new HashMap<Long, Long>();

			roomFlags.put(K, Room.SAFE);

		}

		long flag = 0;

		for (Map.Entry<Long, Long> entry : roomFlags.entrySet()) {
			if ((value & entry.getKey()) != 0) {
				flag |= entry.getValue();
			}
		}
		return flag;
	}

	public static Sector convertSector(int sector) {
		switch (sector) {
		case 0:
			return Sector.INSIDE;
		case 1:
			return Sector.CITY;
		case 2:
			return Sector.FIELD;
		case 3:
			return Sector.FOREST;
		case 4:
			return Sector.HILLS;
		case 5:
			return Sector.MOUNTAIN;
		case 6:
		case 7:
			return Sector.WATER;
		case 9:
			return Sector.AIR;
		case 10:
			return Sector.DESERT;
		default:
			System.out.println("Unknown sector type");
			return Sector.INSIDE;
		}
	}

	public static Sex convertSex(int value) {
		switch (value) {
		case 1:
			return Sex.Male;
		case 2:
			return Sex.Female;
		default:
			return Sex.Sexless;
		}
	}

	public static Sex convertSex(String value) {
		for (Sex sex : Sex.values()) {
			if (Jmud.isPrefix(sex.name(), value))
				return sex;
		}
		return Sex.Sexless;
	}

	public static long convertAffectBits(long vector) {
		if (spellAffects == null) {
			createSpellAffects();
		}

		long value = 0;
		for (Map.Entry<Long, Long> entry : spellAffects.entrySet()) {
			if ((vector & entry.getKey()) != 0) {
				value |= entry.getValue();
			}
		}
		return value;
	}

	public static float convertSize(String name) {
		for (Size s : Size.values()) {
			if (Jmud.isPrefix(s.name(), name)) {
				return s.getValue();
			}
		}
		return Size.Medium.getValue();
	}

	public static void convertVulnerabilityFlags(NonPlayer npc, long flags) {
		if (resImmVulnFlags == null) {
			createResImmVulnFlags();
		}

		for (Map.Entry<Long, DamType> entry : resImmVulnFlags.entrySet()) {
			if ((flags & entry.getKey()) != 0) {
				npc.adjustResistance(entry.getValue(), -100);
			}
		}
	}

	public static ApplyType convertApplyType(int location) {
		switch (location) {
		case 1:
			return ApplyType.Str;
		case 2:
			return ApplyType.Int;
		case 3:
			return ApplyType.Dex;
		case 4:
			return ApplyType.Wis;
		case 5:
			return ApplyType.Con;
		case 6:
			return ApplyType.Sex;
		case 8:
			return ApplyType.Level;
		case 9:
			return ApplyType.Age;
		case 10:
			return ApplyType.Height;
		case 11:
			return ApplyType.Weight;
		case 12:
			return ApplyType.Mana;
		case 13:
			return ApplyType.Hit;
		case 14:
			return ApplyType.Move;
		case 17:
			return ApplyType.Resistance;

		case 18:
			return ApplyType.Hitroll;
		case 19:
			return ApplyType.Damroll;
			// case 20:
			// return ApplyType.Resistance;
		default:
			return ApplyType.None;
		}
	}

	static Map<Long, Long> spellAffects;

	private static void createSpellAffects() {
		spellAffects = new HashMap<Long, Long>();

		spellAffects.put(A, Affect.Blindness);
		spellAffects.put(B, Affect.Invisibility);
		spellAffects.put(C, Affect.DetectEvil);
		spellAffects.put(D, Affect.DetectInvis);
		spellAffects.put(E, Affect.DetectMagic);
		spellAffects.put(F, Affect.DetectHidden);
		spellAffects.put(G, Affect.DetectGood);
		spellAffects.put(H, Affect.Sanctuary);
		spellAffects.put(I, Affect.FaerieFire);
		spellAffects.put(K, Affect.Curse);
		spellAffects.put(M, Affect.Poison);
		spellAffects.put(N, Affect.ProtectEvil);
		spellAffects.put(O, Affect.ProtectGood);
		spellAffects.put(P, Affect.Sneak);
		spellAffects.put(Q, Affect.Hide);
		spellAffects.put(R, Affect.Sleep);
		spellAffects.put(S, Affect.Charm);
		spellAffects.put(T, Affect.Flying);
		spellAffects.put(U, Affect.PassDoor);
		spellAffects.put(V, Affect.Haste);
		spellAffects.put(X, Affect.Plague);
		spellAffects.put(W, Affect.Calm);
		spellAffects.put(Y, Affect.Weaken);
		spellAffects.put(aa, Affect.Berserk);
		spellAffects.put(cc, Affect.Regeneration);
		spellAffects.put(dd, Affect.Slow);

	}

	static Map<Long, Long> wearFlags;

	public static long convertWearFlags(long readFlag) {

		if (wearFlags == null) {
			wearFlags = new HashMap<Long, Long>();

			wearFlags.put(A, WearFlags.TAKE);
			wearFlags.put(B, WearFlags.FINGER);
			wearFlags.put(C, WearFlags.NECK);
			wearFlags.put(E, WearFlags.HEAD);
			wearFlags.put(F, WearFlags.LEGS);
			wearFlags.put(G, WearFlags.FEET);
			wearFlags.put(H, WearFlags.HANDS);
			wearFlags.put(I, WearFlags.ARMS);
			wearFlags.put(L, WearFlags.WAIST);
			wearFlags.put(M, WearFlags.WRIST);
			wearFlags.put(N, WearFlags.WIELD);
			wearFlags.put(P, WearFlags.NO_SAC);
			wearFlags.put(Q, WearFlags.FLOAT);
		}
		long rval = 0;
		for (Map.Entry<Long, Long> entry : wearFlags.entrySet()) {
			if ((readFlag & entry.getKey()) != 0) {
				rval |= entry.getValue();
			}
		}
		return rval;
	}

	static Map<Long, Integer> weaponFlags;

	public static int convertWeaponFlags(long readFlag) {
		if (weaponFlags == null) {
			weaponFlags = new HashMap<Long, Integer>();

			weaponFlags.put(A, WeaponObject.FLAMING);
			weaponFlags.put(B, WeaponObject.FROST);
			weaponFlags.put(C, WeaponObject.VAMPIRIC);
			weaponFlags.put(D, WeaponObject.SHARP);
			weaponFlags.put(E, WeaponObject.VORPAL);
			weaponFlags.put(F, WeaponObject.TWO_HANDED);
			weaponFlags.put(G, WeaponObject.SHOCKING);
			weaponFlags.put(H, WeaponObject.POISON);
		}

		int value = 0;

		for (Map.Entry<Long, Integer> entry : weaponFlags.entrySet()) {
			if ((entry.getKey() & readFlag) != 0)
				value |= entry.getValue();
		}
		return value;
	}

	static Map<Integer, Integer> containerFlags;

	public static int convertContainerFlags(long readFlag) {

		if (containerFlags == null) {
			containerFlags = new HashMap<Integer, Integer>();

			containerFlags.put(1, ContainerObject.CLOSEABLE);
			containerFlags.put(2, ContainerObject.PICKPROOF);
			containerFlags.put(4, ContainerObject.CLOSED);
			containerFlags.put(8, ContainerObject.LOCKED);
			containerFlags.put(16, ContainerObject.PUT_ON);
		}

		int value = 0;

		for (Map.Entry<Integer, Integer> entry : containerFlags.entrySet()) {
			if ((entry.getKey() & readFlag) != 0)
				value |= entry.getValue();
		}
		return value;
	}

	public static Spell convertSpellName(String readWord) {
		return Spell.lookup(readWord);
	}

	static Map<Integer, Class<? extends Object>> itemTypeNumbers;

	public static Object createObjectType(int readNumber) {
		if (itemTypeNumbers == null) {
			itemTypeNumbers = new HashMap<Integer, Class<? extends Object>>();
			itemTypeNumbers.put(1, LightObject.class);
			itemTypeNumbers.put(2, ScrollObject.class);
			itemTypeNumbers.put(3, WandObject.class);
			itemTypeNumbers.put(4, StaffObject.class);
			itemTypeNumbers.put(5, WeaponObject.class);
			itemTypeNumbers.put(8, TreasureObject.class);
			itemTypeNumbers.put(9, ArmorObject.class);
			itemTypeNumbers.put(10, PotionObject.class);
			itemTypeNumbers.put(12, FurnitureObject.class);
			itemTypeNumbers.put(13, TrashObject.class);
			itemTypeNumbers.put(15, ContainerObject.class);
			itemTypeNumbers.put(17, LiquidContainerObject.class);
			itemTypeNumbers.put(18, KeyObject.class);
			itemTypeNumbers.put(19, FoodObject.class);
			itemTypeNumbers.put(20, MoneyObject.class);
			itemTypeNumbers.put(22, RaftObject.class);
			itemTypeNumbers.put(25, LiquidContainerObject.class);
			itemTypeNumbers.put(26, PillObject.class);
			itemTypeNumbers.put(28, MapObject.class);
			itemTypeNumbers.put(29, PortalObject.class);
			itemTypeNumbers.put(30, WarpStoneObject.class);
			itemTypeNumbers.put(31, KeyObject.class);
			itemTypeNumbers.put(32, TreasureObject.class);
			itemTypeNumbers.put(33, TreasureObject.class);
		}

		for (Map.Entry<Integer, Class<? extends Object>> entry : itemTypeNumbers
				.entrySet()) {
			if (entry.getKey() == readNumber) {
				try {
					Constructor<? extends Object> c = entry.getValue()
							.getConstructor();

					return c.newInstance();
				} catch (Exception e) {
					break;
				}
			}
		}
		System.out.println("could not create object of type " + readNumber);
		return new TrashObject();
	}

	static Map<Long, Integer> furnitureFlags;

	/**
	 * @param readFlag
	 * @return
	 */
	public static int covertFurnitureFlag(long readFlag) {

		if (furnitureFlags == null) {
			furnitureFlags = new HashMap<Long, Integer>();

			furnitureFlags.put(A, FurnitureObject.STAND_AT);
			furnitureFlags.put(B, FurnitureObject.STAND_ON);
			furnitureFlags.put(C, FurnitureObject.STAND_IN);
			furnitureFlags.put(D, FurnitureObject.SIT_AT);
			furnitureFlags.put(E, FurnitureObject.SIT_ON);
			furnitureFlags.put(F, FurnitureObject.SIT_IN);
			furnitureFlags.put(G, FurnitureObject.REST_AT);
			furnitureFlags.put(H, FurnitureObject.REST_ON);
			furnitureFlags.put(I, FurnitureObject.REST_IN);
			furnitureFlags.put(J, FurnitureObject.SLEEP_AT);
			furnitureFlags.put(K, FurnitureObject.SLEEP_ON);
			furnitureFlags.put(L, FurnitureObject.SLEEP_IN);
			furnitureFlags.put(M, FurnitureObject.PUT_AT);
			furnitureFlags.put(N, FurnitureObject.PUT_ON);
			furnitureFlags.put(O, FurnitureObject.PUT_IN);
			furnitureFlags.put(P, FurnitureObject.PUT_INSIDE);
		}

		int value = 0;

		for (Map.Entry<Long, Integer> entry : furnitureFlags.entrySet()) {
			if ((entry.getKey() & readFlag) != 0)
				value |= entry.getValue();
		}
		return value;
	}

	static Map<Long, Integer> gateFlags;

	/**
	 * @param readFlag
	 * @return
	 */
	public static int convertGateFlags(int readFlag) {
		if (gateFlags == null) {
			gateFlags = new HashMap<Long, Integer>();

			gateFlags.put(A, PortalObject.NORMAL);
			gateFlags.put(B, PortalObject.NOCURSE);
			gateFlags.put(C, PortalObject.GOWITH);
			gateFlags.put(D, PortalObject.BUGGY);
			gateFlags.put(E, PortalObject.RANDOM);
		}

		int value = 0;
		for (Map.Entry<Long, Integer> entry : gateFlags.entrySet()) {
			if ((entry.getKey() & readFlag) != 0)
				value |= entry.getValue();
		}
		return value;
	}

	/**
	 * @param readWord
	 * @return
	 * @throws ImportException
	 */
	public static WeaponClass convertWeaponClass(String readWord)
			throws ImportException {
		for (WeaponClass wc : WeaponClass.values())
			if (Jmud.isPrefix(wc.name(), readWord))
				return wc;

		throw new ImportException("Weapon Class not found " + readWord);
	}

	public static Attack convertAttackType(String word) throws ImportException {
		for (Map.Entry<String, DamType> entry : DamType.getDefaultAttackTypes()) {
			if (Jmud.isPrefix(entry.getKey(), word))
				return new Attack(entry.getValue(), entry.getKey());
		}
		System.out.println("invalid attack type " + word);
		return new Attack(DamType.BASH, "hit");
	}
}
