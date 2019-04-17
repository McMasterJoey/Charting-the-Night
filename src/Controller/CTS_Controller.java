package Controller;
import Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Place holder controller for the project
 * Please edit once we do something meaningful with it.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Controller {

	ArrayList<CTS_Star> starList;
	
	
	public CTS_Controller() {
		starList = new ArrayList<CTS_Star>();
		build_starList();
	}

	/**
	 * Parse Stars.csv database to build list of stars
	 */
	private void build_starList() {
		
		BufferedReader fileReader = null;
		int id; 
		String name = null;
		double magnitude;
		double rightAscension; 
		double declination;
		
		try {
			fileReader = new BufferedReader(new FileReader(".\\src\\Resources\\Stars.csv"));
			String line = "";
			String[] tokens;
		    
			// Read all lines of Stars.csv file
			fileReader.readLine(); // skip header
		    while ((line = fileReader.readLine()) != null)
		    {
		    	// Get all tokens available in line
		        tokens = line.split(",");
		        
		        // Get relevant star data from line
		        id = Integer.valueOf(tokens[0]);
		        
		        if (!tokens[4].isEmpty()) {
		        	name = tokens[4];
		        } else if (!tokens[5].isEmpty()) {
		        	name = tokens[5];
		        } else if (!tokens[6].isEmpty()) {
		        	name = tokens[6];
		        } else {
		        	name = "UNNAMED STAR";
		        }
		        
		        magnitude = Double.valueOf(tokens[13]);		        
		        rightAscension = Double.valueOf(tokens[7]);
		        declination = Double.valueOf(tokens[8]);		
		        
		        // Create new star object and add to starList
		        starList.add(new CTS_Star(id, name, magnitude, rightAscension, declination));
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    try {
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    // TODO: DELETE BELOW CODE, IT IS PRINTS FOR TESTING!
	    for (CTS_Star star : starList) {
	    	System.out.println(star.ID() + ",  " + star.name() + ",  " + star.getMagnitude() + ",  " + star.getRightAscension() + ",  " + star.getDeclination());
	    }
	    // END TODO
	}
	
}
