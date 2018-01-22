package common;

import org.json.*;
import java.sql.*;
import java.util.*;

public class Groups {
    private static final String INSERT_NEW = "INSERT INTO groups (group_id, name,complete) VALUES (?, ?, ?)";
    private static final String SEARCH_GROUP =  "SELECT * FROM groups WHERE group_id = ?";
    private static final String UPDATE_GROUP =  "UPDATE groups SET offset = ? WHERE group_id = ?;";
    ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public Groups(JSONObject jsonObject) throws JSONException {

        JSONArray  jsonArray = jsonObject.getJSONArray("response");
        for (int i = 1; i < jsonArray.length(); i++) {

            JSONObject group = jsonArray.getJSONObject(i);
            this.arrayList.add(group.getInt("gid"));
            addGroupToDB(group.getInt("gid"),group.getString("name"));
        }
    }

    private static void addGroupToDB(int group_id, String name){
        try {
            PreparedStatement  pstmt = DatabaseConnection.connection.prepareStatement(SEARCH_GROUP);
            pstmt.setInt(1, group_id);
            ResultSet rs = pstmt.executeQuery();
            rs.last();
            if(rs.getRow() == 0){
                PreparedStatement preparedStatement = DatabaseConnection.connection.prepareStatement(INSERT_NEW);
                preparedStatement.setInt(1,group_id);
                preparedStatement.setString(2,name);
                preparedStatement.setInt(3,0);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       // databaseConnection.closeConnection();
    }

    public static void updateOffsetGroup(int group_id, int offset){
        try {
            PreparedStatement preparedStatement = DatabaseConnection.connection.prepareStatement(UPDATE_GROUP);
            preparedStatement.setInt(1,offset);
            preparedStatement.setInt(2,group_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // databaseConnection.closeConnection();
    }

    public static int getOffsetGroup(int group_id){

        int offset = 0;
        try {
            PreparedStatement  pstmt = DatabaseConnection.connection.prepareStatement(SEARCH_GROUP);
            pstmt.setInt(1, group_id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                offset = rs.getInt("offset");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offset;
    }

    public static boolean isCompleteGroup(int group_id){
        boolean complete = false;
        try {
            PreparedStatement  pstmt = DatabaseConnection.connection.prepareStatement(SEARCH_GROUP);
            pstmt.setInt(1, group_id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                if(rs.getInt("complete") == 1){
                    complete = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return complete;
    }

}