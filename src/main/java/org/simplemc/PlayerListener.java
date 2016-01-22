package org.simplemc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener
{
    private final SimpleEconomy economy;

    public PlayerListener(SimpleEconomy economy)
    {
        this.economy = economy;
    }

    @EventHandler
    public void onLoginEvent(PlayerLoginEvent event)
    {
        economy.getAccount(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event)
    {
        UUID uniqueId = event.getPlayer().getUniqueId();
        if (economy.accounts.containsKey(uniqueId))
        {
            economy.accounts.get(uniqueId).save();
            economy.accounts.remove(uniqueId);
        }
    }
}