package Controller;

import Model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Place holder controller for the project
 * Please edit once we do something meaningful with it.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Controller {

	CTS_Model model;
	
	
	
	public CTS_Controller() {
		model = new CTS_Model();
		
	}
	
	

	/**
	 * Calculates and sets the altitude for a star.
	 * USES: getHourAngle, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAltitude(CTS_Star star) {
		double sinOfAlt = Math.sin(star.getDeclination()) * Math.sin(model.getLatitude()) + Math.cos(star.getDeclination()) * Math.cos(model.getLatitude()) * Math.cos(getHourAngle(star));

		star.setAltitude(Math.asin(sinOfAlt));
	}


	/**
	 * Calculates and sets the azimuth for a star.
	 * USES: calcAltitude, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAzimuth(CTS_Star star) {
		calcAltitude(star);
		double sinOfHA = Math.sin(getHourAngle(star));
		double cosOfA = (Math.sin(star.getDeclination()) - Math.sin(star.getAltitude()) * Math.sin(model.getLatitude())) / (Math.cos(star.getAltitude()) * Math.cos(model.getLatitude()));
		double A = Math.acos(cosOfA);

		if (sinOfHA < 0) {
			star.setAzimuth(A);
		}
		else {
			star.setAzimuth(360 - A);
		}
	}
	
	
	
	/**
	 * Calculates and returns the hour angle of a star.
	 * USES: getLocalSiderialTime, so the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 * @return a double representing the hour angle
	 */
	public double getHourAngle(CTS_Star star) {
		double lst = model.getLocalSiderialTime();
		double ha = lst - star.getRightAscension();

		while (ha < 0) {
			ha += 360;
		}

		while (ha > 360) {
			ha -= 360;
		}

		return ha;
	}


	
}
