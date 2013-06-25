package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("pill")
public class PillObject extends ScrollPotionPillObject {

	private static final long serialVersionUID = 1L;
}
