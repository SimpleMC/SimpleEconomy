package org.simplemc;

import org.bukkit.plugin.java.JavaPlugin;
import org.simplemc.api.SimpleEconomyApi;
import org.simplemc.commands.MoneyCommand;
import org.simplemc.database.DatabaseManager;

public class SimpleEconomy extends JavaPlugin
{
    private SimpleEconomyApi api;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable()
    {
        getLogger().info(getName() + " is loading...");
        getCommand("money").setExecutor(new MoneyCommand(this));
        databaseManager = new DatabaseManager(this);
        api = new SimpleEconomyApi(this);
        getLogger().info(getName() + " has finished loading!");
    }

    public SimpleEconomyApi getApi()
    {
        return api;
    }

    public DatabaseManager getDatabaseManager()
    {
        return databaseManager;
    }
}
