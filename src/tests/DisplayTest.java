package tests;

import common.Grid;
import common.IGrid;
import simulation.Earth;
import view.EarthDisplay;

public class DisplayTest {

	public static void main(String... args) {
		EarthDisplay d = new EarthDisplay();
		d.display(15, 1);
		
		int height = 180 / 15;
		int width = 360 / 15;
		IGrid grid = new Grid(0, 0, 0,Earth.EarthTimeStep, width, height,0,(float) Earth.SEMI_MAJOR_AXIS,0);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid.setTemperature(x, y, 1);
			}
		}
		
		d.update(grid);
		
	}
}
