package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.ScrollPotionPillObject;

@Entity
@DiscriminatorValue("potion")
public class PotionObject extends ScrollPotionPillObject {
	private static final long serialVersionUID = 1L;

}
