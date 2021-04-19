package projectomicron.studybuddy;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDatabaseConnection {

    public SQLDatabaseConnection() throws SQLException {
        this.connectionUrl =
                "jdbc:sqlserver://teamalpha.database.windows.net:1433;"
                        + "database=studybuddy-db;"
                        + "user=tadmin@teamalpha;"
                        + "password=@t3amalpha;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        this.connection = DriverManager.getConnection(connectionUrl);
    }


    public void CreateAccount(String first_name, String last_name, String username,
                              String password, int reasonForUse) throws SQLException
    {
        String insertSql = "INSERT INTO [dbo].[UserProfile](\n" +
                " \taccount_id,\n" +
                "    first_name,\n" +
                "    last_name,\n" +
                "    username,\n" +
                "    [password],\n" +
                "    usertype\n" +
                ")\n" +
                "VALUES(\n" +
                    first_name + ",\n" +
                    last_name + ",\n" +
                    username + ",\n" +
                    password + ",\n" +
                    reasonForUse + "\n" +
                ")";

        PreparedStatement insertProfile = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
        insertProfile.execute();
        ResultSet resultSet = insertProfile.getGeneratedKeys();

        // Print the ID of the inserted row.
        while (resultSet.next()) {
            System.out.println("Generated: " + resultSet.getString(1));
        }
    }

    public boolean CheckUserName(String Username) throws SQLException
    {
        String selectSql = "SELECT COUNT(*) \n" +
                "FROM [dbo].[UserProfile]\n" +
                "WHERE [username] = '"+ Username +"'";

        PreparedStatement checkUsername = connection.prepareStatement(selectSql, Statement.RETURN_GENERATED_KEYS);
        ResultSet resultSet = checkUsername.executeQuery();

        return true;

    }

    // Connect to your database.
    // Replace server name, username, and password with your credentials
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) {
        String connectionUrl =
                "jdbc:sqlserver://yourserver.database.windows.net:1433;"
                        + "database=AdventureWorks;"
                        + "user=yourusername@yourserver;"
                        + "password=yourpassword;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        String insertSql = "INSERT INTO SalesLT.Product (Name, ProductNumber, Color, StandardCost, ListPrice, SellStartDate) VALUES "
                + "('NewBike', 'BikeNew', 'Blue', 50, 120, '2016-01-01');";

        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);) {

            prepsInsertProduct.execute();
            // Retrieve the generated key from the insert.
            resultSet = prepsInsertProduct.getGeneratedKeys();

            // Print the ID of the inserted row.
            while (resultSet.next()) {
                System.out.println("Generated: " + resultSet.getString(1));
            }
        }
        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String connectionUrl;
    private Connection connection;
}