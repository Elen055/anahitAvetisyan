import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    public static Connection connect()  {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("JDBC:mysql://localhost:3306/college", "root", "admin");
            System.out.println("Connected");
        }
        catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return connection;
    }

}



