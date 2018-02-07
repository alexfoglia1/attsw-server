package com.alexfoglia.server;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteOneById(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeInDb(int n, int[][] matrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getShortestPath(String source, String sink, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
