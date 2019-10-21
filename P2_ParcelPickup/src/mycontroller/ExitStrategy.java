package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class ExitStrategy implements IGoalStrategy{

	@Override
	public Coordinate getGoal(MapSearch map, Coordinate currentPost) {
		
		
		HashMap<Coordinate, MapTile> exits = map.getExits();
		
		
		for (Coordinate c: exits.keySet()) {
			
			Coordinate potentialGoal = map.BFSSearch(currentPost, c);
			
			if (potentialGoal != null) {
				return potentialGoal;
			}
			
		}

		
		// TODO Auto-generated method stub
		return null;
		
	}

}
