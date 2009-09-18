package gov.nih.nci.cagrid.introduce.test.unit;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.codegen.serializers.SyncSerialization;

import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

public class SyncSerializationTestCase extends TestCase {
	private File emptyFile = null;
	private File noMarkupFile = null;
	private File onlyHeaderFile = null;
	private File fullMarkupFile = null;

	private static final String IGNORED = "THIS SHOULD REMAIN AS IS";
	private static final String EXISTING = "REPLACE ME";
	private static final String REPLACEMENT = "HERE IS THE NEW STUFF";

	private static final String NO_MARKUP = IGNORED
			+ SyncSerialization.WSDD_END_TAG;

	private static final String ONLY_HEADER_MARKUP = IGNORED
			+ SyncSerialization.MAPPING_HEADER + SyncSerialization.WSDD_END_TAG;

	private static final String ALL_MARKUP = IGNORED
			+ SyncSerialization.MAPPING_HEADER + "\n" + EXISTING + "\n"
			+ SyncSerialization.MAPPING_FOOTER + SyncSerialization.WSDD_END_TAG;

	private static final String GOLD = IGNORED
			+ SyncSerialization.MAPPING_HEADER + "\n" + REPLACEMENT + "\n"
			+ SyncSerialization.MAPPING_FOOTER + SyncSerialization.WSDD_END_TAG
			+ "\n";

	private static final String NO_REPLACEMENT_GOLD = IGNORED
			+ SyncSerialization.WSDD_END_TAG + "\n";

	protected void setUp() throws Exception {
		super.setUp();
		emptyFile = File.createTempFile(this.getClass().getName(), ".wsdd");
		emptyFile.deleteOnExit();

		noMarkupFile = File.createTempFile(this.getClass().getName(), ".wsdd");
		noMarkupFile.deleteOnExit();
		FileWriter fw = new FileWriter(noMarkupFile);
		fw.write(NO_MARKUP);
		fw.close();

		onlyHeaderFile = File
				.createTempFile(this.getClass().getName(), ".wsdd");
		onlyHeaderFile.deleteOnExit();
		fw = new FileWriter(onlyHeaderFile);
		fw.write(ONLY_HEADER_MARKUP);
		fw.close();

		fullMarkupFile = File
				.createTempFile(this.getClass().getName(), ".wsdd");
		fullMarkupFile.deleteOnExit();
		fw = new FileWriter(fullMarkupFile);
		fw.write(ALL_MARKUP);
		fw.close();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		emptyFile.delete();
	}

	public void testEmptyFile() {
		try {
			SyncSerialization.editFile(emptyFile, "");
		} catch (SynchronizationException e) {
			// this should happen
		}
	}

	public void testNoFile() {
		try {
			SyncSerialization.editFile(null, "");
		} catch (SynchronizationException e) {
			// this should happen
		}
	}

	public void testReplacements() {
		assertReplacement(noMarkupFile);
		assertReplacement(onlyHeaderFile);
		assertReplacement(fullMarkupFile);
	}

	public void testEmptyReplacements() {
		assertNoReplacement(noMarkupFile);
		assertNoReplacement(onlyHeaderFile);
		assertNoReplacement(fullMarkupFile);
	}

	public void assertNoReplacement(File file) {
		try {
			SyncSerialization.editFile(file, "");
			String newContents = Utils.fileToStringBuffer(file).toString();
			assertEquals(NO_REPLACEMENT_GOLD, newContents);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void assertReplacement(File file) {
		try {
			SyncSerialization.editFile(file, REPLACEMENT);
			String newContents = Utils.fileToStringBuffer(file).toString();
			assertEquals(GOLD, newContents);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SyncSerializationTestCase.class);
	}

}
