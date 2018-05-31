import org.postgresql.util.HStoreConverter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Logger;

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
        List<String> tableNames = Arrays.asList("device");
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
        executeCommand("CREATE TABLE IF NOT EXISTS device(" +
                "deviceName   VARCHAR(255)," +
                "ip           VARCHAR(255)," +
                "id           VARCHAR(255)    PRIMARY KEY," +
                "attributes   hstore, " +
                "timestamp    TIMESTAMP," +
                "groupName        VARCHAR(255))");
    }

    /**
     * Add the hstore extension to the postgres database.
     */
    public static void createHstoreExtension(){
        logger.info("Adding hstore extension.");
        executeCommand("CREATE EXTENSION hstore");
    }

    /**
     * Finds a device from the database by its id.
     * @param id id of the device to find.
     * @return the device if it exists in the database, else null.
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

    /**
     * Finds all devices in the database.
     * @return a list of all devices in the database.
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
            logger.severe("Error getting all devices: " + e.getClass().getName()+": "+e.getMessage());
        }
        return devices;
    }

    /**
     * Extract a device from the result set of a database query.
     * @param rs result set from a device query.
     * @return The device that was found.
     */
    private static Device rsToDevice(ResultSet rs){
        Device device = null;
        try{
            String deviceName = rs.getString(1);
            String ip = rs.getString(2);
            String deviceId = rs.getString(3);
            Map<String, String> attributes = HStoreConverter.fromString(rs.getString(4));
            Timestamp timestamp = rs.getTimestamp(5); //
            String group = rs.getString(6);

            device = new Device(deviceName, ip, deviceId, group, attributes, timestamp);
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error converting rs to device: " + e.getClass().getName()+": "+e.getMessage());
        }
        return device;
    }

    /**
     * Saves given device to the database.
     * @param device device to be inserted.
     */
    public static void insertDevice(Device device){
        logger.info("Inserting device with id=" + device.id);
        //executeCommand("INSERT INTO device(deviceName, ip, id, attributes) values(" + deviceToValues(device) + ")");

        try{
            PreparedStatement update = db.prepareStatement
                    ("INSERT INTO device(deviceName, ip, id, attributes, timestamp, groupName) values(?,?,?,?,?,?)");

            update.setString(1, device.deviceName);
            update.setString(2, device.ip);
            update.setString(3, device.id);
            update.setObject(4, device.attributes);
            update.setTimestamp(5, device.timestamp);
            update.setString(6, device.group);

            update.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error inserting device: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * Updates device with given id to have the parameters of the given device.
     * @param device holding new parameters to be saved in the database.
     */
    public static void updateDevice(Device device){
        logger.info("Updating device with id=" + device.id);

        try{
            PreparedStatement update = db.prepareStatement
                    ("UPDATE device SET deviceName = ?, ip = ?, id = ?, attributes = ?, timestamp = ?, " +
                            "groupName = ? WHERE id=?");

            update.setString(1, device.deviceName);
            update.setString(2, device.ip);
            update.setString(3, device.id);
            update.setObject(4, device.attributes);
            update.setTimestamp(5, device.timestamp);
            update.setString(6, device.group);
            update.setString(7, device.id);

            update.executeUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            logger.severe("Error updating device: " + e.getClass().getName()+": "+e.getMessage());
        }
    }

    /**
     * First, attempts to find the device in the database.
     * If successful, updates the existing device with the given device's parameters Otherwise,
     * inserts the given device.
     * @param device device to be inserted or updated.
     */
    public static void insertOrUpdateDevice(Device device){
        if(findDevice(device.id) != null){
            updateDevice(device);
        }
        else{
            insertDevice(device);
        }
    }
}