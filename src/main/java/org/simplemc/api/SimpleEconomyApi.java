package org.simplemc.api;

import org.apache.commons.lang.NotImplementedException;
import org.simplemc.SimpleEconomy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SimpleEconomyApi
{
    private final SimpleEconomy simpleEconomy;

    public SimpleEconomyApi(SimpleEconomy simpleEconomy)
    {
        this.simpleEconomy = simpleEconomy;
    }

    /**
     * Requests an account from the database.
     *
     * @param uuid UUID of the account
     * @return Account
     */
    public Account getAccount(UUID uuid)
    {
        ResultSet resultSet = simpleEconomy.getDatabaseManager().selectAccount(uuid);
        try
        {
            if (resultSet.next()) {
                int resultId = resultSet.getInt("id");
                String resultUUID = resultSet.getString("uuid");
                double resultBalance = resultSet.getDouble("balance");
                return new Account(resultId, UUID.fromString(resultUUID), resultBalance);
            } else {
                return createAccount(uuid, 100); //TODO: Grab this value from config
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves an account to the database.
     *
     * @param account The account to save
     * @return true if save succeed.
     */
    public boolean saveAccount(Account account)
    {
        throw new NotImplementedException("saveAccount not implemented.");
    }

    /**
     * Creates an account in the database.
     *
     * @param uuid The uuid of the player
     * @param balance The starting balance of the account.
     * @return The new account
     */
    public Account createAccount(UUID uuid, double balance)
    {
        throw new NotImplementedException("saveAccount not implemented.");
    }
}
