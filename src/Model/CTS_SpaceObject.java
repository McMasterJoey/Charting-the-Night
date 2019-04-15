package Model;
/**
 * The CTS_SpaceObject is an abstract class that provides the needed boiler plate code 
 * that is common for all entities in space.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public abstract class CTS_SpaceObject {
	protected double _rightAscension = 0.0;
	protected double _declination = 0.0;
	protected double _altitude = 0.0;
	protected double _azimuth = 0.0;
	protected String _name = "Default Name";
	/**
	 * Inits a new space object.
	 * @param name The name of the space object.
	 * @param rightAcension The Right Acension of the space object.
	 * @param declination The Declination of the space object.
	 */
	public CTS_SpaceObject(String name, double rightAcension, double declination) {
		_name = name;
		_rightAscension = rightAcension;
		_declination = declination;
	}
	/**
	 * Inits a new space object.
	 * @param name The name of the space object.
	 * @param rightAcension The Right Acension of the space object.
	 * @param declination The Declination of the space object.
	 * @param altitude The altitude of the space object.
	 * @param azimuth The azimuth of the space object.
	 */
	public CTS_SpaceObject(String name, double rightAcension, double declination, double altitude, double azimuth) {
		_name = name;
		_rightAscension = rightAcension;
		_declination = declination;
		_altitude = altitude;
		_azimuth = azimuth;
	}
	/**
	 * Sets the Azimuth data point to the inputed value.
	 * @param azimuth The azimuth of the space object
	 */
	public void setAzimuth(double azimuth) {
		_azimuth = azimuth;
	}
	/**
	 * Sets the altitude data point to the inputed value.
	 * @param altitude The altitude of the space object
	 */
	public void setAltitude(double altitude) {
		_altitude = altitude;
	}
	/**
	 * Fetches the altitude of the space object.
	 * @return The altitude of the space object.
	 */
	public double getAltitude() {
		return _altitude;
	}
	/**
	 * Fetches the azimuth of the space object.
	 * @return The azimuth of the space object.
	 */
	public double getAzimuth() {
		return _azimuth;
	}
	/**
	 * Fetches the Right Ascension of the space object.
	 * @return The Right Ascension of the space object.
	 */
	public double getRightAscension() {
		return _rightAscension;
	}
	/**
	 * Fetches the declination of the space object.
	 * @return The declination of the space object.
	 */
	public double getDeclination() {
		return _declination;
	}
	/**
	 * Fetches the name of the space object.
	 * @return The name of the space object.
	 */
	public String getName() {
		return _name;
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the space object.
	 */
	public String toString() {
		return "SpaceObject: [" + _name + "," + _rightAscension + "," + _declination + "," + _altitude + "," + _azimuth + "]";
	}

}
