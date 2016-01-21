package org.simplemc.api;

import org.apache.commons.lang.NotImplementedException;

import java.util.UUID;

public class Account
{
    private final int id;
    private final UUID uuid;
    private double balance;

    public Account(int id, UUID uuid, double balance)
    {
        this.id = id;
        this.uuid = uuid;
        this.balance = balance;
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
