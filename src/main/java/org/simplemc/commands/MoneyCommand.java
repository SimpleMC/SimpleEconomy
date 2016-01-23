package org.simplemc.commands;

import com.sk89q.squirrelid.Profile;
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
        boolean isHandled = false;
        if (args.length >= 1)
        {
            SubCommands subCommands;
            try
            {
                subCommands = SubCommands.valueOf(args[0].toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                commandSender.sendMessage(economy.formatPhrase("error.subcommand.notfound"));
                return false;
            }

            switch (subCommands)
            {
                case GET:
                    if (args.length == 2)
                    {
                        getBalance(commandSender, args[1]);
                    }
                    else
                    {
                        getBalance(commandSender, null);
                    }
                    isHandled = true;
                    break;
                case SET:
                    if (args.length == 2)
                    {
                        setBalance(commandSender, null, Double.parseDouble(args[1]));
                    }
                    else if (args.length == 3)
                    {
                        setBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                    isHandled = true;
                    break;
                case GIVE:
                    if (args.length == 3)
                    {
                        giveBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                    isHandled = true;
                    break;
                case TAKE:
                    if (args.length == 3)
                    {
                        takeBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                    isHandled = true;
                    break;
                case SEND:
                    if (args.length == 3)
                    {
                        sendBalance(commandSender, args[1], Double.parseDouble(args[2]));
                    }
                    isHandled = true;
                    break;
                case TOP:
                    HashMap<UUID, Double> top = economy.getDatabaseManager().getTop(10);
                    top.entrySet().stream().map(x -> {
                        Profile profile = economy.getProfileFromUUID(x.getKey());
                        if (profile != null) {
                            return String.format("%s - %f", profile.getName(), x.getValue());
                        } else {
                            return "";
                        }
                    }).filter(x -> !x.isEmpty()).forEach(commandSender::sendMessage);
                    isHandled = true;
                    break;
                case HELP:
                    commandSender.sendMessage(economy.formatPhrase("help"));
                    isHandled = true;
                    break;
                default:
                    isHandled = false;
                    break;
            }

        }
        if (!isHandled)
        {
            commandSender.sendMessage(economy.formatPhrase("error.subcommand.notfound"));
        }
        return isHandled;
    }

    private boolean sendBalance(CommandSender commandSender, String name, Double amount)
    {
        Profile profile = economy.getProfileFromName(name);
        if (profile != null)
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

            Account reciver = economy.getAccount(profile.getUniqueId());
            if (sender.getUuid().equals(reciver.getUuid()))
            {
                commandSender.sendMessage(economy.formatPhrase("error.player.self"));
                return true;
            }
            sender.setBalance(sender.getBalance() - amount);
            sender.save();
            reciver.setBalance(reciver.getBalance() + amount);
            reciver.save();
            commandSender.sendMessage(economy.formatPhrase("balance.send", amount, profile.getName()));
        }
        else
        {
            commandSender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }
        return true;
    }

    private boolean giveBalance(CommandSender sender, String name, Double amount)
    {
        Profile profile = economy.getProfileFromName(name);
        if (profile != null)
        {
            Account account = economy.getAccount(profile.getUniqueId());
            account.setBalance(account.getBalance() + amount);
            account.save();
            sender.sendMessage(economy.formatPhrase("balance.give", amount, profile.getName()));
        }
        else
        {
            sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }
        return true;
    }

    private boolean takeBalance(CommandSender sender, String name, Double amount)
    {
        Profile profile = economy.getProfileFromName(name);
        if (profile != null)
        {
            Account account = economy.getAccount(profile.getUniqueId());
            account.setBalance(account.getBalance() - amount);
            account.save();
            sender.sendMessage(economy.formatPhrase("balance.take", amount, profile.getName()));
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
            Profile profile = economy.getProfileFromName(name);
            if (profile != null)
            {
                Account account = economy.getAccount(profile.getUniqueId());
                account.setBalance(amount);
                account.save();
                sender.sendMessage(economy.formatPhrase("balance.set.other", profile.getName(), account.getBalance()));
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

    private boolean getBalance(CommandSender sender, String name)
    {
        if (name == null)
        {
            Account account = economy.getAccount(((Player) sender).getUniqueId());
            sender.sendMessage(economy.formatPhrase("balance.get.self", account.getBalance()));
        }
        else
        {
            Profile profile = economy.getProfileFromName(name);
            if (profile != null)
            {
                Account account = economy.getAccount(profile.getUniqueId());
                sender.sendMessage(economy.formatPhrase("balance.get.other", profile.getName(), account.getBalance()));
            }
            else
            {
                sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
            }
        }
        return true;
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
