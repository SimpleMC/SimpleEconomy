package org.simplemc.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simplemc.SimpleEconomy;
import org.simplemc.Account;

import java.util.Arrays;
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
            if (args[0].equalsIgnoreCase("get"))
            {
                Account account = economy.getAccount(((Player) commandSender).getUniqueId());
                commandSender.sendMessage(account.toString());
            }
            else if (args[0].equalsIgnoreCase("set"))
            {
                if (args.length == 2)
                {
                    int amount = Integer.parseInt(args[1]);
                    Account account = economy.getAccount(((Player) commandSender).getUniqueId());
                    account.setBalance(amount);
                    account.save();
                    commandSender.sendMessage(account.toString());
                }
                else if (args.length == 3)
                {
                    //TODO: Find a better way to do this, I would use getOfflinePlayer but lookup by name is deprecated
                    Optional<OfflinePlayer> offlinePlayer = Arrays.asList(economy.getServer().getOfflinePlayers()).stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findFirst();
                    if (offlinePlayer.isPresent()) {
                        UUID uuid = offlinePlayer.get().getUniqueId();
                        int amount = Integer.parseInt(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(amount);
                        account.save();
                        commandSender.sendMessage(account.toString());
                    } else {
                        commandSender.sendMessage("Could not find player by the name of " + args[1]);
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("give"))
            {
                if (args.length == 3)
                {
                    //TODO: Find a better way to do this, I would use getOfflinePlayer but lookup by name is deprecated
                    Optional<OfflinePlayer> offlinePlayer = Arrays.asList(economy.getServer().getOfflinePlayers()).stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findFirst();
                    if (offlinePlayer.isPresent()) {
                        UUID uuid = offlinePlayer.get().getUniqueId();
                        int amount = Integer.parseInt(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(account.getBalance() + amount);
                        account.save();
                        commandSender.sendMessage(account.toString());
                    } else {
                        commandSender.sendMessage("Could not find player by the name of " + args[1]);
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("take"))
            {
                if (args.length == 3)
                {
                    //TODO: Find a better way to do this, I would use getOfflinePlayer but lookup by name is deprecated
                    Optional<OfflinePlayer> offlinePlayer = Arrays.asList(economy.getServer().getOfflinePlayers()).stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findFirst();
                    if (offlinePlayer.isPresent()) {
                        UUID uuid = offlinePlayer.get().getUniqueId();
                        int amount = Integer.parseInt(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(account.getBalance() - amount);
                        account.save();
                        commandSender.sendMessage(account.toString());
                    } else {
                        commandSender.sendMessage("Could not find player by the name of " + args[1]);
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("send"))
            {
                commandSender.sendMessage("Not implemented");
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                commandSender.sendMessage("Not implemented");
            }
            else if (args[0].equalsIgnoreCase("help"))
            {
                commandSender.sendMessage("Not implemented");
            }
            else
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
