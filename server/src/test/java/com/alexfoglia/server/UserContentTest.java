package com.alexfoglia.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UserContentTest {

	private UserContent fixture;

	@Before
	public void setUp() {
		fixture=new UserContent();
	}

	@Test
	public void testParseMatrixWhenEmpty() {
		assertArrayEquals(new int[0][0],fixture.parseMatrix());
		assertEquals(0,fixture.getNumber());
	}

	@Test
	public void testParseMatrixWhenSingle() {
		fixture.setContent("1");
		fixture.setNumber(1);
		assertArrayEquals(new int[][] {{1}},fixture.parseMatrix());
	}

	@Test
	public void testParseMatrix() {
		fixture.setNumber(3);
		fixture.setContent("111010001");
		assertArrayEquals(new int[][] {{1,1,1},{0,1,0},{0,0,1}},fixture.parseMatrix());
	}

	@Test
	public void testParseMatrixWhenTooMuchContentWRTnumber() {
		fixture.setNumber(3);
		fixture.setContent("11101000101011101010");
		assertArrayEquals(new int[][] {{1,1,1},{0,1,0},{0,0,1}},fixture.parseMatrix());
	}

	@Test
	public void testParseMatrixWhenLessContentWRTnumber() {
		fixture.setNumber(3);
		fixture.setContent("111");
		assertArrayEquals(new int[][] {{1,1,1},{0,0,0},{0,0,0}},fixture.parseMatrix());
	}

	@Test
	public void testParseMatrixWhenWrongContent() {
		fixture.setNumber(3);
		fixture.setContent("pippopluto");
		assertArrayEquals(new int[][] {{0,0,0},{0,0,0},{0,0,0}},fixture.parseMatrix());
	}

}
