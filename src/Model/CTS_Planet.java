package Model;

import java.lang.Math;

public class CTS_Planet extends CTS_SpaceObject{
	
	
	public CTS_Planet(double U, double W, double V, double L, String nameIn, int setID) {
		super (setID, nameIn, 0.0, 0.0);
		L = 2 * Math.PI * L;
		
		this.rightAscension = U - Math.pow(V, 2.0);
		this.rightAscension = Math.sqrt(this.rightAscension);
		this.rightAscension = W / this.rightAscension;
		this.rightAscension = 1/Math.sin(this.rightAscension);
		this.rightAscension = this.rightAscension + L;
		
		this.declination = V / Math.sqrt(U);
		this.declination = 1/Math.sin(this.declination);

		
		
	}
}
