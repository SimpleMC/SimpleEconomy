package org.simplemc;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.plugin.java.JavaPlugin;
import org.simplemc.commands.MoneyCommand;
import org.simplemc.database.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SimpleEconomy extends JavaPlugin
{
    private DatabaseManager databaseManager;

    @Override
    public void onEnable()
    {
        getLogger().info(getName() + " is loading...");
        saveDefaultConfig();
        getCommand("money").setExecutor(new MoneyCommand(this));
        databaseManager = new DatabaseManager(this);
        getLogger().info(getName() + " has finished loading!");
    }

    public DatabaseManager getDatabaseManager()
    {
        return databaseManager;
    }

    /**
     * Requests an account from the database.
     *
     * @param uuid UUID of the account
     * @return Account
     */
    public Account getAccount(UUID uuid)
    {
        ResultSet resultSet = getDatabaseManager().selectAccount(uuid);
        try
        {
            if (resultSet.next())
            {
                int resultId = resultSet.getInt("id");
                String resultUUID = resultSet.getString("uuid");
                double resultBalance = resultSet.getDouble("balance");
                return new Account(resultId, UUID.fromString(resultUUID), resultBalance, this);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return createAccount(uuid, getConfig().getInt("defaults.balance"));
    }

    /**
     * Creates an account in the database.
     *
     * @param uuid    The uuid of the player
     * @param balance The starting balance of the account.
     * @return The new account
     */
    public Account createAccount(UUID uuid, double balance)
    {
        int accountId = getDatabaseManager().createAccount(uuid, balance);
        if (accountId != -1) {
            return new Account(accountId, uuid, balance, this);
        } else {
            throw new RuntimeException("Failure to create account.");
        }
    }
}
