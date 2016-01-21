package org.simplemc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simplemc.SimpleEconomy;
import org.simplemc.api.Account;

import java.util.Arrays;

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
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("get")) {
                Account account = economy.getApi().getAccount(((Player) commandSender).getUniqueId());
                commandSender.sendMessage(account.toString());
            } else if (args[0].equalsIgnoreCase("set")) {
                commandSender.sendMessage("Not implemented");
            } else if (args[0].equalsIgnoreCase("give")) {
                commandSender.sendMessage("Not implemented");
            } else if (args[0].equalsIgnoreCase("take")) {
                commandSender.sendMessage("Not implemented");
            } else if (args[0].equalsIgnoreCase("top")) {
                commandSender.sendMessage("Not implemented");
            } else if (args[0].equalsIgnoreCase("help")) {
                commandSender.sendMessage("Not implemented");
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
