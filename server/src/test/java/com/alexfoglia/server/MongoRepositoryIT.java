package com.alexfoglia.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoRepositoryIT {

	@Autowired 
	private IMongoRepository repo;

	@Autowired 
	private MongoOperations mongoOps;

	@Before
	public void setUp() {
		mongoOps.dropCollection(DatabaseGrid.class);
	}

	@Test
	public void testSave() {
		DatabaseGrid toSave=new DatabaseGrid(1,new int[][] {{1}});
		repo.save(toSave);
		List<DatabaseGrid> allGrids=mongoOps.findAll(DatabaseGrid.class);
		assertEquals(1,allGrids.size());
		assertEquals(1,allGrids.get(0).getN());
		assertArrayEquals(new int[][] {{1}},allGrids.get(0).getMatrix());
		assertNotNull(allGrids.get(0).getId());
	}

	@Test
	public void testFindOne() {
		DatabaseGrid tosave=new DatabaseGrid(1,new int[][] {{1}});
		tosave.setId("a_generated_id");
		mongoOps.save(tosave);
		DatabaseGrid found=repo.findOne("a_generated_id");
		assertEquals(1,found.getN());
		assertEquals("a_generated_id",found.getId());
		assertArrayEquals(new int[][] {{1}},found.getMatrix());
	}

	@Test
	public void testFindAll() {
		DatabaseGrid grid1=new DatabaseGrid();
		DatabaseGrid grid2=new DatabaseGrid();
		grid1.setId("id1");
		grid2.setId("id2");
		mongoOps.save(grid1);
		mongoOps.save(grid2);
		List<DatabaseGrid> allGrids=repo.findAll();
		assertEquals(2,allGrids.size());
		assertEquals("id1",allGrids.get(0).getId());
		assertEquals(0,allGrids.get(0).getN());
		assertArrayEquals(new int[0][0],allGrids.get(0).getMatrix());
		assertEquals("id2",allGrids.get(1).getId());
		assertEquals(0,allGrids.get(1).getN());
		assertArrayEquals(new int[0][0],allGrids.get(1).getMatrix());
	}

}