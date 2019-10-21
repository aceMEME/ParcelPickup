package mycontroller;

import controller.CarController;
import swen30006.driving.Simulation;
import world.Car;
import world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;
			
		HashMap<Coordinate, Boolean> wallsFollowed = new HashMap<Coordinate, Boolean>();
		
		Coordinate startPosition;
		
		// theMap responsible for tracking what we've seen + determining shortest path to our goal (dependent on strategy) 
		MapSearch theMap;
				
		int minParcels;

		

		
		public MyAutoController(Car car) {
			super(car);
			
			// Initialize the map + our initial position
			theMap = new MapSearch();
			startPosition = new Coordinate(getPosition());
			theMap.visit(startPosition);
			minParcels = car.targetParcels;
			

		}
		
		@Override
		public void update() {
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			// Gets current position 
			Coordinate current = new Coordinate(getPosition());
			
			// Checks if car has visited this point before, if not mark as visited 
			if (!theMap.visited(current)) {
				
			theMap.visit(current);
				
			}
			
			// Add the information from what we're currently viewing to the MapSearch
			theMap.applyNewView(currentView);
			
						
			ExploreStrategy explore = new ExploreStrategy();
			ParcelStrategy parcel = new ParcelStrategy();
			ExitStrategy exit = new ExitStrategy();

			HashMap<Coordinate, MapTile> parcels = theMap.getParcels();
			
			// Next coordinate we're going to move towards 
			Coordinate next = null;
			
			int numFound = this.numParcelsFound();
			


			// If we're not looking for an exit OR we are but havent seen an exit yet AND we've seen a parcel 
			// Try and find a path to it 
			if (theMap.getParcels().size() > 0) {
				
				next = GoalStrategyFactory.getInstance().getStrategy("parcel").getGoal(theMap, current);
								
			}
			
			// If we've picked up enough parcels, look for exit
			// If no exit findable, search for a next state by explore strategy  
			if (next == null && numFound == minParcels) {
				next = GoalStrategyFactory.getInstance().getStrategy("exit").getGoal(theMap, current);
			}
			
			
			// If we've found neither a path to a parcel or the exit (if we've found enough parcels), explore 
			if (next == null) {
				
				next = GoalStrategyFactory.getInstance().getStrategy("explore").getGoal(theMap, current);
			
			System.out.println("EXPLORE MODE");
			
			}
			

			// If we're not moving, start moving 
			if (getSpeed() == 0) {
							
				// If wall ahead of us, reverse 
				if (checkWallAhead(getOrientation(), currentView) ) {				

					applyReverseAcceleration();
				}
				// Otherwise accelerate forwards 
				else {
					System.out.println("Go forward");
					applyForwardAcceleration();
				}
				
				
			}
			
			moveToGoal(next, current, getOrientation());



			
			
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
			boolean onTheRight = false;
			boolean above = false;
			
			// Input is 1 step away, so we're either on the same x level or on the same y level

			if (currentPosition.x == goal.x) {
				sameX = true;
			}
			
			if (currentPosition.y == goal.y) {
				sameY = true;
			}
			
			if (currentPosition.x > goal.x) {
				onTheRight = true;
			}
			
			if (currentPosition.y > goal.y) {
				above = true;
			}
				

			
			// Base actions on our orientation, assumes we're moving due to the check at start of update, so can make any necessary turns 
			switch (orientation) {
			
			
			
			case EAST: 
				

				if (sameX) {
					
					if (above) {
						turnRight();
					}
					
					else {
						turnLeft();
					}
					
				}
				
				if (sameY) {
					
						
						if (onTheRight) {
							
							applyReverseAcceleration();
							
							
						}
						
						else {
							applyForwardAcceleration();
						}
					
				}
				
				break;


				
					
			case WEST: 
				
				if (sameX) {
					
					if(above) {
						turnLeft();
					}
					
					else {
						turnRight();
					}
					
				}
				
				if (sameY) {
					
					if (onTheRight) {
						
						applyForwardAcceleration();
						

					}
					
					else {
						
						applyReverseAcceleration();
					}
					
				}

				break;
				
			case NORTH:
				
				if (sameX) {
					
					if (above) {
							
						applyReverseAcceleration();
							
					}
					
					else {
						

						applyForwardAcceleration();
						
					}
				}
				
				if(sameY) {
					
					if (onTheRight) {
						
						turnLeft();
						
					}
					
					else {
						turnRight();
					}
					
				}
				
				break;
				
			case SOUTH: 
				
				if (sameX) {
					
					if (above) {
							
							applyForwardAcceleration();
							

					}
					
					else {
						
						applyReverseAcceleration();
						
					}
					

				}
				
				if(sameY) {
					
					if (onTheRight) {
						
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
