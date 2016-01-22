package org.simplemc;

import org.apache.commons.lang.NotImplementedException;

import java.util.UUID;

public class Account
{
    private final int id;
    private final UUID uuid;
    private double balance;
    private final SimpleEconomy simpleEconomy;

    public Account(int id, UUID uuid, double balance, SimpleEconomy simpleEconomy)
    {
        this.id = id;
        this.uuid = uuid;
        this.balance = balance;
        this.simpleEconomy = simpleEconomy;
    }

    /**
     * Gets the database ID for the player.
     * Note: This is mainly for internal use. You should not need this if you are just hooking into this plugin.
     *
     * @return The database ID as an int.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the UUID attached to the account.
     *
     * @return UUID of the player for this account.
     */
    public UUID getUuid()
    {
        return uuid;
    }

    /**
     * Gets the balance for this account.
     *
     * @return The balance of the account as a double.
     */
    public double getBalance()
    {
        return balance;
    }

    /**
     * Sets the balance of the account.
     *
     * @param balance The new balance for the account.
     */
    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    /**
     * Saves the account to the database.
     *
     * @return true if the save was successful
     */
    public boolean save()
    {
        return simpleEconomy.getDatabaseManager().saveAccount(this);
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", balance=" + balance +
                '}';
    }
}
