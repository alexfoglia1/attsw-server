package com.alexfoglia.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class GridServiceTest {

	@Mock
	private IMongoRepository repo;

	@InjectMocks
	private ConcreteGridService serv;

	@Captor
	private ArgumentCaptor<DatabaseGrid> captor;

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
				(Arrays.asList(
						new DatabaseGrid(1,new int[][] {{1}}),
						new DatabaseGrid(0,new int[0][0]))
				);
		List<DatabaseGrid> found=serv.findAllGridsInDb();
		verify(repo,times(1)).findAll();
		assertEquals(2,found.size());
	}

	@Test
	public void testDeleteById() {
		serv.deleteOneById("test_id");
		verify(repo,times(1)).delete("test_id");
	}

	@Test
	public void testDeleteAll() {
		serv.deleteAll();
		verify(repo,times(1)).deleteAll();
	}

	@Test
	public void testStoreInDb() {
		serv.storeInDb(0,new int[0][0]);
		verify(repo,times(1)).save(captor.capture());
		assertEquals(0,captor.getValue().getN());
		assertArrayEquals(new int[0][0],captor.getValue().getMatrix());
	}

	@Test(expected=RuntimeException.class)
	public void testGetShortestPathWhenGridDoesNotExist() {
		serv.getShortestPath("", "", "");
	}

	@Test
	public void testGetShortestPathWhenReturningAnEmptyPathDueToNotMatchingRegex() {
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid());
		List<String> path=serv.getShortestPath("", "", "test_id");
		verify(repo,times(1)).findOne("test_id");
		assertEquals(0,path.size());
	}

	@Test
	public void testGetShortestPathWhenReturningAnEmptyPathDueToNonExistingNodes() {
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(2,new int[][] {{1,0},{0,1}}));
		List<String> path=serv.getShortestPath("0_0", "0_1", "test_id");
		assertEquals(0,path.size());
		path=serv.getShortestPath("0_1", "1_1", "test_id");
		assertEquals(0,path.size());
		path=serv.getShortestPath("0_1", "1_0", "test_id");
		assertEquals(0,path.size());
		verify(repo,times(3)).findOne("test_id");
	}

	@Test
	public void testGetShortestPathWhenReturningASinglePath() {
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(1,new int[][] {{1}}));
		List<String> path=serv.getShortestPath("0_0", "0_0", "test_id");
		assertEquals(Arrays.asList("0_0"),path);
	}

	@Test
	public void testGetShortestPathCorrect() {
		int[][]matrix=new int[][] {
			{1,0,1},
			{1,1,1},
			{1,1,1}
		};
		List<String> expectedPath=Arrays.asList("0_0","1_0","1_1","1_2","0_2");
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(3,matrix));
		List<String> actualPath=serv.getShortestPath("0_0", "0_2", "test_id");
		List<String> actualPathReverse=serv.getShortestPath("0_2", "0_0", "test_id");
		assertEquals(expectedPath,actualPath);
		Collections.reverse(expectedPath);
		assertEquals(expectedPath,actualPathReverse);
		verify(repo,times(2)).findOne("test_id");
	}

	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testGetShortestPathWhenUnvalidPath() {
		int[][]matrix=new int[][] {
			{1,0,1},
			{1,1,1},
			{1,1,1}
		};
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(3,matrix));
		serv.getShortestPath("0_0", "0_3", "test_id");
	}

	@Test
	public void testGetShortestPathWhenUnvalidPathWhenExistingSourceAndSinkButNoPaths() {
		int[][]matrix=new int[][] {
			{1,0,1},
			{1,0,1},
			{1,0,1}
		};
		List<String> expectedPath=Arrays.asList();
		when(repo.findOne("test_id")).thenReturn(new DatabaseGrid(3,matrix));
		List<String> actualPath=serv.getShortestPath("0_0", "0_2", "test_id");
		assertEquals(expectedPath,actualPath);
		verify(repo,times(1)).findOne("test_id");
	}

}
