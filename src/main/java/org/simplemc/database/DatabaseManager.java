package org.simplemc.database;

import org.simplemc.Account;
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

    private void setupDatabase()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            simpleEconomy.getLogger().severe("Could not load database driver.");
            simpleEconomy.getServer().getPluginManager().disablePlugin(simpleEconomy);
            e.printStackTrace();
        }

        try
        {
            connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s",
                            simpleEconomy.getConfig().getString("db.host"),
                            simpleEconomy.getConfig().getString("db.database"),
                            simpleEconomy.getConfig().getString("db.username"),
                            simpleEconomy.getConfig().getString("db.password")
                    ));
        }
        catch (SQLException e)
        {
            simpleEconomy.getLogger().severe("Could not connect to database.");
            simpleEconomy.getServer().getPluginManager().disablePlugin(simpleEconomy);
            e.printStackTrace();
        }
    }

    public ResultSet selectAccount(UUID uuid)
    {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement(
                    String.format("SELECT * FROM %s.accounts WHERE uuid = ?",
                            simpleEconomy.getConfig().getString("db.database")));
            preparedStatement.setString(1, uuid.toString());
            return preparedStatement.executeQuery();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveAccount(Account account)
    {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement(
                    String.format("UPDATE %s.accounts SET balance=? WHERE id = ?",
                            simpleEconomy.getConfig().getString("db.database")));
            preparedStatement.setDouble(1, account.getBalance());
            preparedStatement.setInt(2, account.getId());
            preparedStatement.execute();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
