package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class CarbonNewsReloadCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsReloadCommand(CarbonNews p) { plugin = p; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("carbonnews.reload")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		plugin.reload();
		PluginDescriptionFile pd = plugin.getDescription();
		sender.sendMessage(ChatColor.AQUA + "Reloaded " + pd.getName() + " v" + pd.getVersion());
		return true;
	}
}
