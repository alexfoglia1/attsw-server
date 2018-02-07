package com.alexfoglia.server;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class GridServiceTest {

	@Mock
	private IMongoRepository repo;
	@InjectMocks
	private IGridService serv;
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
