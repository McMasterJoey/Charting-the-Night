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
	private double _rightAscension = 0.0;
	private double _declination = 0.0;
	private String _name = "Default Star Name";
	private double _magnitude = 0.0;
	private double _altitude = 0.0;
	private double _azimuth = 0.0;
	
	// Name of constellation this star belongs to
	private String _constellation = null;
	// List of other stars in this constellation this star is "connected" to
	// in the visual drawing of a constellation
	private ArrayList<CTS_Star> _linesTo;
	
	/**
	 * Inits a new star.
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(String name, double magnitude, double rightAcension, double declination) {
		super(name, rightAcension, declination);
		_magnitude = magnitude;
		_linesTo = new ArrayList<CTS_Star>();		
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
		_magnitude = magnitude;
	}
	/**
	 * Fetches the magnitude of the star.
	 * @return The magnitude of the star.
	 */
	public double getMagnitude() {
		return _magnitude;
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_Star object.
	 */
	public String toString() {
		return "Star: [" + _name + "," + _magnitude + "," + _rightAscension + "," + _declination + "," + _altitude + "," + _azimuth + "]";
	}
}
