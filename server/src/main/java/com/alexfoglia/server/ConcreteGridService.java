package com.alexfoglia.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
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
		if(grid==null) throw new MyRuntimeException(String.format("Grid with id: %s not found",id));
		return getShortestPath(source,sink,grid);
	}

	private List<String> getShortestPath(String source, String sink, DatabaseGrid grid) {
		if(!grid.isEnabled(source) || !grid.isEnabled(sink)) return new LinkedList<>();
		else if(source.equals(sink)) return Arrays.asList(source);
		return manageGraph(source,sink,grid);
	}

	private List<String> manageGraph(String source, String sink, DatabaseGrid grid) {
		List<String> vertexSet=createNodes(grid);
		List<String[]> edgeSet=createEdges(grid,vertexSet);
		return minPath(source,sink,edgeSet);
	}

	private List<String[]> createEdges(DatabaseGrid grid,List<String> vertexSet) {
		List<String[]> edges=new LinkedList<>();
		vertexSet.forEach(vertex->addEdge(vertex,edges,grid));
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
		List<Integer> outOfBoundsValues=Arrays.asList(-1,grid.getN());
		if(outOfBoundsValues.contains(i) || outOfBoundsValues.contains(j)) return;
		if (grid.isEnabled(String.format(D_D,i,j))) {
			String target = String.format(D_D, i, j);
			edges.add(new String[] {vertex,target});
		}
	}

	private List<String> createNodes(DatabaseGrid grid) {
		List<String> nodes=new LinkedList<>();
		int n=grid.getN();
		for(int i=0; i<n;i++) {
			for(int j=0; j<n;j++) {
				String target=String.format(D_D, i,j);
				if(grid.isEnabled(target)) {
					nodes.add(target);
				}
			}
		}
		return nodes;
	}

	private List<String> minPath(String from, String to, List<String[]> edgeSet) {
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
				for(String neigh:neighboursOf(current,edgeSet)) {
					if(!vis.containsKey(neigh)) {
						queue.add(neigh);
						vis.put(neigh,true);
						prev.put(neigh,current);
					}
				}
			}
		}
		if(!current.equals(to)) {
			return new LinkedList<>();
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
