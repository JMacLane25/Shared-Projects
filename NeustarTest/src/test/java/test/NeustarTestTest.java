package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NeustarTestTest {
	NeustarTest neustarTest = null;
	
	private final ByteArrayOutputStream systemOutStream = new ByteArrayOutputStream();
	private final ByteArrayOutputStream systemErrStream = new ByteArrayOutputStream();

	@Before
	public void setUp() {
		neustarTest = new NeustarTest();
	    System.setOut(new PrintStream(systemOutStream));
	    System.setErr(new PrintStream(systemErrStream));
	}

	@After
	public void cleanUp() {
		systemOutStream.reset();
		systemErrStream.reset();
	    System.setOut(null);
	    System.setErr(null);
	}
	
	@Test
	public void testLoadGoodShortFileThenDisplayProperResponse() {
		String[] args = {"goodShortTestFile.txt"};
		String properOutputStream = "CATEGORY     COUNT\r\nPERSON     2\r\nPLACE     1\r\n\r\nPERSON Bob Jones\nPLACE Washington\nPERSON Mary\n";

		neustarTest.nMain(args);
		
		assertEquals(systemOutStream.toString(), properOutputStream);
	}

	@Test
	public void testLoadGoodFileThenDisplayProperResponse() {
		String[] args = {"goodTestFile.txt"};
		String properOutputStream = "CATEGORY     COUNT\r\nPERSON     3\r\nPLACE     2\r\nCOMPUTER     1\r\nOTHER     1\r\nANIMAL     2\r\n\r\nPERSON Bob Jones\nPLACE Washington\nPERSON Mary\nCOMPUTER Mac\nPERSON Bob Jones\nOTHER Tree\nANIMAL Dog\nPLACE Texas\nANIMAL Cat\n";

		neustarTest.nMain(args);
		
		assertEquals(systemOutStream.toString(), properOutputStream);
	}
	
	@Test
	public void testLoadFileWithBadCategoryThenDisplayProperResponse() {
		String[] args = {"hasBadCategoryTestFile.txt"};
		String properOutputStream = "CATEGORY     COUNT\r\nPERSON     3\r\nPLACE     2\r\nCOMPUTER     1\r\nOTHER     1\r\nANIMAL     2\r\n\r\nPERSON Bob Jones\nPLACE Washington\nPERSON Mary\nCOMPUTER Mac\nPERSON Bob Jones\nOTHER Tree\nANIMAL Dog\nPLACE Texas\nANIMAL Cat\n";

		neustarTest.nMain(args);
		
		assertEquals(systemOutStream.toString(), properOutputStream);
	}
	
	@Test
	public void testLoadEmptyFileThenDisplayJustHeader() {
		String[] args = {"emptyTestFile.txt"};
		String properOutputStream = "CATEGORY     COUNT\r\n\r\n";

		neustarTest.nMain(args);
		
		assertEquals(systemOutStream.toString(), properOutputStream);
	}

	@Test
	public void testLoadFileWithAMissingSubcategoryThenDisplayProperResponse() {
		String[] args = {"missingSubCategoryTestFile.txt"};
		String properOutputStream = "CATEGORY     COUNT\r\nPERSON     3\r\nPLACE     2\r\nCOMPUTER     1\r\nOTHER     1\r\nANIMAL     2\r\n\r\nPERSON Bob Jones\nPLACE Washington\nPERSON Mary\nCOMPUTER Mac\nPERSON Bob Jones\nOTHER Tree\nANIMAL Dog\nPLACE Texas\nANIMAL Cat\n";

		neustarTest.nMain(args);
		
		assertEquals(systemOutStream.toString(), properOutputStream);
	}
	
}
