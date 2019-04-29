package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

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

	HashMap<String, String> constellationDBs;
	
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
		build_constellationDBMap();
		build_constellationList("western.fab");
		
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
		build_constellationDBMap();
		build_constellationList("western.fab");
		
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
		int id, hip;
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

		        if (!tokens[1].equals("")) {
					hip = Integer.valueOf(tokens[1]);
				} else {
		        	hip = -1;
				}

		        
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
		        CTS_Star newStar = new CTS_Star(id, hip, name, magnitude, rightAscension, declination);
		        starList.add(newStar);
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

	/**
	 * Builds the list of CTS_Constellation objects, given a file name.
	 * The file should be in the Stellarium fab format, with any tabs replaced by spaces.
	 * ASSUMES: the file resides in the src/Resources directory.
	 * @param fileName A String indicating the name of the file to use.
	 * @return If it was successful or not.
	 */
	public boolean build_constellationList(String fileName) {

		BufferedReader in = null;
		Constellations = new ArrayList<CTS_Constellation>();
		try {

			int edges, fromIdx, toIdx;
			String path = ".\\src\\Resources\\" + fileName;
			in = new BufferedReader(new FileReader(path));
			String line, name;
			String tokens[];
			CTS_Constellation constellation;

			while ( (line = in.readLine()) != null ) {

				tokens = line.split(" ");
				name = tokens[0];
				edges = Integer.valueOf(tokens[1]);
				constellation = new CTS_Constellation(name);

				if (tokens[2].equals("")) {
					fromIdx = 3;
					toIdx = 4;
				}
				else {
					fromIdx = 2;
					toIdx = 3;
				}


				for (int i = 1; i <= edges; i++) {
					int fromHip = Integer.valueOf(tokens[fromIdx]);
					int toHip = Integer.valueOf(tokens[toIdx]);
					constellation.addConnection(getStarByHip(fromHip), getStarByHip(toHip));
					fromIdx += 2;
					toIdx += 2;
				}

				Constellations.add(constellation);
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Builds the constellationDBs HashMap associating the name of a culture with
	 * the database containing their constellations by Hipparcus catalog number.
	 * WARNING: has no error checking of the ConstellationDB_List.csv file,
	 * and assumes it has no empty lines.
	 */
	public void build_constellationDBMap() {

		BufferedReader in = null;

		try {

			in = new BufferedReader(new FileReader(".\\src\\Resources\\ConstellationDB_List.csv"));
			String line;
			String tokens[];
			constellationDBs = new HashMap<>();

			// Skip the header line
			in.readLine();

			while ( (line = in.readLine()) != null) {

				tokens = line.split(",");
				constellationDBs.put(tokens[0], tokens[1]);
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds and returns a CTS_Star object by the hip field.
	 * @param hip An int indicating the star's Hipparcus catalog number
	 * @return a CTS_Star object with the matching hip number, if found, or null
	 */
	public CTS_Star getStarByHip(int hip) {

		for (CTS_Star star : starList) {

			if (star.hip == hip) {
				return star;
			}
		}

		return null;
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

	public ArrayList<CTS_Constellation> getConstellations() {
		return Constellations;
	}

	/**
	 * Getter to return the constellationDBs HashMap.
	 * @return A HashMap<String, String> containing the list of constellation
	 * databases by a string with the associated culture.
	 */
	public HashMap<String, String> getConstellationDBs() {
		return constellationDBs;
	}
	
	/**
	 * Returns the local siderial time.
	 * ASSUMES: daysSinceStand is set to the decimal days since J2000
	 * ASSUMES: universalTime is appropriately set
	 * @return a double representing the local siderial time for the star
	 */
	public double getLocalSiderialTime() {
		double lst = 100.46 + (0.985647 * daysSinceStandard) + longitude + (15 * universalTime);

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
	    //System.out.println("decTime = " + decTime);
	    // Formula from https://en.wikipedia.org/wiki/Julian_day#Julian_date_calculation
	    double jd = (1461 * (year + 4800 + (month - 14) / 12)) / 4 +
                    (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12 -
                    (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 +
                    day - 32075;
	    jd += decTime;
		//System.out.println("jd = " + jd);
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
}
