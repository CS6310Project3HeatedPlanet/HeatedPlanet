package tests;

import static org.junit.Assert.*;
import interpolator.GridInterprolator;

import org.junit.Before;
import org.junit.Test;

import common.Grid;
import simulation.Earth;

public class GridIterprolatorTest {
	
	private GridInterprolator GI;
	private Grid grid1;
	private Grid grid2;
	

	@Before
	public void setUp() throws Exception {
		GI = new GridInterprolator();
		grid1 = new Grid(0,0,0,10,5,0,(float)Earth.SEMI_MAJOR_AXIS,0);
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 10; j++){
				if(i < 2 && j > 5)
					grid1.setTemperature(j,i,100);
				else if(i >= 2 && j <= 5)
					grid1.setTemperature(j,i,200);
				else
					grid1.setTemperature(j,i,300);
			}
		}
		
		grid2 = new Grid(0,0,0,4,2,0,(float)Earth.SEMI_MAJOR_AXIS,0);
		grid2.setTemperature(0, 0, 100);
		grid2.setTemperature(1, 0, 100);
		grid2.setTemperature(2, 0, 200);
		grid2.setTemperature(3, 0, 200);
		grid2.setTemperature(0, 1, 200);
		grid2.setTemperature(1, 1, 200);
		grid2.setTemperature(2, 1, 300);
		grid2.setTemperature(3, 1, 300);
	}

	
	@Test
	public void test2() {
		Grid newGrid = GI.interpolateSpace(grid2, 40);
		assertEquals(newGrid.getTemperature(0,0),100,0.1);
		assertEquals(newGrid.getTemperature(9,0),200,0.1);
		assertEquals(newGrid.getTemperature(0,4),200,0.1);
		assertEquals(newGrid.getTemperature(9,4),300,0.1);
		assertEquals(newGrid.getTemperature(0,2),150,0.1);
		assertEquals(newGrid.getTemperature(9,2),250,0.1);
		assertTrue(newGrid.getGridWidth() == 10);
		assertTrue(newGrid.getGridHeight() == 5);
	}
	
	@Test
	public void test1() {
		printGrid(grid1);
		Grid newGrid = GI.decimateSpace(grid1, 50);
		printGrid(newGrid);
	
		assertEquals(newGrid.getTemperature(0,0),(2*300.0+0.5*200.0)/2.5,0.1);
		assertEquals(newGrid.getTemperature(3,0),(2*100+0.5*300)/2.5,0.1);
		assertEquals(newGrid.getTemperature(0,1),200,0.1);
		assertEquals(newGrid.getTemperature(3,1),300,0.1);
		assertTrue(newGrid.getGridWidth() == 4);
		assertTrue(newGrid.getGridHeight() == 2);
	}
	
	public void printGrid(Grid printGrid){
		for(int i = 0; i < printGrid.getGridHeight(); i++){
			for(int j =0; j< printGrid.getGridWidth(); j++){
				System.out.print(printGrid.getTemperature(j, i)+", ");
			}
			System.out.println("");
		}
	}

}
