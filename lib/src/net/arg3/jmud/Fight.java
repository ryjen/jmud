package net.arg3.jmud;

import net.arg3.jmud.enums.ApplyType;
import net.arg3.jmud.enums.DamType;
import net.arg3.jmud.enums.WearLocation;
import net.arg3.jmud.interfaces.ITickable;
import net.arg3.jmud.objects.WeaponObject;

public class Fight implements ITickable {

	Character ch;
	Character victim;
	boolean over;

	public Fight(Character ch, Character victim) {
		this.ch = ch;
		this.victim = victim;
		ch.setFighting(victim);
		victim.setFighting(ch);
	}

	private void showDamage(Character ch, Character victim, Attack attack,
			int dam) {
		String punct;
		String vp;

		if (dam <= 0) {
			vp = "misses";
		} else if (dam <= 4) {
			vp = "scratches";
		} else if (dam <= 8) {
			vp = "grazes";
		} else if (dam <= 12) {
			vp = "hits";
		} else if (dam <= 16) {
			vp = "injures";
		} else if (dam <= 20) {
			vp = "wounds";
		} else if (dam <= 24) {
			vp = "mauls";
		} else if (dam <= 28) {
			vp = "decimates";
		} else if (dam <= 32) {
			vp = "devastates";
		} else if (dam <= 36) {
			vp = "maims";
		} else if (dam <= 40) {
			vp = "MUTILATES";
		} else if (dam <= 44) {
			vp = "DISEMBOWELS";
		} else if (dam <= 48) {
			vp = "DISMEMBERS";
		} else if (dam <= 52) {
			vp = "MASSACRES";
		} else if (dam <= 56) {
			vp = "MANGLES";
		} else if (dam <= 60) {
			vp = "*** DEMOLISHES ***";
		} else if (dam <= 75) {
			vp = "*** DEVASTATES ***";
		} else if (dam <= 100) {
			vp = "=== OBLITERATES ===";
		} else if (dam <= 125) {
			vp = ">>> ANNIHILATES <<<";
		} else if (dam <= 150) {
			vp = "<<< ERADICATES >>>";
		} else {
			vp = "does UNSPEAKABLE things to";
		}

		punct = dam <= 24 ? "." : "!";

		if (ch == victim) {
			ch.getRoom()
					.format("{0}'s {1} {2} {0:M}{3}", ch, attack, vp, punct);
			ch.format("Your {0} {1} you{2}", attack, vp, punct);
		} else {
			victim.getRoom().format("{0}'s {1} {2} {3}{4}", ch, attack, vp,
					victim, punct);
			ch.format("Your {0} {1} {2}{3}", attack, vp, victim, punct);
			victim.format("{0}'s {1} {2} you{3}", ch, attack, vp, punct);
		}
	}

	public boolean damage(Character ch, Character victim, Attack attack,
			int dam, boolean show) {

		if (over)
			return false;

		if (dam > 35)
			dam = (dam - 35) / 2 + 35;
		if (dam > 80)
			dam = (dam - 80) / 2 + 80;

		// TODO: modify dam on abilities

		if (show) {
			showDamage(ch, victim, attack, dam);
		}

		if (dam == 0)
			return false;

		victim.getVitals().adjustHit(-dam);

		if (victim.getVitals().getHit() <= 0) {
			over = true;
			victim.getVitals().setHit(0);
			ch.setFighting(null);
			victim.setFighting(null);
			victim.writeln("~RYou have been KILLED!~x");
			victim.getRoom().format("~R{0} has killed {1}!~x", ch, victim);
			ch.format("~GYou have killed {0}!~x", victim);
		}
		return true;
	}

	@Override
	public int getPeriod() {
		return 700;
	}

	public int interpolate(int level, int value_00, int value_32) {
		return value_00 + level * (value_32 - value_00) / 32;
	}

	public void multiHit(Character ch, Character victim) {

		oneHit(ch, victim, null);
	}

