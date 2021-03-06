package test.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import common.EarthGridProperties;
import common.Grid;
import common.EarthGridProperties.EarthGridProperty;
import common.IGrid;
import dao.EarthGridDao;
import dao.EarthGridInsert;
import dao.EarthGridQuery;
import dao.EarthGridResponse;
import dao.ResponseType;

/**
 * Created by David Welker on 11/13/14.
 */
public class DatabaseTest
{
    public static void main(String[] args) throws Exception
    {
    	 Calendar cal1 = getBaseCalendar();
         Calendar cal2 = getBaseCalendar();
         Calendar cal3 = getBaseCalendar();
         Calendar cal4 = getBaseCalendar();
         
         cal2.add(Calendar.HOUR_OF_DAY, 1);
         cal3.add(Calendar.HOUR_OF_DAY, 2);
         cal4.add(Calendar.HOUR_OF_DAY, 3);
         
         System.out.println("#Print Calendar Values");
         System.out.println("Cal1 = "+printCalendar(cal1));
         System.out.println("Cal2 = "+printCalendar(cal2));
         System.out.println("Cal3 = "+printCalendar(cal3));
         System.out.println("Cal4 = "+printCalendar(cal4));
         System.out.println("----------------------------");
    	
        EarthGridDao dao = EarthGridDao.getEarthGridDao();
                
        EarthGridProperties EGProps = new EarthGridProperties();
        EGProps.setProperty(EarthGridProperty.NAME, "InsertedName1");
        EGProps.setProperty(EarthGridProperty.AXIAL_TILT, 1.0);
        EGProps.setProperty(EarthGridProperty.ECCENTRICITY, 0.99);
        EGProps.setProperty(EarthGridProperty.GRID_SPACING, 1);
        EGProps.setProperty(EarthGridProperty.SIMULATION_TIME_STEP, 1);
        EGProps.setProperty(EarthGridProperty.SIMULATION_LENGTH, 1);
        EGProps.setProperty(EarthGridProperty.PRECISION, 1);
        EGProps.setProperty(EarthGridProperty.GEO_PRECISION, 1);
        EGProps.setProperty(EarthGridProperty.TIME_PRECISION, 1);
        EGProps.setProperty(EarthGridProperty.END_DATE, cal1);
        
        
        IGrid grid1 = new Grid(1, 1, (int) cal1.getTimeInMillis(), 1, 1, 1, 1, 1, 1);
        IGrid grid2 = new Grid(2, 2, (int) cal2.getTimeInMillis(), 2, 2, 2, 2, 2, 2);
        IGrid grid3 = new Grid(3, 3, (int) cal3.getTimeInMillis(), 3, 3, 3, 3, 3, 3);
        IGrid grid4 = new Grid(4, 4, (int) cal4.getTimeInMillis(), 4, 4, 4, 4, 4, 4);
        
        
        IGrid[] gridArray1 = {grid1};
        IGrid[] gridArray2 = {grid2,grid3};
        IGrid[] gridArray3 = {grid1,grid2,grid3,grid4};
        
        Calendar[] calArray1 = {cal1};
        Calendar[] calArray2 = {cal2,cal3};
        Calendar[] calArray3 = {cal1,cal2,cal3,cal4};
        
        try{
        	System.out.println("#Insert First Record");
            EarthGridInsert insert1 = new EarthGridInsert(EGProps, gridArray1, calArray1);
        	ResponseType response1 = dao.insertEarthGridSimulation(insert1);
        	System.out.println(response1.name());
        	System.out.println("----------------------------");
        	
        	System.out.println("#Insert Second Record");
            EGProps.setProperty(EarthGridProperty.NAME, "InsertedName2");
            EGProps.setProperty(EarthGridProperty.END_DATE, cal3);
            EarthGridInsert insert2 = new EarthGridInsert(EGProps, gridArray2, calArray2);
            ResponseType response2 = dao.insertEarthGridSimulation(insert2);
            System.out.println(response2.name());
            System.out.println("----------------------------");
        	
            System.out.println("#Insert Third Record");
            EGProps.setProperty(EarthGridProperty.NAME, "InsertedName3");
            EGProps.setProperty(EarthGridProperty.END_DATE, cal4);
            EarthGridInsert insert3 = new EarthGridInsert(EGProps, gridArray3, calArray3);
            ResponseType response3 = dao.insertEarthGridSimulation(insert3);
            System.out.println(response3.name());
            System.out.println("----------------------------");
        	
            System.out.println("#Test simple queries");
            System.out.println(dao.isNameUnique("InsertedName2"));
	        System.out.println(dao.isNameUnique("name"));
	        System.out.println(dao.getAllNames().length);
	        String[] names = dao.getAllNames();
	        for (int i=0; i < names.length; i++) {
	        	System.out.println("Name = "+names[i]);
	        	System.out.println("ID = "+dao.getSimulationIdFromName(names[i]));
	        }
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for 'InsertedName3' by name");
	         EarthGridResponse response4 = dao.queryEarthGridSimulationByName("InsertedName3");
	        System.out.println(response4.getResult().name());
	        IGrid[] gridresponse4 = response4.getAllGrids();
	        for(int i = 0; i< gridresponse4.length; i++)
	        	System.out.println(gridresponse4[i].getSunPosition());
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for 'InsertedName2' using EarthGridQuery");
	        EarthGridProperties egpQuery2 = new EarthGridProperties();
	        egpQuery2.setProperty(EarthGridProperty.NAME, "InsertedName2");
	        EarthGridQuery egq2 = new EarthGridQuery(egpQuery2);
	        EarthGridResponse response5 = dao.queryEarthGridSimulation(egq2);
	        
	        System.out.println(response5.getResult().name());
	        IGrid[] gridresponse5 = response5.getAllGrids();
	        for(int i = 0; i< gridresponse5.length; i++)
	        	System.out.println(gridresponse5[i].getSunPosition());
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for Axial Tilt = 1.0");
	        EarthGridProperties egpQuery6 = new EarthGridProperties();
	        egpQuery6.setProperty(EarthGridProperty.AXIAL_TILT,1.0);
	        EarthGridQuery egq6 = new EarthGridQuery(egpQuery6);
	        EarthGridResponse response6 = dao.queryEarthGridSimulation(egq6);
	        System.out.println(response6.getResult().name());
	        IGrid[] gridresponse6 = response6.getAllGrids();
	        Calendar[] calresponse6 = response6.getAllGridDates();
	        for(int i = 0; i< gridresponse6.length; i++)
	        	System.out.println(gridresponse6[i].getSunPosition()+"@"+calresponse6[i].getTime().toString());
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for StartDate >= Cal3");
	        EarthGridProperties egpQuery7 = new EarthGridProperties();
	        egpQuery7.setProperty(EarthGridProperty.START_DATE,cal3);
	        EarthGridQuery egq7 = new EarthGridQuery(egpQuery7);
	        EarthGridResponse response7 = dao.queryEarthGridSimulation(egq7);
	        System.out.println(response7.getResult().name());
	        IGrid[] gridresponse7 = response7.getAllGrids();
	        Calendar[] calresponse7 = response7.getAllGridDates();
	        for(int i = 0; i< gridresponse7.length; i++)
	        	System.out.println(gridresponse7[i].getSunPosition()+"@"+calresponse7[i].getTime());
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for EndDate <= Cal3");
	        EarthGridProperties egpQuery8 = new EarthGridProperties();
	        egpQuery8.setProperty(EarthGridProperty.END_DATE,cal3);
	        EarthGridQuery egq8 = new EarthGridQuery(egpQuery8);
	        EarthGridResponse response8 = dao.queryEarthGridSimulation(egq8);
	        System.out.println(response8.getResult().name());
	        if(response8.getResult() == ResponseType.FOUND_MANY || response8.getResult() == ResponseType.FOUND_ONE){
	        	IGrid[] gridresponse8 = response8.getAllGrids();
	        	Calendar[] calresponse8 = response8.getAllGridDates();
	        	for(int i = 0; i< gridresponse8.length; i++)
	        		System.out.println(gridresponse8[i].getSunPosition()+"@"+calresponse8[i].getTime());
	        }
	        System.out.println("----------------------------");
        	
	        System.out.println("#Query for EndDate <= Cal3 and StartDate >= Cal2");
	        EarthGridProperties egpQuery9 = new EarthGridProperties();
	        egpQuery9.setProperty(EarthGridProperty.END_DATE,cal3);
	        egpQuery9.setProperty(EarthGridProperty.START_DATE,cal2);
	        EarthGridQuery egq9 = new EarthGridQuery(egpQuery9);
	        EarthGridResponse response9 = dao.queryEarthGridSimulation(egq9);
	        System.out.println(response9.getResult().name());
	        if(response9.getResult() == ResponseType.FOUND_MANY || response9.getResult() == ResponseType.FOUND_ONE){
		        IGrid[] gridresponse9 = response9.getAllGrids();
		        Calendar[] calresponse9 = response9.getAllGridDates();
		        for(int i = 0; i< gridresponse9.length; i++)
		        	System.out.println(gridresponse9[i].getSunPosition()+"@"+calresponse9[i].getTime());
	        }   
        }catch(SQLException ex){
        	for (Throwable e : ex)
            {
                if (e instanceof SQLException)
                {
                    SQLException sqlException = (SQLException) e;
                    if ( true )
                    {
                    	sqlException.printStackTrace(System.out);
                        System.out.println("SQLState: " + sqlException.getSQLState());
                        System.out.println("Error Code: " + sqlException.getErrorCode());
                        System.out.println("Message: " + sqlException.getMessage());
                        Throwable t = ex.getCause();
                        while (t != null)
                        {
                            System.out.println("Cause: " + t);
                            t = t.getCause();
                        }
                    }
                }
            }
        }finally{
        	dao.resetDatabase("42");
        }

        System.out.println("Done with test!");
    }
    
    
    public static String printCalendar(Calendar c){
		StringBuilder sb = new StringBuilder();
		sb.append(c.get(Calendar.YEAR)+"-");
		sb.append((c.get(Calendar.MONTH)+1)+"-");
		sb.append(c.get(Calendar.DAY_OF_MONTH)+" ");
		sb.append(c.get(Calendar.HOUR_OF_DAY)+":");
		sb.append(c.get(Calendar.MINUTE)+":");
		sb.append(c.get(Calendar.SECOND)+".");
		sb.append(c.get(Calendar.MILLISECOND)+" ");
		
		return sb.toString();
	}
	
	public static Calendar getBaseCalendar(){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.set(2014, 6, 11, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 542);
		
		return c;
	}
}
