package Model;
/**
 * The CTS_Star is a class that holds all the assoiated data with a star
 * is used by the program.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Star {
	private double _magnitude = 0.0;
	private double _rightAscension = 0.0;
	private double _declination = 0.0;
	private double _altitude = 0.0;
	private double _azimuth = 0.0;
	private String _name = "Default Star Name";
	/**
	 * Inits a new star.
	 * @param name The name of the star.
	 * @param magnitude The Magnitude of the star.
	 * @param rightAcension The Right Acension of the star.
	 * @param declination The Declination of the star.
	 */
	public CTS_Star(String name, double magnitude, double rightAcension, double declination) {
		_name = name;
		_magnitude = magnitude;
		_rightAscension = rightAcension;
		_declination = declination;
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
		_name = name;
		_magnitude = magnitude;
		_rightAscension = rightAcension;
		_declination = declination;
		_altitude = altitude;
		_azimuth = azimuth;
	}
	/**
	 * Sets the Azimuth data point to the inputed value.
	 * @param azimuth The azimuth of the star
	 */
	public void setAzimuth(double azimuth) {
		_azimuth = azimuth;
	}
	/**
	 * Sets the altitude data point to the inputed value.
	 * @param altitude The altitude of the star
	 */
	public void setAltitude(double altitude) {
		_altitude = altitude;
	}
	/**
	 * Fetches the altitude of the star.
	 * @return The altitude of the star.
	 */
	public double getAltitude() {
		return _altitude;
	}
	/**
	 * Fetches the azimuth of the star.
	 * @return The azimuth of the star.
	 */
	public double getAzimuth() {
		return _azimuth;
	}
	/**
	 * Fetches the Right Ascension of the star.
	 * @return The Right Ascension of the star.
	 */
	public double getRightAscension() {
		return _rightAscension;
	}
	/**
	 * Fetches the magnitude of the star.
	 * @return The magnitude of the star.
	 */
	public double getMagnitude() {
		return _magnitude;
	}
	/**
	 * Fetches the declination of the star.
	 * @return The declination of the star.
	 */
	public double getDeclination() {
		return _declination;
	}
	/**
	 * Fetches the name of the star.
	 * @return The name of the star.
	 */
	public String getName() {
		return _name;
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_Star object.
	 */
	public String toString() {
		return "[" + _name + "," + _magnitude + "," + _rightAscension + "," + _declination + "," + _altitude + "," + _azimuth + "]";
	}
}
