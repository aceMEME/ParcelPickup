package controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.Input;
import world.Car;
import swen30006.driving.Simulation;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

// Manual Controls for the car
public class ManualController extends CarController {
	
	public ManualController(Car car){
		super(car);
	}
	
	public void update(){
		
		HashMap<Coordinate, MapTile> currentView = getView();
		
	    Iterator it = currentView.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        MapTile tile = (MapTile) pair.getValue();
	        
	        if (tile.getType() == Type.TRAP){
	        
	        String type = tile.getType().toString();
	        System.out.println("TRAP DETECTED AT "  + pair.getKey() + " = " +  type );
	        
	        }
	        // avoids a ConcurrentModificationException
	    }		

		
		Set<Integer> parcels = Simulation.getParcels();
		Simulation.resetParcels();
        for (int k : parcels){
		     switch (k){
		        case Input.Keys.B:
		        	applyBrake();
		            break;
		        case Input.Keys.UP:
		        	applyForwardAcceleration();
		            break;
		        case Input.Keys.DOWN:
		        	applyReverseAcceleration();
		        	break;
		        case Input.Keys.LEFT:
		        	turnLeft();
		        	break;
		        case Input.Keys.RIGHT:
		        	turnRight();
		        	break;
		        default:
		      }
		  }
	}
}
