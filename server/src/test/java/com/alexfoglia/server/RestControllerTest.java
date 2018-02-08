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
	
}