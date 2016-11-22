package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import net.teamcarbon.carbonnews.utils.BroadcastTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class CarbonNewsHelpCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsHelpCommand(CarbonNews p) { plugin = p; }

	private String[] allPerms = {
			"carbonnews.set.enabled", "carbonnews.list.sets", "carbonnews.list.messages", "carbonnews.set.delay",
			"carbonnews.set.requireperms", "carbonnews.set.prefix", "carbonnews.set.postfix", "carbonnews.set.random",
			"carbonnews.set.sendtoconsole", "carbonnews.set.colorconsole", "carbonnews.set.sendtoplayer"
	};
	private String[] allSetPerms = {
			"carbonnews.set.enabled", "carbonnews.set.delay", "carbonnews.set.requireperms", "carbonnews.set.prefix",
			"carbonnews.set.postfix", "carbonnews.set.random", "carbonnews.set.sendtoconsole",
			"carbonnews.set.colorconsole", "carbonnews.set.sendtoplayer"
	};

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("carbonnews.help") ||
				(!sender.hasPermission("carbonnews.info") && !CarbonNews.anyPerm(sender, allPerms))) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		plugin.printHeader(sender);
		if (sender.hasPermission("carbonnews.info")) {
			int ts = BroadcastTask.taskListSize(), enabled = 0;
			for (BroadcastTask bt : BroadcastTask.getTasks()) { if (bt.isEnabled()) enabled++; }
			sender.sendMessage(ChatColor.DARK_AQUA + "" + ts + " sets currently loaded");
			sender.sendMessage(ChatColor.DARK_AQUA + "" + enabled + "/" + ts + " sets enabled");
			if (CarbonNews.anyPerm(sender, allPerms))
				plugin.printDivider(sender);
		}
		if (CarbonNews.anyPerm(sender, allPerms)) {
			sf(sender, "toggle [set] <on|off>", "Toggles a set on or off", "carbonnews.set.enabled");
			sf(sender, "reload", "Reloads CarbonNews", "carbonnews.reload");
			sf(sender, "info <set>", "Lists info about a message set", "carbonnews.setinfo");
			sf(sender, "list [sets|messages [set]]", "Lists sets or messages", "carbonnews.list");
			sf(sender, "addmessage [set] [msg]", "Adds a message to a set", "carbonnews.addmessage");
			sf(sender, "removemessage [set] [msgID]", "Removes a message from a set", "carbonnews.removemessage");
			sf(sender, "setmessage [set] [msgID] [newMsg]", "Sets a message in a set", "carbonnews.setmessage");
			sf(sender, "addset [set]", "Adds a blank message set", "carbonnews.addset");
			sf(sender, "removeset [set]", "Removes a message set", "carbonnews.removeset");
			sf(sender, "set [set] [setting] [value]", "Sets message set options", allSetPerms);
		}
		plugin.printFooter(sender);
		return true;
	}

	private void sf(CommandSender s, String a, String d, String ... p) {
		String f = ChatColor.AQUA + "/cn %s " + ChatColor.DARK_AQUA + "- %s";
		if (CarbonNews.anyPerm(s, p)) { s.sendMessage(String.format(Locale.ENGLISH, f, a, d)); }
	}
}
