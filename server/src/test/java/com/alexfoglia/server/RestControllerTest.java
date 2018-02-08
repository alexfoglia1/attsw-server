package com.alexfoglia.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
@RunWith(SpringRunner.class)
@WebMvcTest(controllers=ARestController.class)
@Import(WebSecurityConfig.class)
public class RestControllerTest {
	@Autowired
	private MockMvc mvc;
	@MockBean
	private IGridService gridService;
	
	@Test
	public void testStatus200() throws Exception{
		mvc.perform(get("/api")).andExpect(status().isOk());
	}
	@Test
	public void testGetAllIdsWhenNoGridExists() throws Exception {
		whenGetAllGridsReturn(gridService,new LinkedList<DatabaseGrid>());
		this.mvc.perform(get("/api")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$",hasSize(0)));
				
			verify(gridService,times(1)).findAllGridsInDb();
		
	}
	private void whenGetAllGridsReturn(IGridService gs,List<DatabaseGrid> toreturn) {
		given(gs.findAllGridsInDb()).willReturn(toreturn);
	}
	@Test
	public void testApiWhenSingleGridExists() throws Exception {
		DatabaseGrid toreturn=new DatabaseGrid();
		toreturn.setId("0");
		whenGetAllGridsReturn(gridService,Arrays.asList(toreturn));
		this.mvc.perform(get("/api")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]",is("0")));
				
			verify(gridService,times(1)).findAllGridsInDb();
	}
	@Test
	public void testApiWhenMoreThanOneGridExists() throws Exception{
		DatabaseGrid grid1=new DatabaseGrid();
		DatabaseGrid grid2=new DatabaseGrid();
		grid1.setId("0");
		grid2.setId("1");
		whenGetAllGridsReturn(gridService,Arrays.asList(grid1,grid2));
		this.mvc.perform(get("/api")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]",is("0")))
			.andExpect(jsonPath("$[1]",is("1")));
		verify(gridService,times(1)).findAllGridsInDb();
	}
	
	@Test
	public void testGrid() throws Exception{
		int[][] mat=new int[][] {
			{1,1},
			{0,1}
		};
		DatabaseGrid toreturn=new DatabaseGrid(2,mat);
		toreturn.setId("1");
		given(gridService.findOneById("1")).
			willReturn(toreturn);
		
		this.mvc.perform(get("/api/grid1")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("n",is(2)))
			.andExpect(jsonPath("matrix",hasSize(2)))
			.andExpect(jsonPath("id",is("1")));
			
		verify(gridService,times(1)).findOneById("1");
	}
	@Test
	public void testGridWhenNotExists() throws Exception{
		given(gridService.findOneById("1")).
		willReturn(null);
	
	MvcResult res =this.mvc.perform(get("/api/grid1")
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();
		assertEquals("null",res.getResponse().getContentAsString());
	verify(gridService,times(1)).findOneById("1");
	}
}