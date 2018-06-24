package common;

import java.sql.*;
import java.util.*;

public class Users {
    String firstName;
    String lastName;
    String deactivated;
    int userId;
    int cityId;
    int relation;
    boolean sex;
   // Date birthDate;
    private static final String INSERT_NEW = "INSERT INTO users (user_id,first_name,last_name,sex,city_id,birth_date,deactivated,relation) VALUES (?,?,?,?,?,?,?,?)";
    private static final String INSERT_NEW_GROUP = "INSERT INTO users_group (user_id,group_id) VALUES (?,?)";
    private static final String SEARCH_USER = "SELECT * FROM users WHERE user_id = ?";
    private static final String SEARCH_MATCH_USER = "SELECT user_id, COUNT(*) as cnt  FROM users_group  GROUP BY user_id  HAVING COUNT(user_id) > 1";
    //private static final String SEARCH_MATCH_USER = "SELECT user_id, COUNT(*) as cnt  FROM users_group  GROUP BY user_id  HAVING COUNT(user_id) > 15 ORDER BY cnt DESC LIMIT 0,1000 ";


    ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public Users(String firstName, String lastName, int userId, int cityId, boolean sex, String birthDate, String deactivated, int relation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.cityId = cityId;
        this.sex = sex;
       // this.birthDate = birthDate;
        this.deactivated = deactivated;
        this.relation = relation;
        addUserToDB(userId,this);
    }


    public static LinkedHashMap<Integer, Integer> getMatchUsers(){
        LinkedHashMap<Integer, Integer> usersHashMap = new LinkedHashMap<>();
        try {
            PreparedStatement  pstmt = DatabaseConnection.connection.prepareStatement(SEARCH_MATCH_USER);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                usersHashMap.put(rs.getInt("user_id"),rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usersHashMap;
    }


   /* public void addGroupUser(int idGroup){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {
            PreparedStatement preparedStatement = databaseConnection.connection.prepareStatement(INSERT_NEW_GROUP);
            preparedStatement.setInt(1, this.userId);
            preparedStatement.setInt(2, idGroup);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

   /* public static void addGroupUser(int idGroup, int uid){
        try {
            PreparedStatement preparedStatement = DatabaseConnection.connection.prepareStatement(INSERT_NEW_GROUP);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, idGroup);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public static void addGroupUserArray(ArrayList<Integer> arrayList , int idGroup){
        try {
            Statement stmt = DatabaseConnection.connection.createStatement();
            DatabaseConnection.connection.setAutoCommit(false);
            for (Integer uid : arrayList) {
                stmt.addBatch("INSERT INTO users_group (user_id,group_id) VALUES (" + uid + "," + idGroup + ");");
            }
            stmt.executeBatch();
            DatabaseConnection.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addUserToDB(int userId, Users user){
        try {
            PreparedStatement pstmt = DatabaseConnection.connection.prepareStatement(SEARCH_USER);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            rs.last();
            pstmt = null;
            if(rs.getRow() == 0){
                PreparedStatement preparedStatement = DatabaseConnection.connection.prepareStatement(INSERT_NEW);
                preparedStatement.setInt(1,user.userId);
                preparedStatement.setString(2,user.firstName);
                preparedStatement.setString(3,user.lastName);
                preparedStatement.setInt(4,user.sex ? 1: 0);
                preparedStatement.setInt(5, user.cityId);
                preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setString(7,user.deactivated);
                preparedStatement.setInt(8, user.relation);
                preparedStatement.execute();
                preparedStatement = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}