package Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The CTS_Constellation is a class that holds all the associated data with a constellation. 
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_Constellation {
	private String name;
	private HashMap<CTS_Star, ArrayList<CTS_Star>> connections;
	
	public CTS_Constellation(String nameIn) {
		this.name = nameIn;
		this.connections = new HashMap<CTS_Star, ArrayList<CTS_Star>>();
	}
	
	public void addConnection(CTS_Star from, CTS_Star to) {
		if (this.connections.containsKey(from)) {
			this.connections.get(from).add(to);
		} else {
			ArrayList<CTS_Star> newList = new ArrayList<CTS_Star>();
			newList.add(to);
			this.connections.put(from, newList);
		}
	}
	
	public HashMap<CTS_Star, ArrayList<CTS_Star>> getConnections(){
		return this.connections;
	}
	
}
