package Model;

import java.lang.Math;

public class CTS_Planet extends CTS_SpaceObject{
	
	
	public CTS_Planet(String nameIn, int setID, double M, double e, double a) {
		super (setID, nameIn, 0.0, 0.0);
		
		M = Math.toRadians(M);
		double E = M + e * Math.sin(M) * (1.0 + e * Math.cos(M));
		//Anomaly convergence 
		if (e > 0.05) {
			double E0 = E;
			double E1 = E0 - (E0 - e * Math.sin(E0) - M) / (1 -e * Math.cos(E0));
			while (Math.abs(E0 - E1) > 0.06) {
				E0 = E1;
				E1 = E0 - (E0 - e * Math.sin(E0) - M) / (1 -e * Math.cos(E0));
			}
			E = E0;
		}
		
		double xv = a * (Math.cos(E) - e);
		double yv = a * (Math.sqrt(1.0 - e*e) * Math.sin(E));
		
		double v = Math.atan2(yv, xv);
		double r = Math.sqrt(xv*xv + yv*yv);
		
		
		
	}
}
