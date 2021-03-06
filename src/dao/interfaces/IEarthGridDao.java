package dao.interfaces;

import java.io.IOException;
import java.sql.SQLException;

import dao.EarthGridInsert;
import dao.EarthGridQuery;
import dao.EarthGridResponse;
import dao.ResponseType;

public interface IEarthGridDao {
	
	public EarthGridResponse queryEarthGridSimulation(EarthGridQuery query) 
			throws SQLException, NumberFormatException, ClassNotFoundException, IOException;
	
	public EarthGridResponse queryEarthGridSimulationByName(String name) 
			throws SQLException, IOException, ClassNotFoundException;
	
	public ResponseType insertEarthGridSimulation(EarthGridInsert egq) 
			throws SQLException, NumberFormatException, IOException;
	
	public int getSimulationIdFromName(String name) 
			throws SQLException;
		
	public boolean isNameUnique(String name) 
			throws SQLException;
	
	public String[] getAllNames() 
			throws SQLException;
	
	public void resetDatabase(String secretCode) 
			throws SQLException;
}
