package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import net.teamcarbon.carbonnews.utils.BroadcastTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CarbonNewsToggleCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsToggleCommand(CarbonNews p) { plugin = p; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("carbonnews.set.enabled")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "You need to specify a message set!");
			if (sender.hasPermission("carbonnews.list.sets")) plugin.listAvailbleGroups(sender);
			return true;
		}
		if (!BroadcastTask.isTask(args[0])) {
			sender.sendMessage(ChatColor.RED + "Couldn't find the set specified: " + args[0]);
			if (sender.hasPermission("carbonnews.list.sets"))
				plugin.listAvailbleGroups(sender);
			return true;
		}
		BroadcastTask bt = BroadcastTask.getTask(args[0]);
		if (bt == null) {
			sender.sendMessage(ChatColor.RED + "Couldn't find the set specified: " + args[0]);
			if (sender.hasPermission("carbonnews.list.sets"))
				plugin.listAvailbleGroups(sender);
			return true;
		}
		if (bt.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "This list is empty! You must add messages to it before enabling it!");
			return true;
		}
		if (args.length > 1 && isBoolean(args[1])) bt.setEnabled(toBoolean(args[1])); else bt.setEnabled(!bt.isEnabled());
		sender.sendMessage(ChatColor.AQUA + "Message set " + bt.getSetName() + (bt.isEnabled() ? ChatColor.GREEN + " enabled" : ChatColor.RED + " disabled"));
		return true;
	}

	private boolean isBoolean(String query) {
		return (isInteger(query) && Integer.parseInt(query) > 0) ||
				CarbonNews.anyEq(query, "on", "off", "1", "0", "true", "false", "enabled", "disabled", "enable", "disable",
						"allow", "deny", "yes", "no", "y", "n", "agree", "disagree");
	}
	private boolean toBoolean(String query) {
		return isInteger(query) && Integer.parseInt(query) > 0 ||
				isBoolean(query) && CarbonNews.anyEq(query, "on", "1", "true", "enabled", "enable", "allow", "yes", "y", "agree");
	}
	private boolean isInteger(String query) {
		try {
			Integer.parseInt(query);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}
