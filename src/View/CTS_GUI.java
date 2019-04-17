package View;

import Controller.CTS_Controller;
import Model.CTS_SpaceObject;
import Model.CTS_Star;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	private HBox uicontrols;
	CTS_Controller controller;
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
		Canvas canvas = new Canvas(VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		mainpane.setCenter(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0,  VIEWING_AREA_WIDTH, VIEWING_AREA_HEIGHT);
		drawCircle(VIEWING_AREA_WIDTH / 2, VIEWING_AREA_WIDTH / 2, VIEWING_AREA_HEIGHT / 2, Color.BLACK);
		// Set up Buttons
		uicontrols = new HBox();
		TextField timetext = new TextField("0:00");
		TextField location = new TextField("Tuscon Arizona");
		Button b = new Button("Update");
		b.setOnAction((event) -> {
			chartTheStars();
        });
		uicontrols.getChildren().add(new Label("Time"));
		uicontrols.getChildren().add(timetext);
		uicontrols.getChildren().add(new Label("Location"));
		uicontrols.getChildren().add(location);
		uicontrols.getChildren().add(b);
        mainpane.setTop(uicontrols);
		// Display it!
        
        privateTests();
        
		Scene scene = new Scene(mainpane, VIEWING_AREA_WIDTH , VIEWING_AREA_HEIGHT + 30);
        stage.setScene(scene);
        stage.show();
	}
	/**
	 * Updates the GUI with the star chart of the inputed time and date.
	 */
	public void chartTheStars() {
		TextField time = (TextField) uicontrols.getChildren().get(1);
		TextField location = (TextField) uicontrols.getChildren().get(3);
		System.out.println(time.getText());
		System.out.println(location.getText());
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
	 */
	public void drawSpaceObject(CTS_SpaceObject obj, int radius, Color color) {
		double[] result = getPositionOfSpaceObject(obj);
		drawCircle(radius, (int) result[0], (int) result[1], color);
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
		altitude = Math.abs(altitude - 90);
		double rad = (altitude / 90) * (Math.PI / 2);
		double sin = Math.sin(rad);
		double hypo = sin * (VIEWING_AREA_WIDTH / 2);
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
		double changey = hypo * Math.sin(azimuth * (Math.PI / 180));
		double changex = Math.sqrt((hypo * hypo) - (changey * changey));
		if (left) {
			changex *= -1;
		}
		if (postive) {
			changey *= -1;
		}
		
		double[] retval = new double[2];
		retval[0] = changex + (VIEWING_AREA_WIDTH / 2);
		retval[1] = changey + (VIEWING_AREA_HEIGHT / 2);
		//System.out.println(changex + " " + changey);
		return retval;
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
		CTS_Star n = new CTS_Star(0, "Test", 0, 0, 0,45,90);
		CTS_Star n1 = new CTS_Star(0, "Test", 0, 0, 0,90,50);
		CTS_Star n2 = new CTS_Star(0, "Test", 0, 0, 0,10,270);
		CTS_Star n3 = new CTS_Star(0, "Test", 0, 0, 0,45,300);
		
		// CTS_Star(int Id, String name, double magnitude, double rightAcension, double declination, 
		// double altitude, double azimuth)
		drawSpaceObject(n, 5, Color.RED);
		drawSpaceObject(n1,5,Color.YELLOW);
		drawSpaceObject(n2,2,Color.BLUE);
		drawSpaceObject(n3,10,Color.GREEN);
	}
	/*
	 * Alt is distance from the edge of the circle. 90 deg - 0 (goes to - 90)
	 * az - 0 - 360, degrees 
	 */
}
