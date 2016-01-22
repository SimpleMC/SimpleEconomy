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
            if (args[0].equalsIgnoreCase("get"))
            {
                Account account = economy.getAccount(((Player) commandSender).getUniqueId());
                commandSender.sendMessage(economy.formatPhrase("balance.get", account.getBalance()));
            }
            else if (args[0].equalsIgnoreCase("set"))
            {
                if (args.length == 2)
                {
                    double amount = Double.parseDouble(args[1]);
                    Account account = economy.getAccount(((Player) commandSender).getUniqueId());
                    account.setBalance(amount);
                    account.save();
                    commandSender.sendMessage(economy.formatPhrase("balance.set.self", account.getBalance()));
                }
                else if (args.length == 3)
                {
                    //TODO: Find a better way to do this, I would use getOfflinePlayer but lookup by name is deprecated
                    Optional<OfflinePlayer> offlinePlayer = Arrays.asList(economy.getServer().getOfflinePlayers()).stream().filter(x -> x.getName().equalsIgnoreCase(args[1])).findFirst();
                    if (offlinePlayer.isPresent()) {
                        UUID uuid = offlinePlayer.get().getUniqueId();
                        double amount = Double.parseDouble(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(amount);
                        account.save();
                        commandSender.sendMessage(economy.formatPhrase("balance.set.other", offlinePlayer.get().getName(), account.getBalance()));
                    } else {
                        commandSender.sendMessage(economy.formatPhrase("error.player.notfound", args[1]));
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
                        double amount = Double.parseDouble(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(account.getBalance() + amount);
                        account.save();
                        commandSender.sendMessage(economy.formatPhrase("balance.give", amount, offlinePlayer.get().getName()));
                    } else {
                        commandSender.sendMessage(economy.formatPhrase("error.player.notfound", args[1]));
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
                        double amount = Double.parseDouble(args[2]);
                        Account account = economy.getAccount(uuid);
                        account.setBalance(account.getBalance() - amount);
                        account.save();
                        commandSender.sendMessage(economy.formatPhrase("balance.take", amount, offlinePlayer.get().getName()));
                    } else {
                        commandSender.sendMessage(economy.formatPhrase("error.player.notfound", args[1]));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("send"))
            {
                commandSender.sendMessage("Not implemented");
            }
            else if (args[0].equalsIgnoreCase("top"))
            {
                HashMap<UUID, Double> top = economy.getDatabaseManager().getTop(10);
                top.entrySet().stream().map(x -> {
                    String name = economy.getServer().getOfflinePlayer(x.getKey()).getName();
                    return String.format("%s - %f", name, x.getValue());
                }).forEach(commandSender::sendMessage);
            }
            else if (args[0].equalsIgnoreCase("help"))
            {
                commandSender.sendMessage(economy.formatPhrase("help"));
            }
            else
            {
                return false;
            }
            return true;
        }
        Account account = economy.getAccount(((Player) commandSender).getUniqueId());
        commandSender.sendMessage(economy.formatPhrase("balance.get", account.getBalance()));
        return true;
    }
}
