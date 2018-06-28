package Database;

import org.postgresql.util.HStoreConverter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Logger;
import Models.DeviceHistory;

public class Postgres {
    
    private static Logger logger = Logger.getLogger("myLogger");
    private static String databaseName = "myDB";
    private static Postgres postgres = null;

    private static Connection db = null;

    private  Postgres(){
        try{
            //Read ip, port from config file
            Properties prop = new Properties();
            String fileName = "iotmon.config";
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            String port = prop.getProperty("POSTGRES_PORT");
            String ip = prop.getProperty("POSTGRES_IP");

            makeConnection(ip, port);
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error initializing postgres: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Initialize the singleton instance of postgres, connecting to the database.
     * Must be done before any static methods can be used.
     */
    public static void initialize() {
        if (postgres == null){
            postgres = new Postgres();
        }
    }

    /**
     * First time database setup.
     * Creates necessary extensions, databases, and tables
     */
    public static void setupDatabase(){
        createHstoreExtension();
        makeDatabase();
        makeTables();
    }

    /**
     * Drops and recreates all tables.
     */
    public static void resetDatabase(){
        dropTables();
        makeTables();
    }

    /**
     * Connects to the postgres database, allowing for database ops.
     * @return
     */
    public static void makeConnection(String ip, String port){
        if(db == null){
            try {
                Class.forName("org.postgresql.Driver");
                db = DriverManager
                        .getConnection("jdbc:postgresql://" + ip + ":" + port + "/myDB",
                                "postgres", "123");
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("Error connecting to database : " + e.getClass().getName()+": "+e.getMessage());
            }
        }
    }

    /**
     * Executes the given SQL command in the database.
     * @param command SQL commmand string
     */
    public static void executeCommand(String command){
        try{
            Statement statement = db.createStatement();
            statement.execute(command);
            //statement.executeUpdate(command);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error executing database command: '" + command + "' " +
                    e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Lists all postgres databases. Primarily for testing.
     */
    public static void listAllDatabases() {
        try {
            PreparedStatement ps = db
                    .prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logger.info(rs.getString(1));
            }
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Drops all tables from the database.
     */
    public static void dropTables(){
        List<String> tableNames = Arrays.asList("deviceHistory");
        for(String tableName: tableNames){
            dropTable(tableName);
        }
    }

    /**
     * Drop a table from the database.
     * @param tableName name of the table to be dropped
     */
    public static void dropTable(String tableName){
        logger.info("Dropping database");
        executeCommand("DROP TABLE " + tableName);
    }

    /**
     * Creates a postgres database.
     */
    public static void makeDatabase(){
        logger.info("Creating database.");
        executeCommand("CREATE DATABASE " + databaseName);
    }

    /**
     * Create tables for each model.
     */
    public static void makeTables(){
        logger.info("Making tables.");
        executeCommand("CREATE TABLE IF NOT EXISTS deviceHistory_history(" +
                "deviceId           VARCHAR(255)," +
                "attributes   hstore, " +
                "timestamp    TIMESTAMP," +
                "id           VARCHAR(255)    PRIMARY KEY,"
        );
    }

    /**
     * Add the hstore extension to the postgres database.
     */
    public static void createHstoreExtension(){
        logger.info("Adding hstore extension.");
        executeCommand("CREATE EXTENSION hstore");
    }

    /**
     * Finds a deviceHistory from the database by its id.
     * @param id id of the deviceHistory to find.
     * @return the deviceHistory if it exists in the database, else null.
     */
    public static DeviceHistory findDeviceHistory(String id){
        DeviceHistory deviceHistory = null;
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM deviceHistory WHERE ID ='" + id+"'");
            while (rs.next())
            {
                deviceHistory = rsToDeviceHistory(rs);
            }
            rs.close();
            st.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error finding deviceHistory: " + e.getClass().getName()+": "+e.getMessage());
        }
        return deviceHistory;
    }

    /**
     * Finds all deviceHistorys in the database.
     * @return a list of all deviceHistorys in the database.
     */
    public static List<DeviceHistory> getAllDeviceHistories(){
        List<DeviceHistory> deviceHistorys = new ArrayList<DeviceHistory>();
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM deviceHistory");
            while (rs.next())
            {
                deviceHistorys.add(rsToDeviceHistory(rs));
            }
            rs.close();
            st.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error getting all deviceHistorys: " + e.getClass().getName()+": "+e.getMessage());
        }
        return deviceHistorys;
    }

    /**
     * Extract a deviceHistory from the result set of a database query.
     * @param rs result set from a deviceHistory query.
     * @return The deviceHistory that was found.
     */
    private static DeviceHistory rsToDeviceHistory(ResultSet rs){
        DeviceHistory deviceHistory = null;
        try{
            String deviceId = rs.getString(1);
            Map<String, String> attributes = HStoreConverter.fromString(rs.getString(2));
            Timestamp timestamp = rs.getTimestamp(3);
            String historyId = rs.getString(4);

            deviceHistory = new DeviceHistory(deviceId, attributes, timestamp, historyId);
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error converting rs to deviceHistory: " + e.getClass().getName()+": "+e.getMessage());
        }
        return deviceHistory;
    }

    /**
     * Saves given deviceHistory to the database.
     * @param deviceHistory deviceHistory to be inserted.
     */
    public static void insertDeviceHistory(DeviceHistory deviceHistory){
        logger.info("Inserting deviceHistory with id=" + deviceHistory.id);

        try{
            PreparedStatement update = db.prepareStatement
                    ("INSERT INTO deviceHistory(device_id, attributes, timestamp, id) values(?,?,?,?)");
            update.setString(1, deviceHistory.deviceId);
            update.setObject(2, deviceHistory.attributes);
            update.setTimestamp(3, deviceHistory.timestamp);
            update.setString(4, deviceHistory.id);

            update.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error inserting deviceHistory: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Updates deviceHistory with given id to have the parameters of the given deviceHistory.
     * @param deviceHistory holding new parameters to be saved in the database.
     */
    public static void updateDeviceHistory(DeviceHistory deviceHistory){
        logger.info("Updating deviceHistory with id=" + deviceHistory.id);

        try{
            PreparedStatement update = db.prepareStatement
                    ("UPDATE deviceHistory SET device_id = ?, attributes = ?, timestamp = ?, id = ? " +
                            "WHERE id=?");

            update.setString(1, deviceHistory.id);
            update.setObject(2, deviceHistory.attributes);
            update.setTimestamp(3, deviceHistory.timestamp);
            update.setString(4, deviceHistory.id);

            update.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error updating deviceHistory: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * First, attempts to find the deviceHistory in the database.
     * If successful, updates the existing deviceHistory with the given deviceHistory's parameters Otherwise,
     * inserts the given deviceHistory.
     * @param deviceHistory deviceHistory to be inserted or updated.
     */
    public static void insertOrUpdateDeviceHistory(DeviceHistory deviceHistory){
        if(findDeviceHistory(deviceHistory.id) != null){
            updateDeviceHistory(deviceHistory);
        }
        else{
            insertDeviceHistory(deviceHistory);
        }
    }
}