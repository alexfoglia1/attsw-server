package com.alexfoglia.server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class ConcreteGridService implements IGridService {

	@Autowired
	private IMongoRepository repo;
	@Override
	public DatabaseGrid findOneById(String id) {
		return repo.findOne(id);
	}

	@Override
	public List<DatabaseGrid> findAllGridsInDb() {
		return repo.findAll();
	}

	@Override
	public void deleteOneById(String id) {
		repo.delete(id);
		
	}

	@Override
	public void deleteAll() {
		repo.deleteAll();
		
	}

	@Override
	public void storeInDb(int n, int[][] matrix) {
		DatabaseGrid tosave=new DatabaseGrid(n,matrix);
		repo.save(tosave);
		
	}

	@Override
	public List<String> getShortestPath(String source, String sink, String id) {
		DatabaseGrid grid=repo.findOne(id);
		if(grid==null) throw new RuntimeException(String.format("Grid with id: %s not found",id));
		return getShortestPath(source,sink,grid);
	}

	private List<String> getShortestPath(String source, String sink, DatabaseGrid grid) {
		if(!grid.isEnabled(source) || !grid.isEnabled(sink)) return new LinkedList<String>();
		else if(source.equals(sink)) return Arrays.asList(source);
		return null;
	}

}
