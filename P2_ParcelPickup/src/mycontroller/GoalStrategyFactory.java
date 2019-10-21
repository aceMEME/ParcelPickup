package mycontroller;

public class GoalStrategyFactory {
	
	public static GoalStrategyFactory goalFactory = null;
	
	private GoalStrategyFactory () {
		
	}
	
	public static GoalStrategyFactory getInstance() {
		
		if (goalFactory == null) {
			goalFactory = new GoalStrategyFactory();
		}
		
		return goalFactory;
	}
	
	public IGoalStrategy getStrategy(String mode) {
		
		if (mode.contentEquals("exit")) {
			return new ExitStrategy();
		}
		
		else if (mode.contentEquals("parcel")) {
			return new ParcelStrategy();
		}
		
		else if (mode.contentEquals("explore")) {
			return new ExploreStrategy();
		}
		return null;
		
	}
	

}
