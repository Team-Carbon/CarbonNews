package net.teamcarbon.carbonnews;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.teamcarbon.carbonnews.commands.*;
import net.teamcarbon.carbonnews.listeners.PlayerListeners;
import net.teamcarbon.carbonnews.utils.BroadcastTask;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class CarbonNews extends JavaPlugin {

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		saveDefaultConfig();

		pm.registerEvents(new PlayerListeners(this), this);

		getServer().getPluginCommand("carbonnews").setExecutor(new CarbonNewsCommand(this));
		getServer().getPluginCommand("carbonnewshelp").setExecutor(new CarbonNewsHelpCommand(this));
		getServer().getPluginCommand("carbonnewsinfo").setExecutor(new CarbonNewsInfoCommand(this));
		getServer().getPluginCommand("carbonnewslist").setExecutor(new CarbonNewsListCommand(this));
		getServer().getPluginCommand("carbonnewsreload").setExecutor(new CarbonNewsReloadCommand(this));
		getServer().getPluginCommand("carbonnewstoggle").setExecutor(new CarbonNewsToggleCommand(this));

		reload();
	}

	public void onDisable() {
		BroadcastTask.disableAllTasks();
		BroadcastTask.removeAllTasks();
	}

	public void reload() {
		reloadConfig();
		reloadTasks();
	}

	public void reloadTasks() {
		BroadcastTask.disableAllTasks();
		BroadcastTask.removeAllTasks();
		Set<String> keys = getConfig().getConfigurationSection("MessageSets").getKeys(false);
		for (String key : keys) {
			BroadcastTask bt = new BroadcastTask(this, key);
			if (bt.isEnabled()) bt.startBroadcasts();
		}
	}

	public static void sendFormatted(CommandSender cs, String msg, boolean needsPerm, String ... perms) {
		if (!needsPerm || anyPerm(cs, perms)) {
			if (cs instanceof Player) {
				((Player) cs).spigot().sendMessage(ComponentSerializer.parse(msg));
			} else {
				BaseComponent[] bc = ComponentSerializer.parse(msg);
				cs.sendMessage(ComponentSerializer.toString(bc));
			}
		}
	}

	public static String toFormatArray(String ... jsonObjects) {
		if (jsonObjects.length < 1) return "{\"text\":\"\"}";
		String array = "[{\"text\":\"\",\"extra\":" + jsonObjects[0] + "}";
		for (int i = 1; i < jsonObjects.length; i++) {
			String jo = jsonObjects[i];
			if (jo != null && !jo.isEmpty()) {
				array += ",{\"text\":\"\",\"extra\":" + jo + "}";
			}
		}
		array += "]";
		return array;
	}

	public static boolean anyPerm(CommandSender cs, String ... perms) {
		for (String perm : perms) { if (cs.hasPermission(perm)) return true; } return false;
	}

	public static boolean anyEq(String s, String ... strings) {
		for (String string : strings) { if (string.equalsIgnoreCase(s)) return true; } return false;
	}

	public void printHeader(CommandSender s) {
		printHeader(s, getDescription().getName(), "v." + getDescription().getVersion());
	}
	public void printHeader(CommandSender s, String title) { printHeader(s, title, ""); }
	public void printHeader(CommandSender s, String title, String subtitle) {
		s.sendMessage(titleClr() + "=====[ " + ChatColor.GOLD + title + ChatColor.YELLOW
				+ (subtitle.isEmpty() ? "" : " " + subtitle) + titleClr() + " ]=====");
	}
	public void printFooter(CommandSender s) { s.sendMessage(titleClr() + "=================="); }
	public void printDivider(CommandSender s) { s.sendMessage(titleClr() + "=----------------="); }

	public void listAvailbleGroups(CommandSender sender) {
		if (BroadcastTask.taskListSize() < 1) {
			sender.sendMessage(ChatColor.AQUA + "Available sets: " + ChatColor.GRAY + "No sets");
			return;
		}
		List<BroadcastTask> tasks = BroadcastTask.getTasks();
		String gList = (tasks.get(0).isEnabled() ? ChatColor.GREEN : ChatColor.RED) + tasks.get(0).getSetName();
		for (BroadcastTask bt : tasks.subList(1, tasks.size()-1))
			gList += ChatColor.GRAY + ", " + (bt.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + bt.getSetName();
		sender.sendMessage(ChatColor.AQUA + "Available sets: " + gList);
	}

	private String titleClr() { return ChatColor.GOLD + "" + ChatColor.BOLD; }

}
