package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class ParcelStrategy implements IGoalStrategy{

	@Override
	public Coordinate getGoal(MapSearch map, Coordinate currentPost) {
		

		HashMap<Coordinate, MapTile> parcels = map.getParcels();
		
		
		for (Coordinate c: parcels.keySet()) {
			
			Coordinate potentialGoal = map.BFSSearch(currentPost, c);
			
			if (potentialGoal != null) {
				return potentialGoal;
			}
			
		}

		
		// TODO Auto-generated method stub
		return null;
	}

}
