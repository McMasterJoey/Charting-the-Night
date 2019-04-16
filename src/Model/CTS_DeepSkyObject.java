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

	public CTS_DeepSkyObject(String name, double rightAcension, double declination) {
		super(0,name, declination, declination);
	}
	public CTS_DeepSkyObject(String name, double rightAcension, double declination, double altitude, double azimuth) {
		super(0,name, azimuth, azimuth, azimuth, azimuth);
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_DeepSkyObject object.
	 */
	public String toString() {
		return "Star: [" + name + "," + id + "," + rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}
}
