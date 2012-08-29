/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud.enums;

/**
 * 
 * @author ryan
 */
public enum Sex {

	Female, Male, Hermaphrodite, Sexless;

	public String getHeShe() {
		switch (this) {
		case Male:
			return "he";
		case Female:
			return "she";
		default:
			return "it";
		}
	}

	public String getHimHer() {
		switch (this) {
		case Male:
			return "him";
		case Female:
			return "her";
		default:
			return "it";
		}
	}

	public String getHisHers() {
		switch (this) {
		case Male:
			return "his";
		case Female:
			return "hers";
		default:
			return "its";
		}
	}
}
