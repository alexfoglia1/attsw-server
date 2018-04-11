package com.alexfoglia.server;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class ARestController {

	@Autowired
	private IGridService service;

	@GetMapping("/api")
	public String index() {
		Gson serializer=new Gson();
		List<DatabaseGrid> grids=service.findAllGridsInDb();
		List<String> allids= new LinkedList<>();
		grids.forEach(grid->allids.add(grid.getId()));
		return serializer.toJson(allids);
	}

	@GetMapping("/api/grid{name}")
	public String retrieveGrid(@PathVariable String name) {
		Gson serializer=new Gson();
		return serializer.toJson(service.findOneById(name));
	}

	@GetMapping("/api/path{source}TO{sink}IN{grid}")
	public String path(@PathVariable String source, @PathVariable String sink, @PathVariable String grid) {
		List<String> minpath= service.getShortestPath(source, sink, grid);
		Gson serializer=new Gson();
		return serializer.toJson(minpath);
	}
	
	@GetMapping("/cleardb")
	public String deleteAll() {
		service.deleteAll();
		return "Database cleaned";
	}
	
	@GetMapping("/populate")
	public String populate() {
		service.deleteAll();
		service.storeInDb(0, new int[0][0]);
		service.storeInDb(1, new int[][] {{1}});
		service.storeInDb(3, new int[][] {{1,1,1},{1,0,1},{1,1,0}});
		return "Database populed";
	}
	
}
