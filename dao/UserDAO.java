import java.sql.*;
import java.util.*;

public class UserDAO {

    public boolean regusterUser(User user){  //<-- it used for register and return true for successful register
        String sql = "INSERT INTO user (name,email,password,role) VALUES (?,?,?,?)";

        try(PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)){

//            stmt.setString(1,user.getName());
//            stmt.setString(2,user.getEmail);
//            stmt.setString(3,user.getPassword);
//            stmt.setString(4,user.getRole);

            int rowInserted = stmt.executeUpdate(); //<-- this check if at list one row is inserted
            return rowInserted > 0;  //<--- if 1 it will return true else false (no row inserted)

        }catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public User loginUser(String email){ //<-- by using email it will retur User(containing full user info)
        String sql = "SELECT * FROM user WHERE email = ?";

        try(PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)){
            stmt.setString(1,email);

            ResultSet row = stmt.executeQuery(); //<-- this hold row sqlite user data based one email
           if(row.next()){//<--- this check if the row have data
//                return new User(
//                        row.getInt("id"),
//                        row.getString("name"),
//                        row.getString("email"),
//                        row.getString("password"),
//                        row.getString("row"));
            }

        }catch(SQLException e){
            System.err.println("Login Error: "+e.getMessage());
        }
        return null;
    }

    public boolean deleteUser(User user){
        return false;
    }
}
