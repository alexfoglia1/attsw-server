package com.alexfoglia.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
}