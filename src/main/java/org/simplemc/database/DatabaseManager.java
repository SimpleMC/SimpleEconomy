package org.simplemc.database;

import org.apache.commons.io.IOUtils;
import org.simplemc.Account;
import org.simplemc.SimpleEconomy;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
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

    /**
     * Initialises the connection to the database and sets up the appropriate tables.
     */
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

            // Loads the ddl from the jar and commits it to the database.
            InputStream input = getClass().getResourceAsStream("/ddl.sql");
            try
            {
                String s = IOUtils.toString(input);
                connection.createStatement().execute(s);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (SQLException e)
        {
            simpleEconomy.getLogger().severe("Could not connect to database.");
            simpleEconomy.getServer().getPluginManager().disablePlugin(simpleEconomy);
            e.printStackTrace();
        }
    }

    /**
     * Fetches the account from the database;
     * @param uuid UUID of the account in question.
     * @return A ResultSet for the account.
     */
    public ResultSet selectAccount(UUID uuid)
    {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement(
                    String.format("SELECT * FROM %s.accounts WHERE uuid = ?;",
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

    /**
     * Performs an update of the given account to the database.
     *
     * @param account The account to save
     * @return true if the update succeeded.
     */
    public boolean saveAccount(Account account)
    {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement(
                    String.format("UPDATE %s.accounts SET balance=? WHERE id = ?;",
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

    /**
     * Create an account for the given UUID with the starting balance.
     * @param uuid UUID for the player
     * @param startingBalance The starting balance for the account.
     * @return The database ID for the account.
     */
    public int createAccount(UUID uuid, double startingBalance)
    {
        PreparedStatement preparedStatement = null;
        try
        {
            preparedStatement = connection.prepareStatement(
                    String.format("INSERT INTO %s.accounts (id, uuid, balance) VALUES (NULL, ?, ?);",
                            simpleEconomy.getConfig().getString("db.database")), Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, startingBalance);
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next())
            {
                return generatedKeys.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the top player accounts from the database.
     * @param limit The max amount of data to be returned.
     * @return A HashMap of the accounts.
     */
    public HashMap<UUID, Double> getTop(int limit) {
        PreparedStatement preparedStatement = null;
        try
        {
            HashMap<UUID, Double> topMap = new HashMap<>();
            preparedStatement = connection.prepareStatement(
                    String.format("SELECT uuid, balance FROM %s.accounts ORDER BY balance DESC LIMIT ?;",
                            simpleEconomy.getConfig().getString("db.database")));
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                topMap.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getDouble("balance"));
            }
            return topMap;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return new HashMap<>();
    }


}
