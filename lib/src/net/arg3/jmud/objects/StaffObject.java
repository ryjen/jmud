package net.arg3.jmud.objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.arg3.jmud.WandStaffObject;

@Entity
@DiscriminatorValue("staff")
public class StaffObject extends WandStaffObject {

	private static final long serialVersionUID = 1L;

}
