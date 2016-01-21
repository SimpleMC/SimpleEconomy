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

    public int getId()
    {
        return id;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    /**
     * Saves the account to the database.
     * @return true if the save was successful
     */
    public boolean save()
    {
        throw new NotImplementedException("save not implemented.");
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
