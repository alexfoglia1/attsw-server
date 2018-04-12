package com.alexfoglia.server;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebSecurityConfig.class)
public class RestControllerIT {

	@LocalServerPort
	private int port;
	@Autowired
	private IGridService srv;
	private String url;

	@Before
	public void setup() {
		url = "http://localhost:" + port;
		srv.deleteAll();
	}
	
	@After
	public void teardown() {
		srv.deleteAll();
	}
	
	@Test
	public void testGetAllNamesWhenNoGridExists() throws Exception {
		given().when().get(url + "/api").then().statusCode(200).assertThat().body(is("[]"));
	}

	private List<String> getAllIds() {
		List<String> allIds = new ArrayList<>();
		srv.findAllGridsInDb().forEach((DatabaseGrid e) -> allIds.add(e.getId()));
		return allIds;
	}

	private String allIdsToJson() {
		Gson gson = new Gson();
		return gson.toJson(getAllIds());
	}

	private void assertCorrectRetrievingOfAllNames() {
		given().when().get(url + "/api").then().statusCode(200).assertThat().body(is(allIdsToJson()));
	}

	@Test
	public void testGetAllNamesWithExistingOneGrid() throws Exception {
		srv.storeInDb(1, new int[][] { { 1 } });
		assertCorrectRetrievingOfAllNames();
	}

	@Test
	public void testGetAllNamesWithExistingMoreGrids() throws Exception {
		srv.storeInDb(1, new int[][] { { 1 } });
		srv.storeInDb(1, new int[][] { { 1 } });
		assertCorrectRetrievingOfAllNames();
	}

	@Test
	public void testGetOneGridWhenItExists() throws Exception {
		srv.storeInDb(1, new int[][] { { 1 } });
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		String genId = saved.getId();
		Gson gson = new Gson();
		given().when().get(url + "/api/grid" + genId).then().statusCode(200).assertThat()
				.body(is(gson.toJson(saved)));
	}

	@Test
	public void testGetOneGridWhenItDoesNotExists() throws Exception {
		given().when().get(url + "/api/grid1").then().statusCode(200).assertThat().body(is("null"));
	}

	@Test
	public void testGetPathWhenItsEmpty() {
		srv.storeInDb(2, new int[][] { { 0, 0 }, { 0, 0 } });
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		String genId = saved.getId();
		given().when().get(url + "/api/path0_0TO0_1IN"+genId).then().statusCode(200).assertThat().body(is("[]"));
	}

	@Test
	public void testGetPathWhenItsNotEmpty() {
		srv.storeInDb(2, new int[][] { { 1, 1 }, { 1, 1 } });
		DatabaseGrid saved=srv.findAllGridsInDb().get(0);
		String genId = saved.getId();
		given().when().get(url + "/api/path0_0TO0_1IN"+genId).then().statusCode(200).assertThat()
				.body(is("[\"0_0\",\"0_1\"]"));
	}

}