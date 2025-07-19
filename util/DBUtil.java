import java.io.*;
import java.util.*;
import java.sql.*;


public class DBUtil {
    private static Connection conn;

    public static Connection getConnection() {
        if(conn != null){
            return conn;
        }

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

    public static void closeConnection() {
        try{
            if(conn != null && !conn.isClosed()){
               conn.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initTable(){
        try(Statement stmt = getConnection().createStatement()){

            //user table
            stmt.executeUpdate("""
                create TABLE IF NOT EXISTS user(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
            """);

            //Manager table
            stmt.executeUpdate("""
                create TABLE IF NOT EXISTS manager(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
            """);

            //movies table
            stmt.executeUpdate("""
                create TABLE IF NOT EXISTS movies(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    duration TEXT,
                    genre TEXT
                );
            """);
        System.out.println("Data base Table initialized!");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void dropTabel(){
        try(Statement stmt = getConnection().createStatement()){
            stmt.executeUpdate("DROP TABLE IF EXISTS user");
            stmt.executeUpdate("DROP TABLE IF EXISTS movies");
            System.out.println("Data base Table dropped!");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
