package org.simplemc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener
{
    private final SimpleEconomy economy;

    public PlayerLoginListener(SimpleEconomy economy)
    {
        this.economy = economy;
    }

    @EventHandler
    public void onLoginEvent(PlayerLoginEvent event)
    {
        economy.getAccount(event.getPlayer().getUniqueId());
    }
}
