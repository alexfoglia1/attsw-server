package com.alexfoglia.server;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@WebMvcTest(controllers=AWebController.class)
@Import(WebSecurityConfig.class)
public class WebControllerTest  {
	
	@Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilter;

    protected MockMvc mockMvc;
    protected MockHttpSession session;
    
    @Before
    public void setup() throws Exception{
        this.session = new MockHttpSession();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilter)
                .build();
    }

	@MockBean
	private IGridService gridService;
	
	@Test
	public void testLogin401() throws Exception {
		
		mockMvc.perform(get("/")).andExpect(status().is(401));
	}
	@Test
	public void testLoginOk() throws Exception{
		mockMvc.perform(get("/").with(httpBasic("user","password"))).andExpect(
				status().isOk());
	}
	@Test
	public void testHomepage() throws Exception {
		mockMvc.perform(get("/").with(httpBasic("user","password"))).
			andExpect(view().name("database"));
			
	}
	@Test
	public void testViewDb() throws Exception{
		mockMvc.perform(get("/viewdb").with(httpBasic("user","password"))).andExpect(
				status().isOk()).
				andExpect(view().name("dbview")).
				andExpect(model().attribute("allGrids", new LinkedList<>()));
		verify(gridService,times(1)).findAllGridsInDb();
	}
	@Test
	public void testViewDbWhenThereIsOneGrid() throws Exception{
		when(gridService.findAllGridsInDb()).thenReturn(Arrays.asList(new DatabaseGrid()));
		mockMvc.perform(get("/viewdb").with(httpBasic("user","password"))).andExpect(
				status().isOk()).
				andExpect(view().name("dbview")).
				andExpect(model().attribute("allGrids", Arrays.asList(new DatabaseGrid())));
		verify(gridService,times(1)).findAllGridsInDb();
	}
	@Test
	public void testViewDbWhenThereAreMultipleGrids() throws Exception{
		when(gridService.findAllGridsInDb()).thenReturn(Arrays.asList(new DatabaseGrid(),new DatabaseGrid()));
		mockMvc.perform(get("/viewdb").with(httpBasic("user","password"))).andExpect(
				status().isOk()).
				andExpect(view().name("dbview")).
				andExpect(model().attribute("allGrids", Arrays.asList(new DatabaseGrid(),new DatabaseGrid())));
		verify(gridService,times(1)).findAllGridsInDb();
	}
}