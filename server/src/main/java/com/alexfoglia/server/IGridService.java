package com.alexfoglia.server;

import java.util.List;

public interface IGridService {

	public DatabaseGrid findOneById(String id);
	public List<DatabaseGrid> findAllGridsInDb();
	public void deleteOneById(String id);
	public void deleteAll();
	public void storeInDb(int n, int[][]matrix);
	public List<String> getShortestPath(String source, String sink, String id);

}
