package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("wand")
public class WandObject extends WandStaffObject {

	private static final long serialVersionUID = 1L;

}
