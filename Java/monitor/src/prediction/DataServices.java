package prediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * DirectWebRemoting singleton providing the services
 * related with the data stored in the database 
 * @author heapoverflow
 */
public final class DataServices{

	private static String connectionString = "jdbc:hsqldb:file:mmdb";
	private static Connection c;
	//private static ArrayList<TimeSeries> loaded;

	private static final DataServices INSTANCE = new DataServices();

	private DataServices() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
		try {
			c = DriverManager.getConnection(connectionString, "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InstantiationError("Can't connect with database");
		}
	}

	public static DataServices getInstance() {
		return INSTANCE;
	}

	public int test(){
		return 10;
	}

	public ArrayList<double[][]> getData() {
		System.out.println("prediction.DataServices : getData service provided");
		try {
			Statement s = c.createStatement();
			// get the data
			ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM PUBLIC.STORE");
			rs.next();
			int rowCount = rs.getInt(1);
			
			// build TimeSeries component
			Serie serieX = new Serie("X", rowCount); 
			Serie serieY = new Serie("Y", rowCount);
			int[] time = new int[rowCount];
			
			// fill the series
			rs = s.executeQuery("SELECT * FROM PUBLIC.STORE");
			int i = 0;
			while(rs.next() == true){
				time[i] = rs.getInt(1);
				serieX.value[i] = rs.getDouble(4);
				serieY.value[i] = rs.getDouble(6);
				i++;
			}
			ArrayList<Serie> series = new ArrayList<Serie>();
			series.add(serieX);
			series.add(serieY);
			TimeSeries donnee = new TimeSeries("example", time[0], time[1]-time[0], series);
			return donnee.getPlotData();
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		}
		return null;
	}
}