package monitoring.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import cep.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EventDAO {
	private Connection conn;
	private PreparedStatement insertStmt, queryStmt, countStmt;
	private static String CONNECTION_STRING = "jdbc:derby:db;create=true";
	private static String DRIVER_CLASSNAME = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String USERNAME = "root";
	private static String PASSWORD = "root";
	private static Logger logger = Logger.getLogger("EventDAO");
	
	static {
		logger.setLevel(Level.WARNING);
	}
	
	// SQLs
	private static String SQL_CREATE_EVENT_TABLE = "CREATE TABLE eventinfo " +
			"(id int NOT NULL GENERATED ALWAYS AS IDENTITY," +
			"type varchar(50) NOT NULL," +
			"source varchar(30) NOT NULL," +
			"time timestamp NOT NULL," +
			"detail varchar(1000) NOT NULL," +
			"PRIMARY KEY (id))";
	// a new event
	private static String SQL_INSERT_EVENT = "INSERT INTO eventinfo " +
			"(type, source, time, detail) VALUES (?, ?, ?, ?)";
	// query events by page
	private static String SQL_QUERY_EVENT = 
			"SELECT * FROM eventinfo WHERE id > ? AND type LIKE ? " +
			"AND source LIKE ? AND time > ? ORDER BY id DESC " +
			"OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
	// count events
	private static String SQL_COUNT_EVENT = 
			"SELECT COUNT(*) FROM eventinfo WHERE id > ? AND type LIKE ? " +
			"AND source LIKE ? AND time > ?";
	
	
	public void init() {
		try {
			Class.forName(DRIVER_CLASSNAME).newInstance();
		} catch (Exception e) {
			logger.info("Derby cannot be instantiated.");
			return;
		}
		
		try {
			conn = DriverManager.getConnection(
					CONNECTION_STRING, USERNAME, PASSWORD);
		} catch (SQLException dbNotFound) {
			dbNotFound.printStackTrace();
			return;
		}
		try {
			prepareAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createTable() {
		try {
			Class.forName(DRIVER_CLASSNAME).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			Statement createTableStmt = DriverManager.getConnection(
					CONNECTION_STRING, USERNAME, PASSWORD).createStatement();
			createTableStmt.execute(SQL_CREATE_EVENT_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private static void dropTable() {
		try {
			Class.forName(DRIVER_CLASSNAME).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			Statement createTableStmt = DriverManager.getConnection(
					CONNECTION_STRING, USERNAME, PASSWORD).createStatement();
			createTableStmt.execute("DROP TABLE eventinfo");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	
	private void prepareAll() throws SQLException {
		insertStmt = conn.prepareStatement(SQL_INSERT_EVENT);
		countStmt = conn.prepareStatement(SQL_COUNT_EVENT);
		queryStmt = conn.prepareStatement(SQL_QUERY_EVENT);
	}
	
	public void saveEvent(Event event) {
		try {
			insertStmt.setString(1, event.getType());
			insertStmt.setString(2, event.getSource());
			insertStmt.setTimestamp(3, new Timestamp(event.getTime()));
			insertStmt.setString(4, event.getAttributesAsString());
			insertStmt.execute();
		} catch (SQLException e) {
			logger.warning("Failed to save " + event + "(" + e.getErrorCode() + ")\n" + e.getMessage());
			return;
		}
		logger.info("Saved " + event);
	}
	
	public String queryEvent(int lastId, String type, String source,
			long timeBegin, int start, int limit) {
		JsonArray data = new JsonArray();
		int count = 0;
		try {
			queryStmt.setInt(1, lastId);
			queryStmt.setString(2, type);
			queryStmt.setString(3, source);
			queryStmt.setTimestamp(4, new Timestamp(timeBegin));
			queryStmt.setInt(5, start);
			queryStmt.setInt(6, limit);

			countStmt.setInt(1, lastId);
			countStmt.setString(2, type);
			countStmt.setString(3, source);
			countStmt.setTimestamp(4, new Timestamp(timeBegin));

			ResultSet rs = queryStmt.executeQuery();
			while (rs.next()) {
				JsonObject record = new JsonObject();
				int curId = rs.getInt(1);
				record.addProperty("id", curId);
				record.addProperty("type", rs.getString(2));
				record.addProperty("source", rs.getString(3));
				record.addProperty("time", rs.getTimestamp(4).getTime());
				String detailStr = rs.getString(5);
				record.add("detail", new JsonParser().parse(detailStr));
				data.add(record);
			}
			rs = countStmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			rs = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JsonObject o = new JsonObject();
		o.addProperty("success", true);
		o.addProperty("total", count);
		o.add("data", data);
		return o.toString();
	}

	/**
	 * Run once to create table!
	 * @param args
	 */
	public static void main(String[] args) {
		dropTable();
		createTable();
		
	}

}
