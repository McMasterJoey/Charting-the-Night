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
	protected double rightAscension = 0.0;
	protected double declination = 0.0;
	protected double altitude = 0.0;
	protected double azimuth = 0.0;
	protected double magnitude = 0.0;
	protected String name = "Default Name";
	protected int id;
	
	/**
	 * Inits a new space object.
	 * @param id The id number of the SO from CSV database
	 * @param name The name of the space object.
	 * @param rightAcension The Right Acension of the space object.
	 * @param declination The Declination of the space object.
	 */
	public CTS_SpaceObject(int _id, String _name, double _rightAcension, double _declination) {
		id = _id;
		name = _name;
		rightAscension = _rightAcension;
		declination = _declination;
	}
	/**
	 * Inits a new space object.
	 * @param Id The id number of the SO from CSV database
	 * @param Name The name of the space object.
	 * @param rA The Right Acension of the space object.
	 * @param dec The Declination of the space object.
	 * @param alt The altitude of the space object.
	 * @param az The azimuth of the space object.
	 */
	public CTS_SpaceObject(int Id, String Name, double rA, double dec, double alt, double az) {
		id = Id;
		name = Name;
		rightAscension = rA;
		declination = dec;
		altitude = alt;
		azimuth = az;
	}
	/**
	 * Fetches the magnitude of the space object.
	 * @param The magnitude of the space object.
	 */
	public double getMagnitude(){
		return magnitude;
	}
	/**
	 * Sets the Azimuth data point to the inputed value.
	 * @param az The azimuth of the space object
	 */
	public void setAzimuth(double az) {
		azimuth = az;
	}
	/**
	 * Sets the altitude data point to the inputed value.
	 * @param alt The altitude of the space object
	 */
	public void setAltitude(double alt) {
		altitude = alt;
	}
	/**
	 * Fetches the altitude of the space object.
	 * @return The altitude of the space object.
	 */
	public double getAltitude() {
		return altitude;
	}
	/**
	 * Fetches the azimuth of the space object.
	 * @return The azimuth of the space object.
	 */
	public double getAzimuth() {
		return azimuth;
	}
	/**
	 * Fetches the Right Ascension of the space object.
	 * @return The Right Ascension of the space object.
	 */
	public double getRightAscension() {
		return rightAscension;
	}
	/**
	 * Fetches the declination of the space object.
	 * @return The declination of the space object.
	 */
	public double getDeclination() {
		return declination;
	}
	/**
	 * Fetches the name of the space object.
	 * @return The name of the space object.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Fetches the id of the space object.
	 * @return the id of the space object.
	 */
	public int getId() {
		return id;
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the space object.
	 */
	public String toString() {
		return "SpaceObject: [" + name + "," + id +","+ rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}

}
