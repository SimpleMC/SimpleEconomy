package org.simplemc.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simplemc.SimpleEconomy;
import org.simplemc.Account;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor
{
    private final SimpleEconomy economy;

    public MoneyCommand(SimpleEconomy economy)
    {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if (args.length >= 1)
        {
            switch (SubCommands.valueOf(args[0].toUpperCase())){
                case GET:
                    if (args.length == 2)
                    {
                        return geBalance(commandSender, args[1]);
                    }
                    else
                    {
                        return geBalance(commandSender, null);
                    }
                case SET:
                    if (args.length == 2)
                    {
                        return setBalance(commandSender, null, Double.parseDouble(args[1]));
                    }
                    else if (args.length == 3)
                    {
                        return setBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                case GIVE:
                    if (args.length == 3)
                    {
                        return giveBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                case TAKE:
                    if (args.length == 3)
                    {
                        return takeBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                case SEND:
                    if (args.length == 3)
                    {
                        return sendBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                case TOP:
                    HashMap<UUID, Double> top = economy.getDatabaseManager().getTop(10);
                    top.entrySet().stream().map(x -> {
                        String name = economy.getServer().getOfflinePlayer(x.getKey()).getName();
                        return String.format("%s - %f", name, x.getValue());
                    }).forEach(commandSender::sendMessage);
                    return true;
                case HELP:
                    commandSender.sendMessage(economy.formatPhrase("help"));
                    return true;
            }

        }
        return geBalance(commandSender, null);
    }

    private boolean sendBalance(CommandSender commandSender, String name, Double amount)
    {
        OfflinePlayer offlinePlayer = getPlayerByName(name);
        if (offlinePlayer != null)
        {
            Account sender = economy.getAccount(((Player) commandSender).getUniqueId());
            if (amount <= 0)
            {
                commandSender.sendMessage(economy.formatPhrase("error.input.toolow"));
                return true;
            }
            if (sender.getBalance() - amount <= 0)
            {
                commandSender.sendMessage(economy.formatPhrase("error.balance.toolow"));
                return true;
            }

            UUID uuid = offlinePlayer.getUniqueId();
            Account reciver = economy.getAccount(uuid);
            if (sender.getUuid().equals(reciver.getUuid()))
            {
                commandSender.sendMessage(economy.formatPhrase("error.player.yourself"));
                return true;
            }
            sender.setBalance(sender.getBalance() - amount);
            sender.save();
            reciver.setBalance(reciver.getBalance() + amount);
            reciver.save();
            commandSender.sendMessage(economy.formatPhrase("balance.send", amount, offlinePlayer.getName()));
        }
        else
        {
            commandSender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }
        return true;
    }

    private boolean giveBalance(CommandSender sender, String name, Double amount)
    {
        OfflinePlayer offlinePlayer = getPlayerByName(name);
        if (offlinePlayer != null)
        {
            UUID uuid = offlinePlayer.getUniqueId();
            Account account = economy.getAccount(uuid);
            account.setBalance(account.getBalance() + amount);
            account.save();
            sender.sendMessage(economy.formatPhrase("balance.give", amount, offlinePlayer.getName()));
        }
        else
        {
            sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }
        return true;
    }

    private boolean takeBalance(CommandSender sender, String name, Double amount)
    {
        OfflinePlayer offlinePlayer = getPlayerByName(name);
        if (offlinePlayer != null)
        {
            UUID uuid = offlinePlayer.getUniqueId();
            Account account = economy.getAccount(uuid);
            account.setBalance(account.getBalance() - amount);
            account.save();
            sender.sendMessage(economy.formatPhrase("balance.take", amount, offlinePlayer.getName()));
        }
        else
        {
            sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }
        return true;
    }

    private boolean setBalance(CommandSender sender, String name, Double amount)
    {
        if (name != null)
        {
            OfflinePlayer offlinePlayer = getPlayerByName(name);
            if (offlinePlayer != null)
            {
                UUID uuid = offlinePlayer.getUniqueId();
                Account account = economy.getAccount(uuid);
                account.setBalance(amount);
                account.save();
                sender.sendMessage(economy.formatPhrase("balance.set.other", offlinePlayer.getName(), account.getBalance()));
            }
            else
            {
                sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
            }
        }
        else
        {
            Account account = economy.getAccount(((Player) sender).getUniqueId());
            account.setBalance(amount);
            account.save();
            sender.sendMessage(economy.formatPhrase("balance.set.self", account.getBalance()));
        }
        return true;
    }

    private boolean geBalance(CommandSender sender, String name)
    {
        if (name == null)
        {
            Account account = economy.getAccount(((Player) sender).getUniqueId());
            sender.sendMessage(economy.formatPhrase("balance.get.self", account.getBalance()));
        }
        else
        {
            OfflinePlayer playerByName = getPlayerByName(name);
            if (playerByName != null)
            {
                Account account = economy.getAccount(playerByName.getUniqueId());
                sender.sendMessage(economy.formatPhrase("balance.get.other", playerByName.getName(), account.getBalance()));
            }
            else
            {
                sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
            }
        }
        return true;
    }


    /**
     * Wrapper method for getting a Player object by name.
     * This will first check for an online player by the name and then check through all offline players for it.
     *
     * @param name Player name
     * @return OfflinePlayer object if the player is found else it returns null.
     */
    private OfflinePlayer getPlayerByName(String name)
    {
        Player player = economy.getServer().getPlayer(name);
        if (player != null)
        {
            return player;
        }
        else
        {
            //TODO: Add some sort of caching of this so we don't have to do this every time.
            Optional<OfflinePlayer> offlinePlayer = Arrays.asList(economy.getServer().getOfflinePlayers()).stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst();
            if (offlinePlayer.isPresent())
            {
                return offlinePlayer.get();
            }
        }
        return null;
    }
}

enum SubCommands
{
    GET,
    SET,
    GIVE,
    TAKE,
    SEND,
    TOP,
    HELP
}
