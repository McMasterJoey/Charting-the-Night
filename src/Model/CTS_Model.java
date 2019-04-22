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
	ArrayList<CTS_DeepSkyObject> DSOlist;
	ArrayList<CTS_Constellation> Constellations;
	
	// The days since J2000, including the decimal portion
	private double daysSinceStandard;
	private double universalTime;
	
	private double latitude;
	private double longitude;
	
	public CTS_Model() {
		// Generate list of star objects
		starList = new ArrayList<CTS_Star>();
		Constellations = new ArrayList<CTS_Constellation>();
		build_starList();
		this.constellationTestPrint();
		
		// Generate list of deep sky objects
		DSOlist = new ArrayList<CTS_DeepSkyObject>();
		build_DSOlist();
		
		// Set default days since standard and universal time
		daysSinceStandard = 0.0;
		universalTime = 0.0;
		
		// Set default latitude / longitude
		latitude = 0.0;
		longitude = 0.0;
		
		
	}
	/**
	 * Constructs the model with the given params
	 * @param latitude The latitude on earth of where the observer will be
	 * @param longitude The longitude on earth where the observer will be.
	 * @param daysSinceStanderd Days since the year 2000
	 * @param universaltime  Time of day of where the observer will be.
	 */
	public CTS_Model(double latitude, double longitude, double daysSinceStanderd, double universaltime) {
		// Generate list of star objects
		starList = new ArrayList<CTS_Star>();
		Constellations = new ArrayList<CTS_Constellation>();
		
		build_starList();
		
		// Generate list of deep sky objects
		DSOlist = new ArrayList<CTS_DeepSkyObject>();
		build_DSOlist();
				
		// Set params with custom values.
		this.daysSinceStandard = daysSinceStanderd;
		this.universalTime = universaltime;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Fetch star list
	 * 
	 * @return ArrayList of star objects
	 */
	public ArrayList<CTS_Star> getStarList() {
	    return this.starList;
	}
	
	
	/**
	 * Fetch DSO list
	 * 
	 * @return ArrayList of deep sky objects
	 */
	public ArrayList<CTS_DeepSkyObject> getDSOlist() {
		return this.DSOlist;
	}
	
	/**
	 * Parse Stars.csv database to build list of stars
	 */
	private void build_starList() {
		
		BufferedReader fileReader = null;
		int id; 
		String name = null;
		String constellation;
		double magnitude;
		double rightAscension; 
		double declination;
		
		try {
			fileReader = new BufferedReader(new FileReader(".\\src\\Resources\\Stars.csv"));
			String line = "";
			String[] tokens;
		    
			// Read all lines of csv file, skipping the header line:
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
		        rightAscension = Double.valueOf(tokens[7]) * 15;
		        declination = Double.valueOf(tokens[8]);	
		        
		        // Create new star object and add to starList
		        CTS_Star newStar = new CTS_Star(id, name, magnitude, rightAscension, declination);
		        starList.add(newStar);
		        
		        //Instead of putting everything inside more tabs in an if, I'm just going to skip it if there's nothing there
		        if (tokens[5].equals("")) {
		        	continue;
		        }
		        //Find the name of our constellation next
		        String confirmedName = "";
		        String[] values = tokens[5].split(" ");
		        //If there's only one value AND that value is length of three, that's our constellation
		        //(For one that has one value that isn't a constellation, look at 761 on the csv)
		        if (values.length == 1 && values[0].length() == 3) {
		        	confirmedName = values[0];
		        //Okay, I think there can only be 2, but the definition of what goes in this slot makes it unclear, so I'm
		        //adding this safety just in case
		        } else if (values[values.length-1].length() == 3){
		        	confirmedName = values[values.length-1];
		        }
		        //Move on if there was something in slot six but it wasn't a constellation name as far as we could tell
		        if (confirmedName.equals("")) {
		        	continue;
		        }
		        //Now we know we do have a constellation name, check if it already exists
		        CTS_Constellation alreadyHere = this.searchForExisting(confirmedName);
		        if (alreadyHere != null) {
		        	alreadyHere.add(newStar);
		        } else {
		        	CTS_Constellation newConstellation = new CTS_Constellation(confirmedName, 0.0, 0.0);
		        	newConstellation.add(newStar);
		        	this.Constellations.add(newConstellation);
		        }
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
	
	/**
	 * Parse DeepSkyObjects.csv to build list of deep sky objects
	 */
	private void build_DSOlist() {
		
		BufferedReader fileReader = null;
		int id; 
		String name = null;
		double magnitude;
		double rightAscension; 
		double declination;
		
		try {
			fileReader = new BufferedReader(new FileReader(".\\src\\Resources\\DeepSkyObjects.csv"));
			String line = "";
			String[] tokens;
			
			// Read all lines of csv file, skipping the header line:
			fileReader.readLine();			
		    while ((line = fileReader.readLine()) != null)
		    {
		    	// Get all tokens available in line
		        tokens = line.split(",");
		        
		        // Get relevant DSO data from line
		        id = Integer.valueOf(tokens[8]);
		        
		        if (!tokens[5].isEmpty()) {
		        	name = tokens[5];
		        } else {
		        	name = "UNNAMED DEEP SKY OBJECT";
		        }
		        
		        // Some DSO in the database do not have magnitude given: ignore these
		        // and do not create the object
		        if (tokens[4].isEmpty()) {
		        	continue;
		        }
		        
		        magnitude = Double.valueOf(tokens[4]);		        
		        rightAscension = Double.valueOf(tokens[0]) * 15;
		        declination = Double.valueOf(tokens[1]);	
		        
		        // Create new star object and add to starList
		        DSOlist.add(new CTS_DeepSkyObject(id, name, magnitude, rightAscension, declination));	
		        
		        
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
	
	public void setLatitude(double lat) {
		latitude = lat;
	}
	
	public void setLongitude(double lon) {
		longitude = lon;
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
		double lst = 100.46 + 0.985647 * daysSinceStandard + (15 + longitude) * universalTime;

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
	    double minFrac = Double.valueOf(minutes / 60);
	    double secFrac = Double.valueOf(seconds / 3600);
	    universalTime = hour + minutes + seconds;
    }
	
    /**
     * Fetches the days from J2000.
     * @return a double indicating the days from J2000
     */
	public double getDaysSinceStandard() {
	    return daysSinceStandard;
    }
	
	/**
	 * Searches the list of existing constellations we have to see if an existing constellation exists
	 * @param name The name of the constellation to search for
	 * @return The constellation in question if it's found, null if nothing is found. 
	 */
	public CTS_Constellation searchForExisting(String name) {
		for (CTS_Constellation x : this.Constellations) {
			if (x.name.equals(name)) {
				return x;
			}
		}
		return null;
	}
	
	/**
	 * This is just a test method, delete this later
	 */
	private void constellationTestPrint() {
		for (CTS_Constellation x : this.Constellations) {
			System.out.println("This constellation has the name " + x.name +" and the following stars:");
			x.prinStars();
			System.out.println();
		}
	}
}
