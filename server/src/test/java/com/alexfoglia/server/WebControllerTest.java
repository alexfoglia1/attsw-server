package com.alexfoglia.server;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AWebController.class)
@Import(WebSecurityConfig.class)
public class WebControllerTest {

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private FilterChainProxy springSecurityFilter;
	protected MockMvc mockMvc;
	@MockBean
	private IGridService gridService;

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(springSecurityFilter).build();
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
		verify(gridService, times(1)).findAllGridsInDb();
	}

	@Test
	public void testViewDbWhenThereIsOneGrid() throws Exception {
		when(gridService.findAllGridsInDb()).thenReturn(Arrays.asList(new DatabaseGrid()));
		mockMvc.perform(get("/viewdb").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("dbview"))
				.andExpect(model().attribute("allGrids", Arrays.asList(new DatabaseGrid())));
		verify(gridService, times(1)).findAllGridsInDb();
	}

	@Test
	public void testViewDbWhenThereAreMultipleGrids() throws Exception {
		when(gridService.findAllGridsInDb()).thenReturn(Arrays.asList(new DatabaseGrid(), new DatabaseGrid()));
		mockMvc.perform(get("/viewdb").with(httpBasic("user", "password"))).andExpect(status().isOk())
				.andExpect(view().name("dbview"))
				.andExpect(model().attribute("allGrids", Arrays.asList(new DatabaseGrid(), new DatabaseGrid())));
		verify(gridService, times(1)).findAllGridsInDb();
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
		int[][] expmat = new int[][] { { 1, 0 }, { 1, 0 } };
		verifyInvokedStoreInDbWithArguments(2, expmat);
	}

	@Test
	public void testAddOneTableWhenMissingContent() throws Exception {

		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "1010").param("number", "3"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/viewdb"));
		int[][] expmat = new int[][] { { 1, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } };
		verifyInvokedStoreInDbWithArguments(3, expmat);
	}

	@Test
	public void testAddOneTableWhenTooContent() throws Exception {

		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "11101010101010")
				.param("number", "2")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/viewdb"));
		int[][] expmat = new int[][] { { 1, 1 }, { 1, 0 } };
		verifyInvokedStoreInDbWithArguments(2, expmat);
	}

	@Test
	public void testAddOneTableWhenWrongContent() throws Exception {
		mockMvc.perform(
				post("/addtable").with(httpBasic("user", "password")).param("content", "pippopluto")
				.param("number", "2")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/viewdb"));
		int[][] expmat = new int[][] { { 0, 0 }, { 0, 0 } };
		verifyInvokedStoreInDbWithArguments(2, expmat);
	}

	@Test
	public void testRemoveTable() throws Exception {
		String testid = "1";
		mockMvc.perform(get("/remtable?id=" + testid).with(httpBasic("user", "password")))
				.andExpect(status().is3xxRedirection());
		verify(gridService, times(1)).deleteOneById(testid);
	}

	private void verifyInvokedStoreInDbWithArguments(int n, int[][] expmat) {
		ArgumentCaptor<Integer> c1=ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<int[][]> c2=ArgumentCaptor.forClass(int[][].class);
		verify(gridService,times(1)).storeInDb(c1.capture(),c2.capture());
		assertEquals(n,c1.getValue().intValue());
		assertArrayEquals(expmat,c2.getValue());
	}
}