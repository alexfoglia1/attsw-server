package com.alexfoglia.server;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GridServiceAndRepositoryIT {

	@Autowired
	private IMongoRepository repo;

	@Autowired
	private ConcreteGridService service;

	@After
	public void tearDown() {
		repo.deleteAll();
	}

	@Before
	public void setUp() {
		repo.deleteAll();
	}

	@Test
	public void testGetAllWhenDbEmpty() {
		assertEquals(0,service.findAllGridsInDb().size());
	}

	@Test
	public void testGetAllWhenDbContainsOne() {
		repo.save(new DatabaseGrid());
		List<DatabaseGrid> grids=service.findAllGridsInDb();
		assertEquals(1,grids.size());
		assertNotNull(grids.get(0).getId());
		assertEquals(0,grids.get(0).getN());
		assertArrayEquals(new int[0][0],grids.get(0).getMatrix());
	}

	@Test
	public void testGetAllIdWhenDbContainsMore() {
		repo.save(new DatabaseGrid());
		repo.save(new DatabaseGrid());
		List<DatabaseGrid> grids=service.findAllGridsInDb();
		assertEquals(2,grids.size());
		assertNotNull(grids.get(0).getId());
		assertEquals(0,grids.get(0).getN());
		assertArrayEquals(new int[0][0],grids.get(0).getMatrix());
		assertNotNull(grids.get(1).getId());
		assertEquals(0,grids.get(1).getN());
		assertArrayEquals(new int[0][0],grids.get(1).getMatrix());
	}

	@Test
	public void testGetByIdWhenExists() {
		DatabaseGrid expected=new DatabaseGrid();
		expected.setId("test_id");
		repo.save(expected);
		DatabaseGrid retrieved=service.findOneById("test_id");
		assertEquals("test_id",retrieved.getId());
		assertEquals(0,retrieved.getN());
		assertArrayEquals(new int[0][0],retrieved.getMatrix());
	}

	@Test
	public void testGetByIdWhenNotExists() {
		assertNull(service.findOneById(""));
	}

	@Test
	public void testGetShortestPath() {
		int[][]mat;
		mat=new int[][] {
			{1,1,1},
			{1,1,1},
			{1,1,1}
		};
		repo.save(new DatabaseGrid(3,mat));
		String gen_id=repo.findAll().get(0).getId();
		List<String> path=service.getShortestPath("0_0", "2_1", gen_id);
		assertEquals(Arrays.asList("0_0","1_0","2_0","2_1"),path);
	}

	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testGetShortestPathWhenOutOfBounds() {
		repo.save(new DatabaseGrid());
		String gen_id=repo.findAll().get(0).getId();
		service.getShortestPath("0_0", "0_0", gen_id);
	}

}