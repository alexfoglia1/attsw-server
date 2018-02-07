package com.alexfoglia.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class GridServiceTest {

	@Mock
	private IMongoRepository repo;
	@InjectMocks
	private ConcreteGridService serv;
	@Test
	public void testFindOneByIdWhenExisting() {
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(1,new int[][] {{1}}));
		DatabaseGrid found=serv.findOneById("test_id");
		verify(repo,times(1)).findOne("test_id");
		assertEquals(1,found.getN());
		assertArrayEquals(new int[][] {{1}},found.getMatrix());
	}
	@Test
	public void testFindOneByIdWhenNonExisting() {
		when(repo.findOne("test_id")).thenReturn(null);
		assertNull(serv.findOneById("test_id"));
		verify(repo,times(1)).findOne("test_id");
	}
	@Test
	public void testFindAllGridsInDbWhenThereAreNoGrids() {
		when(repo.findAll()).thenReturn(Arrays.asList());
		List<DatabaseGrid> found=serv.findAllGridsInDb();
		verify(repo,times(1)).findAll();
		assertEquals(0,found.size());
		
	}
	@Test
	public void testFindAllGridsInDbWhenThereIsOneGrid() {
		when(repo.findAll()).
			thenReturn
				(Arrays.asList
						(new DatabaseGrid(1,new int[][] {{1}}))
				);
		List<DatabaseGrid> found=serv.findAllGridsInDb();
		verify(repo,times(1)).findAll();
		assertEquals(1,found.size());
		DatabaseGrid unique=found.get(0);
		assertEquals(1,unique.getN());
		assertArrayEquals(new int[][] {{1}},unique.getMatrix());
	}
	@Test
	public void testFindAllGridsInDbWhenThereAreMultipleGrids() {
		when(repo.findAll()).
		thenReturn
			(Arrays.asList
					(new DatabaseGrid(1,new int[][] {{1}}),
					new DatabaseGrid(0,new int[0][0]))
			);
		List<DatabaseGrid> found=serv.findAllGridsInDb();
		verify(repo,times(1)).findAll();
		assertEquals(2,found.size());
	}
}
