package org.cagrid.gaards.dorian.idp;

import junit.framework.TestCase;


public class TestDictionaryCheck extends TestCase {

	public void testGoodPasswords() {
		try {
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("d0ct0r"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("ir0ck"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("23fly24"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("23goo24"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("23asaiuow2n341q2n"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("987-asfakshafs089aflk"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("124l3k5ha;lskdflkq24512345"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("987asdf98akjhaf0780"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord(",anmdsf-09asdklaf-0"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("af9s87akshf90akh"));
			assertFalse(DictionaryCheck.doesStringContainDictionaryWord("$DorianAdmin$"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Error!!!");
		}
	}


	public void testBadPasswords() {
		try {
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("test"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("23test"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("test23test"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("23test"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("tset"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("tset4"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("23asaiuboatow2n341q2n"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("987-asfakshaswimfs089aflk"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("124ltest3k5ha;lskdflkq24512345"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("987asdf98akjhaf0trial780"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord(",anmdsf-09asdburdenklaf-0"));
			assertTrue(DictionaryCheck.doesStringContainDictionaryWord("af9strong87akshf90akh"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Error!!!");
		}
	}


	public void testSubStrings() {
		java.util.List subs = DictionaryCheck.buildSubStrings("testing", 1);
		assertTrue(subs.size() == 7 + 6 + 5 + 4 + 3 + 2 + 1);
		assertEquals(subs.get(3), "t");
		assertEquals(subs.get(7), "te");
		subs = DictionaryCheck.buildSubStrings("testing", 7);
		assertTrue(subs.size() == 1);
		assertEquals(subs.get(0), "testing");
		subs = DictionaryCheck.buildSubStrings("testing", 4);
		assertTrue(subs.size() == 4 + 3 + 2 + 1);
		assertEquals(subs.get(8), "esting");
		assertEquals(subs.get(9), "testing");
	}

}
