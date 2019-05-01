package Model;
/**
 * The CTS_DeepSkyObject is an object that represents a collection of stars and other space objects deep in space.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_DeepSkyObject extends CTS_SpaceObject {
	/**
	 * The primary constructor of the deep space object.
	 * @param id The id of the deep space object.
	 * @param name The name of the deep space object.
	 * @param magnitude The magnitude of the deep space object.
	 * @param rightAcension The rightAcension of the deep space object.
	 * @param declination The declination of the deep space object.
	 */
	public CTS_DeepSkyObject(int id, String name, double magnitude, double rightAcension, double declination) {
		super(id ,name, rightAcension, declination);
		this.magnitude = magnitude;
	}
	/**
	 * The secondary constructor of the deep space object.
	 * @param id The id of the deep space object.
	 * @param name The name of the deep space object.
	 * @param magnitude The magnitude of the deep space object.
	 * @param rightAcension The rightAcension of the deep space object.
	 * @param declination The declination of the deep space object.
	 * @param altitude The altitude of the deep space object.
	 * @param azimuth The azimuth of the deep space object.
	 */
	public CTS_DeepSkyObject(int id, String name, double magnitude, double rightAcension, double declination, double altitude, double azimuth) {
		super(id ,name, rightAcension, declination, altitude, azimuth);
		this.magnitude = magnitude;
	}
	
	/**
	 * Fetches the magnitude of the DSO.
	 * @return The magnitude of the DSO.
	 */
	public double getMagnitude() {
		// This is a constant set directly from DeepSkyObjects.csv, setter not needed
		return magnitude;
	}
	
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_DeepSkyObject object.
	 */
	public String toString() {
		return "Star: [" + name + "," + id + "," + rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}
}
