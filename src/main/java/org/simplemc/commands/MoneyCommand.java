package org.simplemc.commands;

import com.sk89q.squirrelid.Profile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simplemc.Account;
import org.simplemc.SimpleEconomy;
import org.simplemc.utilities.StringUtilities;

import java.util.HashMap;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor
{
    protected final SimpleEconomy economy;

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
            try
            {
                SubCommand sub = SubCommand.valueOf(args[0].toUpperCase());
                isHandled = true;
                if (args.length >= sub.minArgs && args.length <= sub.maxArgs)
                {
                    sub.execute(economy, commandSender, args);
                }
                else
                {
                    commandSender.sendMessage(economy.formatPhrase("error.syntax.base", economy.formatPhrase("error.syntax." + sub.name().toLowerCase())));
                }
            }
            catch (IllegalArgumentException e)
            {
                commandSender.sendMessage(economy.formatPhrase("error.subcommand.notfound"));
            }
        }
        return isHandled;
    }

    protected static void sendBalance(SimpleEconomy economy, CommandSender commandSender, String name, Double amount)
    {
        Profile profile = economy.getProfileFromName(name);
        if (profile != null)
        {
            Account sender = economy.getAccount(((Player) commandSender).getUniqueId());
            Account reciver = economy.getAccount(profile.getUniqueId());
            if (sender.getBalance() - amount <= 0)
            {
                commandSender.sendMessage(economy.formatPhrase("error.balance.toolow"));
            }
            else if (sender.getUuid().equals(reciver.getUuid()))
            {
                commandSender.sendMessage(economy.formatPhrase("error.player.self"));
            }
            else
            {
                sender.setBalance(sender.getBalance() - amount);
                sender.save();
                reciver.setBalance(reciver.getBalance() + amount);
                reciver.save();
                commandSender.sendMessage(economy.formatPhrase("balance.send", amount, profile.getName()));
            }
        }
        else
        {
            commandSender.sendMessage(economy.formatPhrase("error.player.notfound", name));
        }

    }

    protected static void giveBalance(SimpleEconomy economy, CommandSender sender, String name, Double amount)
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
    }

    protected static void takeBalance(SimpleEconomy economy, CommandSender sender, String name, Double amount)
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
    }

    protected static void setBalance(SimpleEconomy economy, CommandSender sender, String name, Double amount)
    {
        if (name != null)
        {
            Profile profile = economy.getProfileFromName(name);
            if (profile != null)
            {
                Account account = economy.getAccount(profile.getUniqueId());
                account.setBalance(amount);
                account.save();
                sender.sendMessage(economy.formatPhrase("balance.set.other", profile.getName(), StringUtilities.formatDouble(account.getBalance())));
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
            sender.sendMessage(economy.formatPhrase("balance.set.self", StringUtilities.formatDouble(account.getBalance())));
        }
    }

    protected static void getBalance(SimpleEconomy economy, CommandSender sender, String name)
    {
        if (name == null)
        {
            Account account = economy.getAccount(((Player) sender).getUniqueId());
            sender.sendMessage(economy.formatPhrase("balance.get.self", StringUtilities.formatDouble(account.getBalance())));
        }
        else
        {
            Profile profile = economy.getProfileFromName(name);
            if (profile != null)
            {
                Account account = economy.getAccount(profile.getUniqueId());
                sender.sendMessage(economy.formatPhrase("balance.get.other", profile.getName(), StringUtilities.formatDouble(account.getBalance())));
            }
            else
            {
                sender.sendMessage(economy.formatPhrase("error.player.notfound", name));
            }
        }
    }
}

enum SubCommand
{
    GET(1, 2)
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    if (args.length == 2)
                    {
                        MoneyCommand.getBalance(economy, sender, args[1]);
                    }
                    else
                    {
                        MoneyCommand.getBalance(economy, sender, null);
                    }
                }
            },
    SET(2, 3)
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    if (args.length == 2)
                    {
                        MoneyCommand.setBalance(economy, sender, null, Double.parseDouble(args[1]));
                    }
                    else if (args.length == 3)
                    {
                        MoneyCommand.setBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                    }
                }
            },
    GIVE(3,3)
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                        MoneyCommand.giveBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    TAKE(3, 3)
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                        MoneyCommand.takeBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    SEND(3, 3)
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                        MoneyCommand.sendBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    TOP
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    HashMap<UUID, Double> top = economy.getDatabaseManager().getTop(10);
                    top.entrySet().stream().map(x -> {
                        Profile profile = economy.getProfileFromUUID(x.getKey());
                        if (profile != null)
                        {
                            return String.format("%s - %s", profile.getName(), StringUtilities.formatDouble(x.getValue()));
                        }
                        else
                        {
                            return "";
                        }
                    }).filter(x -> !x.isEmpty()).forEach(sender::sendMessage);
                }
            },
    HELP
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    sender.sendMessage(economy.formatPhrase("help"));
                }
            };

    final int minArgs; // (minimum) number of arguments subcommand expects
    final int maxArgs; // (maximum) number of arguments subcommand expects

    /**
     * Create subcommand
     *
     * @param minArgs (minimun) number of arguments subcommand expects
     * @param maxArgs (maximum) number of arguments subcommand expects
     */
    SubCommand(int minArgs, int maxArgs)
    {
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    /**
     * Default to no required arguments
     */
    SubCommand()
    {
        this(1, 1);
    }

    public abstract void execute(SimpleEconomy economy, CommandSender sender, String... args);
}