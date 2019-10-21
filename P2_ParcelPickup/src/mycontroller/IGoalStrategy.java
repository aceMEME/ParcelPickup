package mycontroller;

import utilities.Coordinate;

public interface IGoalStrategy {
	
	public Coordinate getGoal(MapSearch map, Coordinate currentPost);


}
