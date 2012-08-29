/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.arg3.jmud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author ryan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubClasses {
	Class<?>[] types();
}
