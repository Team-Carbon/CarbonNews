package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import net.teamcarbon.carbonnews.utils.BroadcastTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CarbonNewsListCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsListCommand(CarbonNews p) { plugin = p; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!CarbonNews.anyPerm(sender, "carbonnews.list.sets", "carbonnews.list.messages")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		if (args.length < 1) {
			if (sender.hasPermission("carbonnews.list.sets"))
				sender.sendMessage(ChatColor.RED + "/cnlist s - Lists message sets");
			if (sender.hasPermission("carbonnews.list.messages"))
				sender.sendMessage(ChatColor.RED + "/cnlist m [set] - Lists messages in a set");
			return true;
		}
		if (CarbonNews.anyEq(args[0], "sets", "set", "s")) {
			if (!sender.hasPermission("carbonnews.list.sets")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
			}
			plugin.listAvailbleGroups(sender);
		} else if (CarbonNews.anyEq(args[0], "messages", "message", "msgs", "msg", "m")) {
			if (!sender.hasPermission("carbonnews.list.messages")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "You need to specify a message set!");
				if (sender.hasPermission("carbonnews.list.sets")) plugin.listAvailbleGroups(sender);
				return true;
			}
			if (!BroadcastTask.isTask(args[1])) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the set specified: " + args[0]);
				if (sender.hasPermission("carbonnews.list.sets"))
					plugin.listAvailbleGroups(sender);
				return true;
			}
			BroadcastTask bt = BroadcastTask.getTask(args[1]);
			if (bt == null) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the set specified: " + args[0]);
				if (sender.hasPermission("carbonnews.list.sets"))
					plugin.listAvailbleGroups(sender);
				return true;
			}
			if (bt.isEmpty()) {
				sender.sendMessage(ChatColor.GRAY + "This set has no messages!");
				if (sender.hasPermission("carbonnews.addmessage"))
					sender.sendMessage(ChatColor.GRAY + "Add a message with /cnaddm " + bt.getSetName() + " [msg]");
				return true;
			}
			for (int i = 0; i < bt.getMessages().size(); i++) {
				String num = "[{\"text\":\"" + i + ": \",\"color\":\"aqua\",\"bold\":true}]";
				String msg = bt.getMessage(i);
				CarbonNews.sendFormatted(sender, CarbonNews.toFormatArray(num, msg), false, "");
			}
		}
		return true;
	}
}
