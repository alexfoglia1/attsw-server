package com.alexfoglia.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
	public void testDeleteAllIsSecured() throws Exception {
		mvc.perform(get("/cleardb")).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testDeleteAll() throws Exception {
		mvc.perform(get("/cleardb").with(httpBasic("user","password"))).andExpect(status().isOk());
		verify(gridService,times(1)).deleteAll();
	}
	@Test
	public void testPopuleIsSecured() throws Exception {
		mvc.perform(get("/populate")).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testPopule() throws Exception {
		mvc.perform(get("/populate").with(httpBasic("user","password"))).andExpect(status().isOk());
		verify(gridService,times(1)).deleteAll();
		verify(gridService,times(3)).storeInDb(anyInt(), anyObject());
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

	private void whenPathReturn(IGridService gs,String from, String to, String id,List<String> toreturn) {
		given(gs.getShortestPath(from, to, id)).
		willReturn(toreturn);
	}

	@Test
	public void testPathWhenOneNode() throws Exception{
		whenPathReturn(gridService,"0_0","0_2","0",Arrays.asList("0"));
		this.mvc.perform(get("/api/path0_0TO0_2IN0")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]",is("0")));
		verify(gridService,times(1)).getShortestPath("0_0", "0_2", "0");
	}

	@Test
	public void testPathWhenNoNodes() throws Exception{
		whenPathReturn(gridService,"0_0","0_2","0",Arrays.asList());
		this.mvc.perform(get("/api/path0_0TO0_2IN0")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$",hasSize(0)));
		verify(gridService,times(1)).getShortestPath("0_0", "0_2", "0");
	}

	@Test
	public void testPathWhenMoreThanOneNode() throws Exception{
		whenPathReturn(gridService,"0_0","0_2","0",Arrays.asList("0_0","0_1","0_2"));
		this.mvc.perform(get("/api/path0_0TO0_2IN0")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]",is("0_0")))
			.andExpect(jsonPath("$[1]",is("0_1")))
			.andExpect(jsonPath("$[2]",is("0_2")));
		verify(gridService,times(1)).getShortestPath("0_0", "0_2", "0");
	}

}