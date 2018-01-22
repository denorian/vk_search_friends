package common;
import java.sql.*;

public class DatabaseConnection {
    public static final String URL_HOST = "jdbc:mysql://localhost:3306/vk_api?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASSWORD = "9891013212Dd+";
    public static Connection connection = null;

    static {
        Driver driver;
        try   {
            driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
        }
        catch (SQLException e1) {
            System.out.println("Драйвер не зарегистрировался");
        }

        try {
            connection = DriverManager.getConnection(URL_HOST, USER, PASSWORD);
            /*if (!connection.isClosed())
                System.out.println("Соединение установлено");*/
        }catch (SQLException ex){
            System.err.println("Соединение не установлено");
            ex.printStackTrace();
        }
    }

    public boolean closeConnection(){
        try {
            if (connection != null) connection.close();
        }catch (SQLException ex){
            return false;
        }
        return true;
    }

    /*public boolean query(String query){

       try(Statement statement = connection.createStatement()) {
           System.out.println(statement.execute("SELECT * FROM vk_api.users;"));
           Rese
       } catch (SQLException e) {
           e.printStackTrace();
       }

        return false;
    }*/
}
