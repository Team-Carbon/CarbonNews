package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import net.teamcarbon.carbonnews.utils.BroadcastTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CarbonNewsInfoCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsInfoCommand(CarbonNews p) { plugin = p; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("carbonnews.info")) {
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
		plugin.printHeader(sender, "Info:", bt.getSetName());
		sender.sendMessage(ChatColor.AQUA + "Broadcasting: " + ChatColor.GRAY + (bt.isEnabled()?ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
		sender.sendMessage(ChatColor.AQUA + "Message Count: " + ChatColor.GRAY + bt.size());
		sender.sendMessage(ChatColor.AQUA + "Interval Delay (seconds) : " + ChatColor.GRAY + plugin.getConfig().getLong("MessageSets." + bt.getSetName() + ".delaySeconds", 60));
		sender.sendMessage(ChatColor.AQUA + "Random: " + ChatColor.GRAY + bt.isRandom());
		sender.sendMessage(ChatColor.AQUA + "Requires Permission: " + ChatColor.GRAY + bt.requirePerms() + ChatColor.GRAY + " (carbonnews.receive." + bt.getSetName() + ")");

		String pre = "[{\"text\":\"Prefix: \",\"color\":\"aqua\",\"bold\":true}]";
		String post = "[{\"text\":\"Postfix: \",\"color\":\"aqua\",\"bold\":true}]";
		CarbonNews.sendFormatted(sender, CarbonNews.toFormatArray(pre, bt.getPrefix()), false, "");
		CarbonNews.sendFormatted(sender, CarbonNews.toFormatArray(post, bt.getPostfix()), false, "");

		//sender.sendMessage(ChatColor.AQUA + "Prefix: " + ChatColor.GRAY + bt.getPrefix());
		//sender.sendMessage(ChatColor.AQUA + "Postfix: " + ChatColor.GRAY + bt.getPostfix());

		plugin.printFooter(sender);
		return true;
	}
}
