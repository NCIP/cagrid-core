package org.cagrid.gaards.dorian.idp;

import junit.framework.TestCase;


public class TestPasswordUtils extends TestCase {

	public void testHasCapitalLetter() {
		assertTrue(PasswordUtils.hasCapitalLetter("Dorianadmin"));
		assertTrue(PasswordUtils.hasCapitalLetter("dorianadmiN"));
		assertTrue(PasswordUtils.hasCapitalLetter("dorianAdmin$1"));
		assertTrue(PasswordUtils.hasCapitalLetter("DorianAdmin$1"));
		assertFalse(PasswordUtils.hasCapitalLetter(""));
		assertFalse(PasswordUtils.hasCapitalLetter(" "));
		assertFalse(PasswordUtils.hasCapitalLetter("	"));
	}


	public void testHasLowerCaseLetter() {
		assertTrue(PasswordUtils.hasLowerCaseLetter("dORIANADMIN"));
		assertTrue(PasswordUtils.hasLowerCaseLetter("DORIANADMIn"));
		assertTrue(PasswordUtils.hasLowerCaseLetter("DORIANaDMIN"));
		assertTrue(PasswordUtils.hasLowerCaseLetter("DorianAdmin$1"));
		assertFalse(PasswordUtils.hasLowerCaseLetter("DORIANADMIN$1"));
		assertFalse(PasswordUtils.hasLowerCaseLetter(""));
		assertFalse(PasswordUtils.hasLowerCaseLetter(" "));
		assertFalse(PasswordUtils.hasLowerCaseLetter("	"));
	}


	public void testSymbol() {

		for (int i = 0; i < PasswordUtils.SYMBOLS.length; i++) {
			assertTrue(PasswordUtils.hasSymbol(PasswordUtils.SYMBOLS[i] + "DorianAdmin"));
			assertTrue(PasswordUtils.hasSymbol("DorianAdmin" + PasswordUtils.SYMBOLS[i]));
			assertTrue(PasswordUtils.hasSymbol("Dorian" + PasswordUtils.SYMBOLS[i] + "Admin1"));
			assertTrue(PasswordUtils.hasSymbol("DorianAdmin" + PasswordUtils.SYMBOLS[i] + "1"));
			assertTrue(PasswordUtils.hasSymbol("Dorian" + PasswordUtils.SYMBOLS[i] + "Admin$1"));
			assertFalse(PasswordUtils.hasSymbol("DorianAdmin"));
			assertFalse(PasswordUtils.hasSymbol(""));
			assertFalse(PasswordUtils.hasSymbol(" "));
			assertFalse(PasswordUtils.hasSymbol("	"));
		}
	}


	public void testHasNumber() {
		assertTrue(PasswordUtils.hasNumber("DorianAdmin$1"));
		assertTrue(PasswordUtils.hasNumber("9DorianAdmin"));
		assertTrue(PasswordUtils.hasNumber("Dorian9Admin"));
		assertFalse(PasswordUtils.hasNumber("DorianAdmin$"));
		assertFalse(PasswordUtils.hasNumber(""));
		assertFalse(PasswordUtils.hasNumber(" "));
		assertFalse(PasswordUtils.hasNumber("	"));
	}

}
