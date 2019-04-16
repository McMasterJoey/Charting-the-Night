package Model;
import java.util.ArrayList;

/**
 * The CTS_Star is a class that holds all the associated data with a star
 * is used by the program.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Star extends CTS_SpaceObject {
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double magnitude = 0.0;
	private double universalTime = 0.0;
	// The days since J2000, including the decimal portion
	private double daysSinceStandard = 0.0;
	// List of other stars in this constellation this star is "connected" to
	// in the visual drawing of a constellation
	private ArrayList<CTS_Star> linesTo;
	/**
	 * Inits a new star.
	 * @param Id The id number of the star from CSV database
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(int Id, String name, double magnitude, double rightAcension, double declination) {
		super(Id, name, rightAcension, declination);
		this.magnitude = magnitude;
	}
	
	
	/**
	 * Inits a new star.
	 * @param Id The id number of the star from CSV database
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 * @param altitude The altitude of the star.
	 * @param azimuth The azimuth of the star.
	 */
	public CTS_Star(int Id, String name, double magnitude, double rightAcension, double declination, double altitude, double azimuth) {
		super(Id, name, rightAcension, declination, altitude, azimuth);
		this.magnitude = magnitude;
	}
	/**
	 * Fetches the magnitude of the star.
	 * @return The magnitude of the star.
	 */
	public double getMagnitude() {
		return magnitude;
	}
    /**
     * Fetches the days from J2000.
     * @return a double indicating the days from J2000
     */
	public double getDaysSinceStandard() {
	    return daysSinceStandard;
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
	    // Formual from https://en.wikipedia.org/wiki/Julian_day#Julian_date_calculation
	    double jd = (1461 * (year + 4800 + (month - 14) / 12)) / 4 +
                    (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12 -
                    (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 +
                    day - 32075;
	    jd += decTime;
		System.out.println("jd = " + jd);
	    daysSinceStandard = jd - j2000;
    }

	/**
	 * Returns the local siderial time for the star.
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
	 * Calculates and returns the hour angle.
	 * USES: getLocalSiderialTime, so the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 * @return a double representing the hour angle
	 */
	public double getHourAngle() {
		double lst = getLocalSiderialTime();
		double ha = lst - rightAscension;

		while (ha < 0) {
			ha += 360;
		}

		while (ha > 360) {
			ha -= 360;
		}

		return lst - rightAscension;
	}

	/**
	 * Calculates and sets the altitude for the star.
	 * USES: getHourAngle, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAltitude() {
		double sinOfAlt = Math.sin(declination) * Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(getHourAngle());

		altitude = Math.asin(sinOfAlt);
	}

	/**
	 * Calculates and sets the azimuth for the star.
	 * USES: calcAltitude, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAzimuth() {
		calcAltitude();
		double sinOfHA = Math.sin(getHourAngle());
		double cosOfA = (Math.sin(declination) - Math.sin(altitude) * Math.sin(latitude)) / (Math.cos(altitude) * Math.cos(latitude));
		double A = Math.acos(cosOfA);

		if (sinOfHA < 0) {
			azimuth = A;
		}
		else {
			azimuth = 360 - A;
		}
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_Star object.
	 */
	public String toString() {
		return "Star: [" + name + "," + magnitude + "," + rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}
}
