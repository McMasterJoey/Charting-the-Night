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
	ArrayList<CTS_Planet> Planets;

	HashMap<String, String> constellationDBs;
	
	// The days since J2000, including the decimal portion
	private double daysSinceStandard;
	private double universalTime;
	private double planetTime;
	
	private double latitude;
	private double longitude;

	/**
	 * Basic constructor for a model. Defaults to the Western constellations, at
	 * latitude/longitude 0, at 12:00 AM on Jan. 1, 2000.
	 */
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
		
		Planets = new ArrayList<CTS_Planet>();
		build_PlanetList();
		
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
	 * @param daysSinceStandard Days since the year 2000
	 * @param universaltime  Time of day of where the observer will be.
	 */
	public CTS_Model(double latitude, double longitude, double daysSinceStandard, double universaltime) {
		// Generate list of star objects
		starList = new ArrayList<CTS_Star>();
		Constellations = new ArrayList<CTS_Constellation>();
		
		build_starList();
		build_constellationDBMap();
		build_constellationList("western.fab");
		
		// Generate list of deep sky objects
		DSOlist = new ArrayList<CTS_DeepSkyObject>();
		build_DSOlist();
		
		Planets = new ArrayList<CTS_Planet>();
		build_PlanetList();
		
		// Set params with custom values.
		this.daysSinceStandard = daysSinceStandard;
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
	
	public ArrayList<CTS_Planet> getPlanetList(){
		return this.Planets;
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
		}
	    
	    try {
            fileReader.close();
        } catch (Exception e) {
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
		}
	    
	    try {
            fileReader.close();
        } catch (Exception e) {
        }
	}

	/**
	 * Builds the list of CTS_Constellation objects, given a file name.
	 * The file should be in the Stellarium fab format.
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

				tokens = line.split("\\s+");
				name = tokens[0];
				edges = Integer.valueOf(tokens[1]);
				constellation = new CTS_Constellation(name);

				// At least one file lists a constellation with no edges
				fromIdx = 2;
				toIdx = 3;

				// Process the edges for the constellation
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
	 * Builds the planet list to be used by the program.
	 */
	public void build_PlanetList() {
		double M, e, a, N, w, i, ecl;
		
		ecl = 23.4393 - (3.563 * planetTime * Math.pow(10, -7.0));
		//Mercury
		M = 168.6562 + 4.0923344368 * planetTime;
		e = 0.205635 + (5.59 * planetTime * Math.pow(10.0, -10.0));
		a = 0.387098;
		N = 48.3313 + (3.24587 * planetTime * Math.pow(10.0, -5.0));
		w = 29.1241 + (1.01444 * planetTime * Math.pow(10.0, -5.0));
		i = 7.0047 + (5 * planetTime * Math.pow(10.0, -8.0));
		CTS_Planet mercury = new CTS_Planet("Mercury", 200000, M, e, a, N, w, i, ecl);
		this.Planets.add(mercury);
		
		//Venus
		M = 48.0052 + 1.6021302244 * planetTime;
		e = 0.006773 - (1.302 * planetTime * Math.pow(10.0, -9.0));
		a = 0.723330;
		N = 76.6799 + (2.46590 * planetTime * Math.pow(10.0, -5.0));
		w = 54.8910 + (1.38374 * planetTime * Math.pow(10.0, -5.0));
		i = 3.3946 + (2.75 * planetTime * Math.pow(10.0, -8.0));
		CTS_Planet venus = new CTS_Planet("Venus", 200001, M, e, a, N, w, i, ecl);
		this.Planets.add(venus);
		
		//Mars
		M = 18.6021 + 0.5240207766 * planetTime;
		e = 0.093405 + (2.516 * planetTime * Math.pow(10.0, -9.0));
		a = 1.523688;
		N = 49.5574 + (2.11081 * planetTime * Math.pow(10.0, -5.0));
		w = 286.5016 + (2.92961 * planetTime * Math.pow(10.0, -5.0));
		i = 1.8497 - (1.78 * planetTime * Math.pow(10.0, -8.0));
		CTS_Planet mars = new CTS_Planet("Mars", 200002, M, e, a, N, w, i, ecl);
		this.Planets.add(mars);
		
		//Jupiter
		M = 19.8950 + 0.0830853001 * planetTime;
		e = 0.048498 + (4.469 * planetTime * Math.pow(10.0, -9.0));
		a = 5.20256;
		N = 100.4542 + (2.76854 * planetTime * Math.pow(10.0, -5.0));
		w = 273.8777 + (1.64505 * planetTime * Math.pow(10.0, -5.0));
		i = 1.3030 - (1.557 * planetTime * Math.pow(10.0, -7.0));
		CTS_Planet jupiter = new CTS_Planet("Jupiter", 200003, M, e, a, N, w, i, ecl);
		this.Planets.add(jupiter);
		
		//Saturn
		M = 316.9670 + 0.0334442282 * planetTime;
		e = 0.055546 - (9.499 * planetTime * Math.pow(10.0, -9.0));
		a = 9.55475;
		N = 113.6634 + (2.38980 * planetTime * Math.pow(10.0, -5.0));
		w = 339.3939 + (2.97661 * planetTime * Math.pow(10.0, -5.0));
		i = 2.4886 - (1.081 * planetTime * Math.pow(10.0, -7.0));
		CTS_Planet Saturn = new CTS_Planet("Saturn", 200004, M, e, a, N, w, i, ecl);
		this.Planets.add(Saturn);
		
		//Uranus
		M = 142.5905 + 0.011725806 * planetTime;
		e = 0.047318 + (7.45 * planetTime * Math.pow(10.0, -9.0));
		a = 19.18171 - (1.55 * planetTime * Math.pow(10.0, -8.0));
		N = 74.0005 + (1.3978 * planetTime * Math.pow(10.0, -5.0));
		w = 96.6612 + (3.0565 * planetTime * Math.pow(10.0, -5.0));
		i = 0.7733 + (1.9 * planetTime * Math.pow(10.0, -8.0));
		CTS_Planet Uranus = new CTS_Planet("Uranus", 200005, M, e, a, N, w, i, ecl);
		this.Planets.add(Uranus);
		
		//Neptune
		M = 260.2471 + 0.005995147 * planetTime;
		e = 0.008606 + (2.15 * planetTime * Math.pow(10.0, -9.0));
		a = 30.05826 + (3.313 * planetTime * Math.pow(10.0, -8.0));
		N = 131.7806 + (3.0173 * planetTime * Math.pow(10.0, -5.0));
		w = 272.8461 - (6.027 * planetTime * Math.pow(10.0, -6.0));
		i = 1.7700  - (2.55 * planetTime * Math.pow(10.0, -7.0));
		CTS_Planet Neptune = new CTS_Planet("Neptune", 200006, M, e, a, N, w, i, ecl);
		this.Planets.add(Neptune);
		
		//Moon
		M = 115.3654 + 13.0649929509 * planetTime;
		e = 0.054900;
		a = 60.2666;
		N = 125.1228 - 0.0529538083 * planetTime;
		w = 318.0634 + 0.1643573223 * planetTime;
		i = 5.1454;
		CTS_Planet Moon = new CTS_Planet("Moon", 200007, M, e, a, N, w, i, ecl);
		this.Planets.add(Moon);
	}
	
	public double helperIgnoreme (double In) {
		if (In > 0) {
			return In - Math.floor(In);
		} else {
			return In - Math.ceil(In);
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

	/**
	 * Setter for latitude.
	 * @param lat A double representing the latitude in degrees.
	 */
	public void setLatitude(double lat) {
		latitude = lat;
	}

	/**
	 * Setter for longitude
	 * @param lon A double representing the longitude in degrees.
	 */
	public void setLongitude(double lon) {
		longitude = lon;
	}

	/**
	 * Getter for latitude.
	 * @return A double representing the latitude in degrees.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Getter for longitude.
	 * @return A double representing the longitude in degrees.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Getter for Constellations
	 * @return  containing the current constellation objects.
	 */
	public ArrayList<CTS_Constellation> getConstellations() {
		return Constellations;
	}

	/**
	 * Getter to return the constellationDBs HashMap.
	 * @return  containing the list of constellation
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
	    double decTime = ((3600 * hour) + (60 * minutes) + seconds);
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
	    double minFrac = Double.valueOf((double) minutes / 60);
	    double secFrac = Double.valueOf((double) seconds / 3600);
	    universalTime = hour + minFrac + secFrac;
      planetTime = 367*year - 7 * ( year + (month+9)/12 ) / 4 - 3 * ( ( year + (month-9)/7 ) / 100 + 1 ) / 4 + 275*month/9 +
	    		day - 730515;
	    planetTime += (hour + (double)minutes/60 + (double)seconds/3600)/24.0;
    }
	
    /**
     * Fetches the days from J2000.
     * @return a double indicating the days from J2000
     */
	public double getDaysSinceStandard() {
	    return daysSinceStandard;
    }
}
