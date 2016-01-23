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
                if (!commandSender.hasPermission(sub.permission)) {
                    commandSender.sendMessage(economy.formatPhrase("error.nopermission"));
                }
                else if (args.length >= sub.minArgs && args.length <= sub.maxArgs)
                {
                    sub.execute(economy, commandSender, args);
                }
                else
                {
                    commandSender.sendMessage(economy.formatPhrase("error.syntax.base", economy.formatPhrase("error.syntax." + sub.name().toLowerCase())));
                }
            }
            catch (NumberFormatException e)
            {
                commandSender.sendMessage(economy.formatPhrase("error.input.notanumber"));
            }
            catch (IllegalArgumentException e)
            {
                commandSender.sendMessage(economy.formatPhrase("error.subcommand.notfound"));
            }
        }
        if (!isHandled){
            SubCommand.GET.execute(economy, commandSender, args);
        }
        return isHandled;
    }

    protected static void sendBalance(SimpleEconomy economy, CommandSender commandSender, String name, Double amount)
    {
        Profile profile = economy.getProfileFromName(name);
        Account receiver = null;
        if (profile != null)
        {
            receiver = economy.getAccount(profile.getUniqueId());
        }

        if (receiver != null)
        {
            Account sender = economy.getAccount(((Player) commandSender).getUniqueId());
            if (sender.getBalance() - amount <= 0)
            {
                commandSender.sendMessage(economy.formatPhrase("error.balance.toolow"));
            }
            else if (sender.getUuid().equals(receiver.getUuid()))
            {
                commandSender.sendMessage(economy.formatPhrase("error.player.self"));
            }
            else if (amount <= 0)
            {
                commandSender.sendMessage(economy.formatPhrase("error.input.toolow"));
            }
            else
            {
                sender.setBalance(sender.getBalance() - amount);
                sender.save();
                receiver.setBalance(receiver.getBalance() + amount);
                receiver.save();
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
        Account account = null;
        if (profile != null)
        {
            account = economy.getAccount(profile.getUniqueId());
        }

        if (account != null)
        {
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
        Account account = null;
        if (profile != null)
        {
            account = economy.getAccount(profile.getUniqueId());
        }

        if (account != null)
        {
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
            Account account = null;
            if (profile != null)
            {
                account = economy.getAccount(profile.getUniqueId());
            }

            if (account != null)
            {
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
            Account account = null;
            if (profile != null)
            {
                account = economy.getAccount(profile.getUniqueId());
            }

            if (account != null)
            {
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
    GET(1, 2, "simple.economy.money.get")
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
    SET(2, 3, "simple.economy.money.set")
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
    GIVE(3, 3, "simple.economy.money.give")
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    MoneyCommand.giveBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    TAKE(3, 3, "simple.economy.money.take")
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    MoneyCommand.takeBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    SEND(3, 3, "simple.economy.money.send")
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    MoneyCommand.sendBalance(economy, sender, args[1], Double.parseDouble(args[2]));
                }
            },
    TOP("simple.economy.money.top")
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
    HELP("simple.economy.money.help")
            {
                @Override
                public void execute(SimpleEconomy economy, CommandSender sender, String... args)
                {
                    sender.sendMessage(economy.formatPhrase("help"));
                }
            };

    final int minArgs; // (minimum) number of arguments subcommand expects
    final int maxArgs; // (maximum) number of arguments subcommand expects
    final String permission;
    /**
     * Create subcommand
     *  @param minArgs (minimun) number of arguments subcommand expects
     * @param maxArgs (maximum) number of arguments subcommand expects
     * @param permission Permissions node for this command.
     */
    SubCommand(int minArgs, int maxArgs, String permission)
    {
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.permission = permission;
    }

    /**
     * Default to no required arguments
     * @param permission Permissions node for this command.
     */
    SubCommand(String permission)
    {
        this(1, 1, permission);
    }

    public abstract void execute(SimpleEconomy economy, CommandSender sender, String... args);
}