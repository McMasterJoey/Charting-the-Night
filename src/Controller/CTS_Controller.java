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
		
		// Update azimuth and altitude for all stars
		ArrayList<CTS_Star> starList = model.getStarList();		
		for (CTS_Star star : starList) {
			calcAzimuthAndAltitude(star);
			}
	}
	

	/**
	 * Calculates and sets the azimuth and altitude for a star.
	 * USES: getHourAngle, so transitively the assumptions in getLocalSiderialTime
	 * are in full affect for this method.
	 */
	public void calcAzimuthAndAltitude(CTS_Star star) {
		double declination = star.getDeclination();
		double latitude = model.getLatitude();
		double longitude = model.getLongitude();
		double ha = getHourAngle(star);
		
		// Calc altitude	
		double sinOfAlt = Math.sin(declination) * Math.sin(latitude) + Math.cos(declination) * Math.cos(latitude) * Math.cos(ha);
		double altitude = Math.asin(sinOfAlt);
		star.setAltitude(altitude);
		
		/*
		// Corrected azimuth calculation?
		double cosDec = Math.cos(declination);
		double sinHA = Math.sin(ha);
		double sinDec = Math.sin(declination);
		double cosLat = Math.cos(latitude);
		double cosHA = Math.cos(ha);
		double sinLat  = Math.sin(latitude);
		
		double azimuth = Math.atan(-(cosDec*sinHA)/(sinDec*cosLat - cosDec*cosHA*sinLat));
		*/
		
		// Calc azimuth
		double sinOfHA = Math.sin(ha);
		double cosOfA = (Math.sin(declination) - Math.sin(altitude) * Math.sin(latitude) / (Math.cos(altitude) * Math.cos(latitude)));
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
