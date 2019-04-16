package Model;
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
	private double rightAscension = 0.0;
	private double declination = 0.0;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private String name = "Default Star Name";
	private double magnitude = 0.0;
	private double altitude = 0.0;
	private double azimuth = 0.0;
	private double universalTime = 0.0;
	// The days since J2000, including the decimal portion
	private double daysSinceStandard = 0.0;
	/**
	 * Inits a new star.
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(String name, double magnitude, double rightAcension, double declination) {
		super(name, rightAcension, declination);
		this.magnitude = magnitude;
	}
	/**
	 * Inits a new star.
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 * @param altitude The altitude of the star.
	 * @param azimuth The azimuth of the star.
	 */
	public CTS_Star(String name, double magnitude, double rightAcension, double declination, double altitude, double azimuth) {
		super(name, rightAcension, declination, altitude, azimuth);
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
		azimuth = Math.cos(altitude);
	}

	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_Star object.
	 */
	public String toString() {
		return "Star: [" + name + "," + magnitude + "," + rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}
}
