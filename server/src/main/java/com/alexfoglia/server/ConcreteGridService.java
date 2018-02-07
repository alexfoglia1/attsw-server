package com.alexfoglia.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;

public class ConcreteGridService implements IGridService {

	private static final String D_D="%d_%d";
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
		return manageGraph(source,sink,grid);
	}

	private List<String> manageGraph(String source, String sink, DatabaseGrid grid) {
		List<String> vertex_set=createNodes(grid);
		List<String[]> edge_set=createEdges(grid,vertex_set);
		return minPath(source,sink,edge_set);
	}

	private List<String[]> createEdges(DatabaseGrid grid,List<String> vertex_set) {
		List<String[]> edges=new LinkedList<String[]>();
		vertex_set.forEach(vertex->addEdge(vertex,edges,grid));
		return edges;
	}

	private void addEdge(String vertex, List<String[]> edges, DatabaseGrid grid) {
		int i = Integer.parseInt(vertex.split("_")[0]);
		int j = Integer.parseInt(vertex.split("_")[1]);
		addNeighbourIfExists(vertex, edges, grid, i+1, j);
		addNeighbourIfExists(vertex, edges, grid, i-1, j);
		addNeighbourIfExists(vertex, edges, grid, i, j+1);
		addNeighbourIfExists(vertex, edges, grid, i, j-1);
	}

	private void addNeighbourIfExists(String vertex, List<String[]> edges, DatabaseGrid grid, int i, int j) {
		if (grid.isEnabled(String.format(D_D,i,j))) {
			String target = String.format(D_D, i, j);
			edges.add(new String[] {vertex,target});
		}
	}


	private List<String> createNodes(DatabaseGrid grid) {
		int[][] matrix=grid.getMatrix();
		List<String> nodes=new LinkedList<>();
		for(int i=0; i<grid.getN();i++) {
			for(int j=0; j<grid.getN();j++) {
				if(matrix[i][j]>0) {
					nodes.add(String.format(D_D, i,j));
				}
			}
		}
		return nodes;
	}
	public List<String> minPath(String from, String to, List<String[]> edges_set) {
		Map<String,Boolean> vis=new HashMap<>();
		Map<String,String> prev=new HashMap<>();
		List<String> minPath=new LinkedList<>();
		Queue<String> queue= new LinkedList<>();
		String current=from;
		queue.add(current);
		vis.put(current, true);
		
		while(!queue.isEmpty()) {
			current=queue.remove();
			if(current.equals(to)) break;
			else {
				for(String neigh:neighboursOf(current,edges_set)) {
					if(!vis.containsKey(neigh)) {
						queue.add(neigh);
						vis.put(neigh,true);
						prev.put(neigh,current);
					}
				}
			}
		}
		

		for(String node=to; node!=null; node=prev.get(node)) {
			minPath.add(node);
		}
		Collections.reverse(minPath);
		return minPath;
	}
	
	private String[] neighboursOf(String target, List<String[]> edges) {
		StringBuilder neighbours=new StringBuilder();
		for(String[] e:edges) {
			if(e[0].equals(target)) {
				neighbours.append(e[1]+" ");
			}
		}
		return neighbours.toString().split(" ");
	}
}