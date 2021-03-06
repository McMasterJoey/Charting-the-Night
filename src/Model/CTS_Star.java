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
	// List of other stars in this constellation this star is "connected" to
	// in the visual drawing of a constellation
	private ArrayList<CTS_Star> linesTo;
	/**
	 * Inits a new star.
	 * @param Id The id number of the star from CSV database
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAscension The Right Ascension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(int Id, String name, double magnitude, double rightAscension, double declination) {
		super(Id, name, rightAscension, declination);
		this.magnitude = magnitude;
	}
	/**
	 * Inits a new star.
	 * @param Id The id number of the star from CSV database.
	 * @param hip The Hipparcos catalog id of the star.
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAscension The Right Ascension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(int Id, int hip, String name, double magnitude, double rightAscension, double declination) {
		super(Id, hip, name, rightAscension, declination);
		this.magnitude = magnitude;
	}
	/**
	 * Inits a new star.
	 * @param Id The id number of the star from CSV database
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAscension The Right Ascension of the star.
	 * @param declination The Declination of the star.
	 * @param altitude The altitude of the star.
	 * @param azimuth The azimuth of the star.
	 */
	public CTS_Star(int Id, String name, double magnitude, double rightAscension, double declination, double altitude
			, double azimuth) {
		super(Id, name, rightAscension, declination, altitude, azimuth);
		this.magnitude = magnitude;
	}
	/**
	 * Fetches the magnitude of the star.
	 * @return The magnitude of the star.
	 */
	public double getMagnitude() {
		// This is a constant set directly from Stars.csv, setter not needed
		return magnitude;
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_Star object.
	 */
	public String toString() {
		return "Star: [NAME: " + name + ", MAG: " + magnitude + ", RA: " + rightAscension + ", DEC: " + declination + ", ALT: " + altitude + ", AZI: " + azimuth + "]";
	}
}
