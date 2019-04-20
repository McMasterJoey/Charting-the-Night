package View;

import java.util.ArrayList;
import Controller.CTS_Controller;
import Model.CTS_SpaceObject;
import Model.CTS_Star;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static java.lang.Math.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The GUI of the project.
 * Generates a window that the user can interact with.
 * @author Joey McMaster
 * @author Nicholas Fiegel
 * @author Matt Theisen
 * @author Jackson
 *
 */
public class CTS_GUI extends Application {
	public static final int VIEWING_AREA_WIDTH = 600;
	public static final int VIEWING_AREA_HEIGHT = 600;
	private GraphicsContext gc;
	private VBox uicontrols;
	private CTS_GUI_Dialoguebox input;
	private Canvas canvas;
	private Alert GUIerrorout;
	private CTS_Controller controller;
	public CTS_GUI(String[] args) {
		launch(args);
	}
	public CTS_GUI() { 
		
	}
	@Override
	public void start(Stage stage) throws Exception {
		controller = new CTS_Controller();
		stage.setTitle("Charting The Stars");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		BorderPane mainpane = new BorderPane();
		canvas = new Canvas(VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		mainpane.setCenter(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, Color.BLACK);
		// Setup Dialog box
        setUpDialoguebox();
        // Add a click handler so when the GUI is clicked, the settings menu comes up.
        mainpane.setOnMouseClicked((event) -> {
        	input.showAndWait();
        });
        		
		// Display it!
        
        privateTests();
        
		Scene scene = new Scene(mainpane, VIEWING_AREA_WIDTH , VIEWING_AREA_HEIGHT);
        stage.setScene(scene);
        stage.show();
	}
	/**
	 * Updates the GUI with the star chart of the inputed time and date.
	 */
	public void chartTheStars() {
		resetSkyDrawing();
		long[] data = getUserInputFromUIControls(); // ASSUMES IT IS VALID.
		controller = new CTS_Controller(data[0],data[1],0,0); // Only using latitude and longitude
		ArrayList<CTS_Star> n = controller.getModel().getStarList();
		long count = 0;
		for(int x = 0; x < n.size(); x++) {
			CTS_Star star = n.get(x);
			try {
				if (star.getAltitude() != 0 && star.getAzimuth() != 0 && star.getMagnitude() < 6) {
					drawSpaceObject(star,3,Color.WHITE);
					//System.out.println(star.getAltitude() + "," + star.getAzimuth());
					count++;
				}
			} catch(IllegalArgumentException e) {
				//System.err.println("An object that attempted to be drawn triggerd a execption");
			}
		}
		//System.out.println(count);
	}
	/**
	 * Draws a circle of the given radius, at the specific x,y location on the graphics context. 
	 * x and y are absolute, relative to the upper left side of the rectangular view. 
	 * @param radius The radius of the circle
	 * @param x The horizontal coordinate of the grid to put the circle center. 
	 * @param y The vertical coordinate of the grid to put the circle center.
	 * @param color The color of the circle.
	 * @throws IllegalArgumentException If the circle can not be fully displayed on the graphics context.
	 */
	public void drawCircle(int radius, int x, int y, Color color) {
		int xx = x - radius;
		int yy = y - radius;
		if (xx < 0 || yy < 0 || xx > (VIEWING_AREA_WIDTH -  radius)|| yy > (VIEWING_AREA_HEIGHT - radius)) {
			throw new IllegalArgumentException("drawCircle: invalid set of radius and x/y cordnates");
		}
		gc.setFill(color);
		gc.fillOval(xx,yy, radius * 2, radius * 2);
	}
	/**
	 * Draws a line of inputed thickness between 2 points.
	 * @param thickness The thickness of the line in pixels
	 * @param startx The starting x coordinate of the line.
	 * @param starty The starting y coordinate of the line.
	 * @param endx The ending x coordinate of the line.
	 * @param endy The ending y coordinate of the line.
	 * @param color The color of the line.
	 * @throws IllegalArgumentException If the line can not be fully displayed on the graphics context or if the thickness is less than 1.
	 */
	public void drawLine(int thickness, int startx, int starty, int endx, int endy, Color color) {
		if (thickness < 1) {
			throw new IllegalArgumentException("drawLine: thickness must be 1 or greater");
		}
		double offset = thickness / 2.0;
		//System.out.println(offset);
		double[] xcords = new double[4];
		double[] ycords = new double[4];
		boolean skiprest = false;
		if (startx - endx == 0) {
			// Case of verticle line.
			skiprest = true;
			xcords[0] = startx + offset;
			xcords[1] = startx - offset;
			xcords[2] = startx - offset;
			xcords[3] = startx + offset;
			ycords[0] = starty;
			ycords[1] = starty;
			ycords[2] = endy;
			ycords[3] = endy;
		}
		double changey = endy - starty;
		double changex = endx - startx;
		double slope = changey / changex;
		if (slope == 0.0 && !skiprest) {
			// Case of horitontal line
			skiprest = true;
			xcords[0] = startx;
			xcords[1] = startx;
			xcords[2] = endx;
			xcords[3] = endx;
			ycords[0] = starty + offset;
			ycords[1] = starty - offset;
			ycords[2] = starty - offset;
			ycords[3] = starty + offset;
			//System.out.println("Case 2!");
		}
		if (!skiprest) {
			double invslope = (1 / slope) * -1;
			ycords[0] = starty + (invslope * offset);
			ycords[1] = starty - (invslope * offset);
			ycords[2] = endy - (invslope * offset);
			ycords[3] = endy + (invslope * offset);
			
			xcords[0] = startx + offset;
			xcords[1] = startx - offset;
			xcords[2] = endx - offset;
			xcords[3] = endx + offset;
			
		}
		// Check for errors
		for(int x = 0; x < 4; x++) {
			if (ycords[x] < 0.0 || ycords[x] > VIEWING_AREA_HEIGHT) {
				throw new IllegalArgumentException("drawLine: Line goes off screen.");
			}
			if (xcords[x] < 0.0 || xcords[x] > VIEWING_AREA_WIDTH) {
				throw new IllegalArgumentException("drawLine: Line goes off screen.");
			}
		}
		gc.setFill(color);
		gc.fillPolygon(xcords, ycords, 4);
	}
	/**
	 * Wrapper function to draw space objects.
	 * @param obj The space object to be drawn.
	 * @param radius Its radius
	 * @param color Its color
	 * @throws IllegalArgumentException When something that is inputed causes an issue with displaying.
	 */
	public void drawSpaceObject(CTS_SpaceObject obj, int radius, Color color) {
		double[] result = getPositionOfSpaceObject(obj);
		if (result == null) {
			//System.err.println("getPostionOfSpaceObject returned null!");
			throw new IllegalArgumentException("Something went wrong");
		}
		drawCircle(radius, (int) result[0], (int) result[1], color);
	}
	/**
	 * Sets up the Dialoguebox that is used to take inputs
	 */
	private void setUpDialoguebox() {
		input = new CTS_GUI_Dialoguebox();
		input.setTitle("Settings");
		BorderPane pane = new BorderPane();
		LocalTime t = LocalTime.now();
		LocalDate d = LocalDate.now();
		
		uicontrols = new VBox(10);
		HBox box0 = new HBox(5);
		TextField lat = new TextField("0");
		box0.getChildren().add(new Label("Latitude: "));
		box0.getChildren().add(lat);
		
		TextField lon = new TextField("0");
		HBox box1 = new HBox(5);
		box1.getChildren().add(new Label("Longitude: "));
		box1.getChildren().add(lon);
		
		HBox box2 = new HBox(5);
		TextField date = new TextField(d.toString());
		box2.getChildren().add(new Label("Date: "));
		box2.getChildren().add(date);
		
		TextField time = new TextField(t.toString());
		HBox box3 = new HBox(5);
		box3.getChildren().add(new Label("Time: "));
		box3.getChildren().add(time);
		HBox box4 = new HBox(5);
		Button but = new Button("Cancel");
		but.setPadding(new Insets(5));
		Button but2 = new Button("Submit");
		but2.setPadding(new Insets(5));
		box4.getChildren().add(but);
		box4.getChildren().add(but2);
		uicontrols.getChildren().add(box0);
		uicontrols.getChildren().add(box1);
		uicontrols.getChildren().add(box2);
		uicontrols.getChildren().add(box3);
		uicontrols.getChildren().add(box4);
		
		but.setOnAction((event) -> {
        	input.close();
        });
		but2.setOnAction((event) -> {
			long i = validateInput();
			if (i == 0) {
				chartTheStars();
				input.close();
			} else {
				GUIerrorout = new Alert(AlertType.ERROR, getGUIErrorMsg(i));
				GUIerrorout.showAndWait();
			}
        });
		pane.setCenter(uicontrols);
		pane.setPadding(new Insets(10));
		Scene scene = new Scene(pane, 400, 180);
		input.setScene(scene);
	}
	private String getGUIErrorMsg(long errorcode) {
		if (errorcode == 0) {
			return "This popup shouldn't have launched!";
		} else if (errorcode == 9) {
			return "Latitude input format incorrect!\nValid Latitude values are -90 to 90 with negative values being south Latitude.";
		} else if (errorcode == 10) {
			return "Longitude input format incorrect!\nValid Longitude values are -180 to 180 with negative values being west latitude.";
		} else if (errorcode == 11) {
			return "Date input format incorrect!\nValid dates go <year>-<month>-<day>\nValid month values are 1- 12 and valid day values are 1 - 31.";
		} else if (errorcode == 12) {
			return "Time input format incorrect!\nValid times go <hour>-<miniute>-<second>\nValid hour values are 0 - 23 and valid miniute and second values are 0 - 59";
		} else if (errorcode == 1) {
			return "Invalid latitude!\nValid Latitude values are -90 to 90 with negative values being south Latitude.";
		} else if (errorcode == 2) {
			return "Invalid Longitude!\nValid Longitude values are -180 to 180 with negative values being west latitude.";
		} else if (errorcode == 3) {
			return "Invalid Year!\nValid year values are " + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ".";
		} else if (errorcode == 4) {
			return "Invalid Month!\nValid month values are 1 - 12";
		} else if (errorcode == 5) {
			return "Invalid Day!\nValid day values are 1 - 31";
		} else if (errorcode == 6) {
			return "Invalid Hour!\nValid hour values are 0 - 23";
		} else if (errorcode == 7) {
			return "Invalid Minute!\nValid minute values are 0 - 59";
		} else if (errorcode == 8) {
			return "Invalid Second!\nValid second values are 0 - 59";
		} 
		return "Undefined Error Message!";
	}
	/**
	 * Grabs info stored in the UI controls and translates to a series of longs.
	 * @return An array of length 9 with the first 2 values being latitude and longitude
	 * The 3,4 and 5 being Year,Month,Day
	 * The 6,7 and 8 being Hour,Minute,Second
	 * 9 being an error code: 0 = NO ERROR
	 * 9 = Bad latitude, 10 = Bad longitude, 11 = bad date, 12 = bad time
	 */
	private long[] getUserInputFromUIControls() {
		HBox n0 = (HBox) uicontrols.getChildren().get(0);
		HBox n1 = (HBox) uicontrols.getChildren().get(1);
		HBox n2 = (HBox) uicontrols.getChildren().get(2);
		HBox n3 = (HBox) uicontrols.getChildren().get(3);
		TextField t0 = (TextField) n0.getChildren().get(1);
		TextField t1 = (TextField) n1.getChildren().get(1);
		TextField t2 = (TextField) n2.getChildren().get(1);
		TextField t3 = (TextField) n3.getChildren().get(1);
		//System.out.println(t0.getText());
		//System.out.println(t1.getText());
		//System.out.println(t2.getText());
		//System.out.println(t3.getText());
		long[] retval = new long[9];
		retval[8] = 9;
		try {
			retval[0] = Long.parseLong(t0.getText());
			retval[8] = 10;
			retval[1] = Long.parseLong(t1.getText());
		} catch(Exception e) {
			return retval;
		}
		String[] date = t2.getText().split("-");
		if (date.length != 3) {
			retval[8] = 11;
			return retval;
		}
		try {
			retval[2] = Long.parseLong(date[0]);
			retval[3] = Long.parseLong(date[1]);
			retval[4] = Long.parseLong(date[2]);
		} catch(Exception e) {
			retval[8] = 11;
			return retval;
		}
		String[] time = t3.getText().split(":");
		if (time.length != 3) {
			retval[8] = 12;
			return retval;
		}
		try {
			retval[5] = Long.parseLong(time[0]);
			retval[6] = Long.parseLong(time[1]);
			retval[7] = (long) floor(Double.parseDouble(time[2]));
		} catch(Exception e) {
			retval[8] = 12;
			return retval;
		}
		retval[8] = 0;
		return retval;
	}
	private long validateInput() {
		long[] inputs = getUserInputFromUIControls();
		if (inputs == null) {
			return 9;
		}
		if (inputs[8] != 0) {
			return inputs[8];
		}
		if (inputs[0] > 90 || inputs[0] < -90) {
			return 1;
		}
		if (inputs[1] > 180 || inputs[1] < -180) {
			return 2;
		}
		// Year can be any number that can be fit within an integer.
		if (inputs[2] > Integer.MAX_VALUE || inputs[2] < Integer.MIN_VALUE) {
			return 3;
		}
		if (inputs[3] > 12 || inputs[3] < 1) {
			return 4;
		}
		if (inputs[4] > 31 || inputs[4] < 1) {
			return 5;
		}
		if (inputs[5] > 23 || inputs[5] < 0) {
			return 6;
		}
		if (inputs[6] > 59 || inputs[6] < 0) {
			return 7;
		}
		if (inputs[7] > 59 || inputs[7] < 0) {
			return 8;
		}
		return 0;
	}
	/**
	 * Determines where to plot space objects on the graph
	 * @param obj A space object with defined azimuth and alititude feilds.
	 * @return A set coordinates for the graph.
	 */
	private double[] getPositionOfSpaceObject(CTS_SpaceObject obj) {
		double azimuth = obj.getAzimuth();
		double altitude = obj.getAltitude();
		if (altitude < 0 || altitude > 90) {
			return null;
		}
		altitude = abs(altitude - 90);
		double rad = toRadians(altitude);
		double sin = sin(rad);
		double hypo = sin * (VIEWING_AREA_WIDTH / 2);
		double r = VIEWING_AREA_WIDTH / 2;
		
		double x_val, y_val, distanceFromCenter;
		distanceFromCenter = r*cos(rad);
		
		// x_val, y_val are coords relative to the center of the viewing area
		// being considered 0,0 on the Cartesian plane
		x_val = distanceFromCenter*(cos(90-azimuth));
		y_val = distanceFromCenter*(sin(90-azimuth));
		
		// view_x, view_y are the actual JavaFX coordinates to draw at
		double view_x = 0, view_y = 0;
		
		if (azimuth < 90) {
			view_x = x_val + 299;
			view_y = max(0,(299-y_val));
		} else if (azimuth < 180) {
			view_x = x_val + 299;
			view_y = min(599,(299-y_val));
		} else if (azimuth < 270) {
			view_x = max(0,(299+x_val));
			view_y = min(599,(299-y_val));
		} else {
			view_x = max(0,(299+x_val));
			view_y = max(0,(299-y_val));
		}
				
		double[] retval = {view_x, view_y};
		return retval;
		
		/*
		//System.out.println(hypo);
		// hypo * sin(degree) = x
		// rad = degree * PI / 180
		if (azimuth == 360.0) {
			azimuth = 0.0;
		}
		boolean postive = azimuth >= 90.0 && azimuth <= 270.0;
		boolean left = false;
		if (azimuth > 180.0) {
			azimuth -= 180.0;
			left = true;
		}
		azimuth -= 90;
		azimuth = Math.abs(azimuth);
		//System.out.println(azimuth);
		double changey = hypo * sin(azimuth * (PI / 180));
		double changex = sqrt((hypo * hypo) - (changey * changey));
		if (left) {
			changex *= -1;
		}
		if (!postive) {
			changey *= -1;
		}
		
		double[] retval = new double[2];
		retval[0] = changex + (VIEWING_AREA_WIDTH / 2);
		retval[1] = changey + (VIEWING_AREA_HEIGHT / 2);
		//System.out.println(changex + " " + changey);
		return retval;
		*/
	}
	/**
	 * Joeys speific testing function to test, inprogress
	 * Varrious draw functions. 
	 */
	private void privateTests() {
		// Test lines
		//drawLine(5,20,20,580,580,Color.RED);
		//drawLine(5,20,580,580,20,Color.RED);
		//drawLine(8,250,100,350,10,Color.BLUE);
		//drawLine(1,1,1,4,500,Color.GREEN);
		CTS_Star n1 = new CTS_Star(0, "Test", 0, 0, 0,90,50);
		
		CTS_Star n5 = new CTS_Star(0, "Test", 0, 0, 0,10,0);
		CTS_Star n = new CTS_Star(0, "Test", 0, 0, 0,10,90);
		CTS_Star n6 = new CTS_Star(0, "Test", 0, 0, 0,10,180);
		CTS_Star n2 = new CTS_Star(0, "Test", 0, 0, 0,10,270);
		CTS_Star n3 = new CTS_Star(0, "Test", 0, 0, 0,45,300);
		CTS_Star n4 = new CTS_Star(0, "Test", 0, 0, 0,45,10); //Incorrect
		
		// CTS_Star(int Id, String name, double magnitude, double rightAcension, double declination, 
		// double altitude, double azimuth)
		drawSpaceObject(n, 2, Color.RED);
		drawSpaceObject(n1,2,Color.YELLOW);
		drawSpaceObject(n2,2,Color.BLUE);
		drawSpaceObject(n3,10,Color.GREEN);
		drawSpaceObject(n4,20,Color.PINK);
		drawSpaceObject(n5,2,Color.ORANGE);
		drawSpaceObject(n6,2,Color.AQUA);
	}
	private void resetSkyDrawing() {
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, Color.BLACK);
	}
	/*
	 * Alt is distance from the edge of the circle. 90 deg - 0 (goes to - 90)
	 * az - 0 - 360, degrees 
	 */
}
