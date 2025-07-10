import java.io.*;
import java.util.*;
import java.sql.*;


public class DBUtil {
    private static Connection conn;

    public static Connection getConnection() {
        try{
            Properties prop = new Properties();
            FileInputStream file = new FileInputStream("config/db_config.txt");
            prop.load(file);

            String dbUrl = prop.getProperty("db_url");
            conn = DriverManager.getConnection(dbUrl);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
