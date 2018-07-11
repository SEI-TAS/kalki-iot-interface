package main.java.Database;

import main.java.Models.Device;
import org.postgresql.util.HStoreConverter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import main.java.Models.DeviceHistory;

import static java.lang.Thread.sleep;

public class Postgres {

    private static Logger logger = Logger.getLogger("myLogger");
    private static String databaseName = "myDB";
    private static Postgres postgres = null;

    public static Connection db = null;

    private  Postgres(){
        try{
            //Read ip, port from config file
            Properties prop = new Properties();
            String fileName = "iotmon.config";
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            String port = prop.getProperty("POSTGRES_PORT");
            String ip = prop.getProperty("POSTGRES_IP");
            db = makeConnection(ip, port);
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
        try {
            sleep(1000);
        } catch(Exception e){}
        createTriggers();
    }

    /**
     * Drops and recreates all tables.
     */
    public static void resetDatabase(){
        dropTables();
        makeTables();
        createTriggers();
    }

    /**
     * Connects to the postgres database, allowing for database ops.
     * @return a connection to the database.
     */
    public Connection makeConnection(String ip, String port){
        try {
            Class.forName("org.postgresql.Driver");
            db = DriverManager
                    .getConnection("jdbc:postgresql://" + ip + ":" + port + "/myDB",
                            "postgres", "123");
            return db;
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error connecting to database : " + e.getClass().getName()+": "+e.getMessage());
            return null;
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

    public static void createTriggers(){
        executeCommand("CREATE OR REPLACE FUNCTION \"testNotify\"()\n" +
                "  RETURNS TRIGGER AS $$\n" +
                "DECLARE\n" +
                "  payload TEXT;\n" +
                "BEGIN\n" +
                "  payload := NEW.id;\n" +
                "  PERFORM pg_notify('deviceinsert', payload);\n" +
                "  RETURN NEW;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");
        executeCommand("CREATE TRIGGER \"testNotify\"\n" +
                "AFTER INSERT ON device\n" +
                "FOR EACH ROW EXECUTE PROCEDURE \"testNotify\"()");
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
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("device_history");
        tableNames.add("device");
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
        executeCommand("CREATE TABLE IF NOT EXISTS device_history(" +
                "device_id     VARCHAR(255)," +
                "attributes   hstore, " +
                "timestamp    TIMESTAMP," +
                "id           VARCHAR(255)    PRIMARY KEY" +
                ");"
        );
        executeCommand("CREATE TABLE IF NOT EXISTS device (" +
                "id               VARCHAR(255)   PRIMARY KEY," +
                "device_id        varchar(255)," +
                "device_name             varchar(255)," +
                "device_type             varchar(255)," +
                "group_id         varchar(255)," +
                "ip_address       varchar(255)," +
                "history_size     int," +
                "sampling_rate    int" +
                ");");
    }

    /**
     * Add the hstore extension to the postgres database.
     */
    public static void createHstoreExtension(){
        logger.info("Adding hstore extension.");
        executeCommand("CREATE EXTENSION hstore");
    }

    //DeviceHistory specific methods

    /**
     * Finds a deviceHistory from the database by its id.
     * @param id id of the deviceHistory to find.
     * @return the deviceHistory if it exists in the database, else null.
     */
    public static DeviceHistory findDeviceHistory(String id){
        DeviceHistory deviceHistory = null;
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM device_history WHERE ID ='" + id+"'");
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
     * Finds all deviceHistories in the database.
     * @return a list of all deviceHistories in the database.
     */
    public static List<DeviceHistory> getAllDeviceHistories(){
        List<DeviceHistory> deviceHistories = new ArrayList<DeviceHistory>();
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM device_history");
            while (rs.next())
            {
                deviceHistories.add(rsToDeviceHistory(rs));
            }
            rs.close();
            st.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error getting all deviceHistories: " + e.getClass().getName()+": "+e.getMessage());
        }
        return deviceHistories;
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
        logger.info("Inserting device_history: " + deviceHistory.toString());

        try{
            PreparedStatement update = db.prepareStatement
                    ("INSERT INTO device_history(device_id, attributes, timestamp, id) values(?,?,?,?)");
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
                    ("UPDATE device_history SET device_id = ?, attributes = ?, timestamp = ?, id = ? " +
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

    //Device specific methods

    /**
     * Saves given device to the database.
     * @param device deviceHistory to be inserted.
     */
    public static void insertDevice(Device device){
        logger.info("Inserting device with id=" + device.id);

        try{
            PreparedStatement update = db.prepareStatement
                    ("INSERT INTO device(id, device_id, device_name, device_type, group_id, ip_address," +
                            "history_size, sampling_rate) values(?,?,?,?,?,?,?,?)");
            update.setString(1, device.id);
            update.setString(2, device.deviceId);
            update.setString(3, device.name);
            update.setString(4, device.type);
            update.setString(5, device.groupId);
            update.setString(6, device.ip);
            update.setInt(7, device.historySize);
            update.setInt(8, device.samplingRate);

            update.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error inserting device: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Finds all devices in the database.
     * @return a list of all deviceHistories in the database.
     */
    public static List<Device> getAllDevices(){
        List<Device> devices = new ArrayList<Device>();
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM device");
            while (rs.next())
            {
                devices.add(rsToDevice(rs));
            }
            rs.close();
            st.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error getting all deviceHistories: " + e.getClass().getName()+": "+e.getMessage());
        }
        return devices;
    }

    /**
     * Extract a deviceHistory from the result set of a database query.
     * @param rs result set from a deviceHistory query.
     * @return The deviceHistory that was found.
     */
    private static Device rsToDevice(ResultSet rs){
        Device device = null;
        try{
            String id = rs.getString(1);
            String deviceId = rs.getString(2);
            String name = rs.getString(3);
            String type = rs.getString(4);
            String groupId = rs.getString(5);
            String ip = rs.getString(6);
            int historySize = rs.getInt(7);
            int samplingRate = rs.getInt(8);

            device = new Device(id, deviceId, name, type, groupId, ip, historySize, samplingRate);
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error converting rs to deviceHistory: " + e.getClass().getName()+": "+e.getMessage());
        }
        return device;
    }

    /**
     * Finds a deviceHistory from the database by its id.
     * @param id id of the device to find.
     * @return the deviceHistory if it exists in the database, else null.
     */
    public static Device findDevice(String id){
        Device device = null;
        try{
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM device WHERE ID ='" + id+"'");
            while (rs.next())
            {
                device = rsToDevice(rs);
            }
            rs.close();
            st.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error finding device: " + e.getClass().getName()+": "+e.getMessage());
        }
        return device;
    }
}