	public void oneHit(Character ch, Character victim, Attack attack) {
		int thac = interpolate(ch.getLevel(), ch.getThac0(), ch.getThac32());
		Object weapon = null;

		if (thac < 0)
			thac /= 2;

		if (thac < -5)
			thac = -5 + (thac + 5) / 2;

		if (attack == null) {
			weapon = ch.getEq(WearLocation.WIELD);
			if (weapon != null && weapon instanceof WeaponObject) {
				attack = ((WeaponObject) weapon).getAttack();
			} else {
				attack = ch.getAttack();
			}
			attack.setIsHit(true);
		}

		int ac = victim.getResistance(attack.getDamType());

		if (ac < -15)
			ac = (ac + 15) / 5 - 15;

		int roll = Jmud.Rand.nextInt(20);

		if (roll == 0 || (roll != 19 && roll < thac - ac)) {
			damage(ch, victim, attack, 0, true);
			return;
		}

		int dam = Jmud.range(ch.getLevel() / 2, ch.getLevel());

		boolean result = damage(ch, victim, attack, dam, true);

		if (result && weapon != null) {
			WeaponObject w = (WeaponObject) weapon;

			if (!over && w.hasWeaponFlag(WeaponObject.POISON)) {
				int level;

				Affect poison = w.findAffect(Affect.Poison);

				if (poison != null)
					level = poison.getLevel();
				else
					level = w.getLevel();

				if (!victim.savesSpell(level / 2, DamType.POISON)) {
					victim.writeln("You feel poison coursing through your veins.");
					victim.getRoom().format(
							"{0} is poisoned by the venom on {1}.", victim, w);

					Affect paf = poison != null ? poison : new Affect();

					paf.setLevel(level * 3 / 4);
					paf.setDuration(level / 2);
					paf.setModifier(-1);
					paf.setLocation(ApplyType.Str);

					victim.addAffect(paf);
				}

				/* weaken the poison if it's temporary */
				if (poison != null) {
					poison.setLevel(Math.max(0, poison.getLevel() - 2));
					poison.setDuration(Math.max(0, poison.getDuration() - 1));

					if (poison.getLevel() == 0 || poison.getDuration() == 0)
						ch.format("The poison on {0} has worn off.", w);
				}
			}

			if (!over && w.hasWeaponFlag(WeaponObject.VAMPIRIC)) {
				dam = Jmud.range(1, w.getLevel() / 5 + 1);
				victim.getRoom().format("{1} draws life from {0}.", victim, w);
				victim.format("You feel {0} drawing your life away.", w);
				damage(ch, victim, new Attack(DamType.NEGATIVE, "life drain"),
						dam, false);
				ch.setAlignment(Math.max(-1000, ch.getAlignment() - 1));
				ch.getVitals().adjustHit(dam / 2);
			}

			if (!over && w.hasWeaponFlag(WeaponObject.FLAMING)) {
				dam = Jmud.range(1, w.getLevel() / 4 + 1);
				victim.getRoom().format("~R{0} is burned by {1}~x.", victim, w);
				victim.format("~R{0} ~Rsears your flesh~x.", w);
				// fire_effect( (void *) victim,wield->level/2,dam,TARGET_CHAR);
				damage(ch, victim, new Attack(DamType.FIRE, "flames"), dam,
						false);
			}

			if (!over && w.hasWeaponFlag(WeaponObject.FROST)) {
				dam = Jmud.range(1, w.getLevel() / 6 + 2);
				victim.getRoom().format("~C{0} freezes {1}.~x", w, victim);
				victim.format(
						"~CThe cold touch of {0} surrounds you with ice.~x", w);
				// cold_effect(victim,w.getLevel()/2,dam,TARGET_CHAR);
				damage(ch, victim, new Attack(DamType.COLD, "ice"), dam, false);
			}

			if (!over && w.hasWeaponFlag(WeaponObject.SHOCKING)) {
				dam = Jmud.range(1, w.getLevel() / 5 + 2);
				victim.getRoom().format("{0} is struck by lightning from {1}.",
						victim, w);
				victim.format("You are shocked by {0}.", w);
				// shock_effect(victim,wield->level/2,dam,TARGET_CHAR);
				damage(ch, victim,
						new Attack(DamType.ELECTRICITY, "lightning"), dam,
						false);
			}
		}
	}

	@Override
	public boolean tick() {

		multiHit(ch, victim);

		if (over)
			return true;

		multiHit(victim, ch);

		return over;
	}
}
