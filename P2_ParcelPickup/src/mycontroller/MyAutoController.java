package mycontroller;

import controller.CarController;
import world.Car;
import world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		
		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;
		
		HashMap<Coordinate,MapTile> worldMap = World.getMap();
		
		HashMap<Coordinate, Boolean> wallsFollowed = new HashMap<Coordinate, Boolean>();
		
		Coordinate startPosition;
		
		// Determines if time to head home 
		boolean headHome = false;
		// theMap responsible for tracking what we've seen + determining shortest path to our goal (dependent on strategy) 
		MapSearch theMap;
		

		
		public MyAutoController(Car car) {
			super(car);
			
			// Initialize the map + our initial position
			theMap = new MapSearch();
			startPosition = new Coordinate(getPosition());


		}
		
		@Override
		public void update() {
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			
			// ------------------------------------------------
			// Just using the headHome mechanic at the moment to test heading to a point that we know how to get to 
			// When agent reaches headHomePoint (currently by following the wall) it begins heading home
			// As it reached the headHome point and updated its map accordingly, it must know the way home 
			// At this point the BFS path finding logic kicks in and provides the coordinates to next square to go to
			// moveToGoal handles the necessary movement to get there 
			Coordinate current = new Coordinate(getPosition());

			Coordinate headHomePoint = new Coordinate(1,17);
			
			Coordinate next;
			
			if (current.equals(headHomePoint)) {
				
				headHome = true;
				
			}
			System.out.println(headHome);
			
			// -------------------------------------------
			
			theMap.applyNewView(currentView);


			// If we're not moving, start moving 
			if (getSpeed() == 0) {
				
				System.out.println("Not moving, need to start moving");
				
				// If wall ahead of us, reverse 
				if (checkWallAhead(getOrientation(), currentView) ) {
					System.out.println("Reverse");
					

					applyReverseAcceleration();
				}
				// Otherwise accelerate forwards 
				else {
					System.out.println("Go forward");
					applyForwardAcceleration();
				}
				
				
			}
			
			

			// If we're heading home, follow the pathfinding logic we'll use for the rest of the project 
			// the alternative is the default explore logic we'll replace with a dedicated exploration strategy 
			if (headHome) {
				
				next = theMap.BFSSearch(current, startPosition);

				System.out.println("Head to " + next.toString());
											
				moveToGoal(next, current, getOrientation());

			}
			
			
			else {		
			
			// checkStateChange();
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}
			if (isFollowingWall) {
				// If wall no longer on left, turn left
				if(!checkFollowingWall(getOrientation(), currentView)) {
					turnLeft();
				} else {
					// If wall on left and wall straight ahead, turn right
					if(checkWallAhead(getOrientation(), currentView)) {
						turnRight();
					}
				}
			} else {
				// Start wall-following (with wall on left) as soon as we see a wall straight ahead
				if(checkWallAhead(getOrientation(),currentView)) {
					turnRight();
					isFollowingWall = true;
				}
			}
		}
			
			
		}
		

				

				

			


		
		
		/**
		 * Check if you have a wall in front of you!
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
			switch(orientation){
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
			}
		}
		

		/**
		 * Check if the wall is on your left hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			
			switch(orientation){
			case EAST:
				return checkNorth(currentView);
			case NORTH:
				return checkWest(currentView);
			case SOUTH:
				return checkEast(currentView);
			case WEST:
				return checkSouth(currentView);
			default:
				return false;
			}	
		}
		
		/**
		 * Method below just iterates through the list and check in the correct coordinates.
		 * i.e. Given your current position is 10,10
		 * checkEast will check up to wallSensitivity amount of tiles to the right.
		 * checkWest will check up to wallSensitivity amount of tiles to the left.
		 * checkNorth will check up to wallSensitivity amount of tiles to the top.
		 * checkSouth will check up to wallSensitivity amount of tiles below.
		 */
		public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
			// Check tiles to my right
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to my left
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to towards the top
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles towards the bottom
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		private void moveToGoal(Coordinate goal, Coordinate currentPosition, Direction orientation) {
			
			// Gather our positon relative to the goal
			boolean sameX = false;
			boolean sameY = false;
			boolean greaterX = false;
			boolean greaterY = false;
			
			// Input is 1 step away, so we're either on the same x level or on the same y level

			if (currentPosition.x == goal.x) {
				sameX = true;
			}
			
			if (currentPosition.y == goal.y) {
				sameY = true;
			}
			
			if (currentPosition.x > goal.x) {
				greaterX = true;
			}
			
			if (currentPosition.y > goal.y) {
				greaterY = true;
			}
				

			
			// Base actions on our orientation, assumes we're moving due to the check at start of update, so can make any necessary turns 
			switch (orientation) {
			
			
			
			case EAST: 
				

				if (sameX) {
					
					if (greaterY) {
						turnRight();
					}
					
					else {
						turnLeft();
					}
					
				}
				
				if (sameY) {
					
						
						if (greaterX) {
							
							applyReverseAcceleration();
							
							
						}
						
						else {
							applyForwardAcceleration();
						}
					
				}
				
				break;


				
					
			case WEST: 
				
				if (sameX) {
					
					if(greaterY) {
						turnLeft();
					}
					
					else {
						turnRight();
					}
					
				}
				
				if (sameY) {
					
					if (greaterX) {
						
						applyForwardAcceleration();
						

					}
					
					else {
						
						applyReverseAcceleration();
					}
					
				}

				break;
				
			case NORTH:
				
				if (sameX) {
					
					if (greaterY) {
							
						applyReverseAcceleration();
							
					}
					
					else {
						

						applyForwardAcceleration();
						
					}
				}
				
				if(sameY) {
					
					if (greaterX) {
						
						turnLeft();
						
					}
					
					else {
						turnRight();
					}
					
				}
				
				break;
				
			case SOUTH: 
				
				if (sameX) {
					
					if (greaterY) {
							
							applyForwardAcceleration();
							

					}
					
					else {
						
						applyReverseAcceleration();
						
					}
					

				}
				
				if(sameY) {
					
					if (greaterX) {
						
						turnRight();
						
					}
					
					else {
						turnLeft();
					}
					
				}
				
				break;
					
	}
					
}
		
	}
