package com.alexfoglia.server;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebSecurityConfig.class)
public class WebControllerIT {

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private FilterChainProxy springSecurityFilter;
	protected MockMvc mockMvc;
	@Autowired
	private IGridService srv;
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(springSecurityFilter).build();
		srv.deleteAll();
	}
	
	@After
	public void teardown() {
		srv.deleteAll();
	}
	
	@Test
	public void testLogin401() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().is(401));
	}

	@Test
	public void testLoginOk() throws Exception {
		mockMvc.perform(get("/").with(httpBasic("user", "password")))
			.andExpect(status().isOk());
	}

	@Test
	public void testHomepage() throws Exception {
		mockMvc.perform(get("/").with(httpBasic("user", "password")))
			.andExpect(view().name("database"));
	}

	@Test
	public void testViewDb() throws Exception {
		mockMvc.perform(get("/viewdb").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("dbview")).andExpect(model().attribute("allGrids", new LinkedList<>()));
	}

	@Test
	public void testViewDbWhenThereIsOneGrid() throws Exception {
		srv.storeInDb(0, new int[0][0]);
		DatabaseGrid saved = srv.findAllGridsInDb().get(0);
		mockMvc.perform(get("/viewdb").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("dbview"))
				.andExpect(model().attribute("allGrids", Arrays.asList(saved)));
	}

	@Test
	public void testViewDbWhenThereAreMultipleGrids() throws Exception {
		srv.storeInDb(0, new int[0][0]);
		srv.storeInDb(0, new int[0][0]);
		mockMvc.perform(get("/viewdb").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("dbview"))
				.andExpect(model().attribute("allGrids", srv.findAllGridsInDb()));
	}

	@Test
	public void testAddTableRendering() throws Exception {
		mockMvc.perform(get("/addtable").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("tableadd")).andExpect(model().attribute("usercontent", isA(UserContent.class)))
				.andExpect(model().attributeDoesNotExist("errormessage"));
	}

	@Test
	public void testAddOneTable() throws Exception {
		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "1010").param("number", "2"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/viewdb"));
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		assertEquals(2,saved.getN());
		assertArrayEquals(new int[][] {{1,0},{1,0}},saved.getMatrix());
	}

	@Test
	public void testAddOneTableWhenMissingContent() throws Exception {
		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "1010").param("number", "3"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/viewdb"));
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		assertEquals(3,saved.getN());
		assertArrayEquals(new int[][] {{1,0,1},{0,0,0},{0,0,0}},saved.getMatrix());
	}

	@Test
	public void testAddOneTableWhenTooContent() throws Exception {
		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "11101010101010")
				.param("number", "2")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/viewdb"));
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		assertEquals(2,saved.getN());
		assertArrayEquals(new int[][] {{1,1},{1,0}},saved.getMatrix());
		
	}

	@Test
	public void testAddOneTableWhenWrongContent() throws Exception {
		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "pippopluto")
				.param("number", "2")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/viewdb"));
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		assertEquals(2,saved.getN());
		assertArrayEquals(new int[][] {{0,0},{0,0}},saved.getMatrix());
	}

	@Test
	public void testRemoveTable() throws Exception {
		srv.storeInDb(0, new int[0][0]);
		String testid = srv.findAllGridsInDb().get(0).getId();
		mockMvc.perform(get("/remtable?id=" + testid).with(httpBasic("user", "password")))
				.andExpect(status().is3xxRedirection());
		assertTrue(srv.findAllGridsInDb().isEmpty());
		
	}

}