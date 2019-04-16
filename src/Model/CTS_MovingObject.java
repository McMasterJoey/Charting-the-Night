package Model;
/**
 * The CTS_MovingObject is an object that represents things in space that move around
 * such as planets, the moon and the sun. 
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_MovingObject extends CTS_SpaceObject {

	public CTS_MovingObject(int id, String name, double rightAcension, double declination) {
		super(id, name, declination, declination);
	}
	public CTS_MovingObject(int id, String name, double rightAcension, double declination, double altitude, double azimuth) {
		super(id, name, azimuth, azimuth, azimuth, azimuth);
	}
	/**
	 * Makes the object printable easily.
	 * @return The string representation of the CTS_MovingObject object.
	 */
	public String toString() {
		return "MovingObject: [" + name + "," + id + "," + rightAscension + "," + declination + "," + altitude + "," + azimuth + "]";
	}
}