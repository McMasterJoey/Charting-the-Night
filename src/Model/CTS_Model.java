package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * The main model used by the project.
 * Stores all objects being displayed by the project.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Model {
	
	ArrayList<CTS_Star> starList;
	
	// The days since J2000, including the decimal portion
	private double daysSinceStandard;
	private double universalTime;
	
	private double latitude;
	private double longitude;
	
	public CTS_Model() {
		// Generate list of star objects
		starList = new ArrayList<CTS_Star>();
		build_starList();
		
		// Set default days since standard and universal time
		daysSinceStandard = 0.0;
		universalTime = 0.0;
		
		// Set default latitude / longitude
		latitude = 0.0;
		longitude = 0.0;
		
		
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
		    
			// Read all lines of Stars.csv file, skipping the header line:
			fileReader.readLine();			
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
	}
	


	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Returns the local siderial time.
	 * ASSUMES: daysSinceStand is set to the decimal days since J2000
	 * ASSUMES: universalTime is appropriately set
	 * @return a double representing the local siderial time for the star
	 */
	public double getLocalSiderialTime() {
		double lst = 100.46 + 0.985647 * daysSinceStandard + 15 * universalTime;

		while (lst < 0) {
			lst += 360;
		}

		while (lst > 360) {
			lst -= 360;
		}

		return lst;
	}
	
    /**
     * Helper method to calculate and set the days since J2000. The time should be in UT.
     * @param year an integer representing the year, CE is positive, BCE is negative
     * @param month an integer representation of the month, 1 = Jan, 2 = Feb, ...
     * @param day an integer representing the day of the month
     * @param hour an integer representing the hour (0-23)
     * @param minutes an integer representing the minutes
     * @param seconds an integer representing the seconds
     */
	public void calcDaysSinceStandard(int year, int month, int day, int hour, int minutes, int seconds) {
	    double j2000 =  2451545.0;
	    double decTime = (3600 * hour + 60 * minutes + seconds);
	    decTime /= 86400;
	    decTime -= 0.5;
	    System.out.println("decTime = " + decTime);
	    // Formula from https://en.wikipedia.org/wiki/Julian_day#Julian_date_calculation
	    double jd = (1461 * (year + 4800 + (month - 14) / 12)) / 4 +
                    (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12 -
                    (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 +
                    day - 32075;
	    jd += decTime;
		System.out.println("jd = " + jd);
	    daysSinceStandard = jd - j2000;
    }
	
    /**
     * Fetches the days from J2000.
     * @return a double indicating the days from J2000
     */
	public double getDaysSinceStandard() {
	    return daysSinceStandard;
    }
}
