package net.teamcarbon.carbonnews.listeners;

import net.teamcarbon.carbonnews.CarbonNews;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerListeners implements Listener {

	CarbonNews plugin;
	public PlayerListeners(CarbonNews p) { plugin = p;}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		boolean np = !e.getPlayer().hasPlayedBefore();
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("welcomeMessage." + (np ? "newPlayer" : "returnPlayer"));
		if (cs.getBoolean("enabled", false)) {
			final boolean rp = cs.getBoolean("requirePermission", false);
			final List<String> msgs = cs.getStringList("messageLines");
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					for (String msg : msgs) { CarbonNews.sendFormatted(e.getPlayer(), msg, rp, "welcome"); }
				}
			}, cs.getLong("delaySeconds", 2L) * 20L);
		}
	}

}
