package org.simplemc.database;

import org.simplemc.SimpleEconomy;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager
{
    private final SimpleEconomy simpleEconomy;
    private Connection connection;

    public DatabaseManager(SimpleEconomy simpleEconomy)
    {
        this.simpleEconomy = simpleEconomy;
        setupDatabase();
    }

    private void setupDatabase() {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e)
        {
            simpleEconomy.getLogger().severe("Could not load database driver.");
            simpleEconomy.getServer().getPluginManager().disablePlugin(simpleEconomy);
            e.printStackTrace();
        }

        try
        {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost/simpleeconomy?" //TODO: manage this from config.
                            + "user=root");
//            connection = DriverManager
//                    .getConnection("jdbc:mysql://localhost/feedback?"
//                            + "user=root&password=password");
        } catch (SQLException e)
        {
            simpleEconomy.getLogger().severe("Could not connect to database.");
            simpleEconomy.getServer().getPluginManager().disablePlugin(simpleEconomy);
            e.printStackTrace();
        }
    }

    public ResultSet selectAccount(UUID uuid) {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement("SELECT * FROM simpleeconomy.accounts WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            return preparedStatement.executeQuery();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
