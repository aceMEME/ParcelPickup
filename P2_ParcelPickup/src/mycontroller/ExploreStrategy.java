package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class ExploreStrategy implements IGoalStrategy{

	@Override
	public Coordinate getGoal(MapSearch map, Coordinate currentPost) {
		
		HashMap<Coordinate, MapTile> unexplored = new HashMap<Coordinate, MapTile>();
			
		HashMap<Coordinate, MapTile> seen = map.getMap();
		
    	ArrayList<Coordinate > immediateNeighbours = new ArrayList<>();

  	
    	Coordinate up  = new Coordinate (currentPost.x, currentPost.y + 1);
    	Coordinate right  = new Coordinate (currentPost.x + 1, currentPost.y );
    	Coordinate left  = new Coordinate (currentPost.x - 1, currentPost.y );
    	Coordinate down  = new Coordinate (currentPost.x, currentPost.y - 1);
   	
    	immediateNeighbours.add(up);
    	immediateNeighbours.add(right);
    	immediateNeighbours.add(left);
    	immediateNeighbours.add(down);

    	for (Coordinate c: immediateNeighbours) {
    		
    		if (!map.visited(c) && !map.getWalls().containsKey(c)) {
    			
    			return c;
    		}
    		
    	}
		
		
    	for (Coordinate coord: seen.keySet()) {
    		
    		if (!map.visited(coord)  && !map.getWalls().containsKey(coord)) {
    			   			
    			unexplored.put(coord, seen.get(coord));
    			System.out.println("to be explored " + coord.x + " , " + coord.y);
    		}
    		
    	}
    	

    	
    	if (unexplored.size() >= 0) {
    		
    		
    		
    		int min = 1000000000;
    		
    		Coordinate goal = null;
    		
    		for (Coordinate coord: unexplored.keySet()) {
    			
    			if (  distanceBetweenCoordinates(  currentPost, coord)  <  min   ) {
    				
    				if (map.BFSSearch(currentPost, coord) != null){
    					
        				min = distanceBetweenCoordinates(currentPost,coord);
        				goal = map.BFSSearch(currentPost, coord);

    					
    				}
    				
    				
    			}
    			
    		}
    		
    		return goal;
    		
    		
    		
    	}
    	

		return null;
	}
	
	public int distanceBetweenCoordinates(Coordinate c1, Coordinate c2) {
		
		int xDiff = c2.x - c1.x;
		xDiff = (int) Math.pow(xDiff, 2);
		int yDiff = c2.y - c1.y;
		yDiff = (int) Math.pow(yDiff, 2);

		return (int) Math.sqrt(xDiff + yDiff);
		
		
	}
	
	



}
