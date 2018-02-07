package com.alexfoglia.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
