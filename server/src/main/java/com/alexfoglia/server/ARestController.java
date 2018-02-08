package com.alexfoglia.server;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
		List<String> allids= new LinkedList<String>();
		grids.forEach(grid->allids.add(grid.getId()));
		return serializer.toJson(allids);
		
	}

}
