package net.teamcarbon.carbonnews.utils;

import net.teamcarbon.carbonnews.CarbonNews;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("unused")
public class BroadcastTask extends BukkitRunnable {
	
	private CarbonNews plugin;

	private static List<BroadcastTask> tasks = new ArrayList<>();

	private List<String> normalMsgList, randomMsgList;
	private String setName, pre, pst;
	private int position = 0;
	private boolean enabled, random, perm, consoleClr;

	public BroadcastTask(CarbonNews p, String set) {
		plugin = p;
		setName = set;
		loadSet(true);
		tasks.add(this);
	}

	@Override
	public void run() {
		if (enabled) {
			if (isEmpty()) {
				setEnabled(false);
				stopBroadcasts();
			} else {
				if (position == 0 && random) Collections.shuffle(randomMsgList, new Random(System.nanoTime()));
				String msg = randomMsgList.get(position);
				msg = toFormatArray(pre, msg, pst);
				broadcastFormatted(msg, perm, getPerm());
				position = (position < 0 || position + 1 >= randomMsgList.size()) ? 0 : position + 1;
			}
		} else {
			stopBroadcasts();
		}
	}

	public void startBroadcasts() {
		try {
			runTaskTimer(plugin, getDelay() * 20L, getDelay() * 20L);
			plugin.getLogger().info("Started broadcast task for set: " + getSetName());
			enabled = true;
		} catch (Exception e) {
			plugin.getLogger().info("Broadcast task already started for set: " + getSetName());
		}
	}
	public void stopBroadcasts() {
		try { cancel(); } catch (Exception ignored) {}
		enabled = false;
		plugin.getLogger().info("Stopped broadcast task for set: " + getSetName());
	}
	public void restartBroadcasts() {
		try {
			stopBroadcasts();
			String name = getSetName();
			removeTask(this);
			BroadcastTask t = new BroadcastTask(plugin, name);
			t.startBroadcasts();
			enabled = true;
			plugin.getLogger().info("Restarted broadcast task for set: " + getSetName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		plugin.getConfig().set(path("setEnabled"), enabled);
		plugin.saveConfig();
		if (enabled) startBroadcasts(); else stopBroadcasts();
		plugin.getLogger().info("Set broadcast task " + (enabled ? "enabled" : "disabled") + " for set: " + getSetName());
	}
	public void setRandom(boolean random) {
		if (random != this.random) {
			this.random = random;
			position = 0;
			randomMsgList.clear();
			randomMsgList = new ArrayList<>(normalMsgList);
			if (random) Collections.shuffle(randomMsgList);
			plugin.getConfig().set(path("randomOrder"), random);
			plugin.saveConfig();
			restartTask();
		}
		plugin.getLogger().info("Set broadcast task random order " + (random ? "enabled" : "disabled") + " for set: " + getSetName());
	}
	public void setDelay(long delay) {
		restartTask();
		plugin.getConfig().set(path("delaySeconds"), delay);
		plugin.saveConfig();
		plugin.getLogger().info("Set broadcast task delay to " + delay + " seconds for set: " + getSetName());
	}
	public void setRequirePerms(boolean requirePerms) {
		this.perm = requirePerms;
		plugin.getConfig().set(path("requirePermission"), perm);
		plugin.saveConfig();
		plugin.getLogger().info("Set broadcast task require perm " + (perm ? "enabled" : "disabled") + " for set: " + getSetName());
	}
	public void setPrefix(String prefix) {
		pre = prefix;
		plugin.getConfig().set(path("prefix"), prefix);
		plugin.saveConfig();
		plugin.getLogger().info("Set broadcast task prefix to " + prefix + " for set: " + getSetName());
	}
	public void setPostfix(String postfix) {
		pst = postfix;
		plugin.getConfig().set(path("postfix"), postfix);
		plugin.saveConfig();
		plugin.getLogger().info("Set broadcast task postfix to " + postfix + " for set: " + getSetName());
	}
	public void setMessages(List<String> messages) {
		normalMsgList.clear();
		normalMsgList = new ArrayList<>(messages);
		position = 0;
		if (random) {
			randomMsgList.clear();
			randomMsgList = new ArrayList<>(normalMsgList);
			Collections.shuffle(randomMsgList);
		}
		plugin.getConfig().set(path("messages"), normalMsgList);
		plugin.saveConfig();
	}
	public void addMessage(String msg) {
		List<String> om = getMessages();
		om.add(msg);
		setMessages(om);
	}
	public void removeMessage(int msgId) {
		List<String> om = getMessages();
		om.remove(msgId);
		setMessages(om);
	}
	public void updateMessage(int msgId, String msg) {
		List<String> om = getMessages();
		om.set(msgId, msg);
		setMessages(om);
	}

	public String getSetName() {
		return setName;
	}
	public String getPerm() { return "carbonkit.news.receive." + setName; }
	public List<String> getMessages() {
		if (normalMsgList == null)
			return new ArrayList<>();
		return new ArrayList<>(normalMsgList);
	}
	public String getMessage(int msgId) {
		if (msgId > size())
			return null;
		return (normalMsgList.get(msgId));
	}
	public boolean isEnabled() { return enabled; }
	public boolean isRandom() { return random; }
	public boolean requirePerms() { return perm; }
	public boolean isEmpty() { return normalMsgList.isEmpty(); }
	public int size() { return normalMsgList.size(); }
	public long getDelay() {
		ConfigurationSection setConf = plugin.getConfig().getConfigurationSection(path());
		long defDelay = plugin.getConfig().getLong("setDefault.delaySeconds", 60L);
		return setConf.getLong("delaySeconds", defDelay);
	}

	public String getPrefix() { return pre; }
	public String getPostfix() { return pst; }
	private String path() { return "MessageSets." + setName; }
	private String path(String postPath) { return "MessageSets." + setName + "." + postPath; }
	public String toFormatArray(String ... jsonObjects) {
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
	private void broadcastFormatted(String msg, boolean needsPerm, String ... perms) {
		for (Player pl : Bukkit.getOnlinePlayers()) { CarbonNews.sendFormatted(pl, msg, needsPerm, perms); }
	}

	private void loadSet(boolean allowCreate) {
		if (plugin.getConfig().contains(path())) {
			ConfigurationSection setConf = plugin.getConfig().getConfigurationSection(path());
			ConfigurationSection setDefaults = plugin.getConfig().getConfigurationSection("setDefaults");
			enabled = setConf.getBoolean("setEnabled", false);
			normalMsgList = new ArrayList<>(setConf.getStringList("messages"));
			random = setConf.getBoolean(("randomOrder"), setDefaults.getBoolean("randomOrder", false));
			perm = setConf.getBoolean(("requirePermission"), setDefaults.getBoolean("requirePermission", false));
			pre = setConf.getString(("prefix"), setDefaults.getString("prefix", "[{\"text\":\"\",\"hoverEvent\":"
					+ "{\"action\":\"show_text\",\"value\":{\"text\":\"News\"}},\"extra\":[{\"text\":\"[\",\"color\":"
					+ "\"dark_aqua\",\"bold\":true},{\"text\":\"!\",\"color\":\"aqua\",\"bold\":true},{\"text\":\"]"
					+ "    \",\"color\":\"dark_aqua\",\"bold\":true}]}]"));
			pst = setConf.getString(("postfix"), setDefaults.getString("postfix", ""));
			if (normalMsgList == null)
				normalMsgList = new ArrayList<>();
			if (size() < 1) {
				enabled = false;
				plugin.getLogger().warning("Message list for set " + setName + " is empty, disabling it");
			}
			randomMsgList = new ArrayList<>(normalMsgList);
		} else if (allowCreate) {
			plugin.getLogger().warning("Failed to find settings for message set '" + setName + "', creating it...");
			plugin.getConfig().set("MessageSets." + setName, plugin.getConfig().getConfigurationSection("setDefaults"));
			plugin.saveConfig();
			plugin.getLogger().info("Created message set '" + setName + "' with default values (setName disabled by default, no messages)");
			loadSet(false); // Reload it now that default values has been added to the set, shouldn't cause stack overflow...
		}
	}

	public static void disableAllTasks() { for (BroadcastTask task : tasks) task.stopBroadcasts(); }

	public static void startAllTasks() { for (BroadcastTask task : tasks) task.startBroadcasts(); }

	public static BroadcastTask getTask(String setName) {
		for (BroadcastTask bt : tasks)
			if (bt.getSetName().equalsIgnoreCase(setName))
				return bt;
		return null;
	}

	public static boolean isTask(String setName) { return getTask(setName) != null; }

	public static List<BroadcastTask> getTasks() { return new ArrayList<>(tasks); }

	public static void removeTask(BroadcastTask task) {
		task.stopBroadcasts();
		if (tasks.contains(task)) tasks.remove(task);
	}

	public static void removeAllTasks() { for (BroadcastTask task : getTasks()) { removeTask(task); } }

	public static void removeTask(String name) { if (getTask(name) != null) removeTask(getTask(name)); }

	public static int taskListSize() { return tasks.size(); }

	private void restartTask() {
		if (isEnabled()) restartBroadcasts();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof BroadcastTask)) return false;
		BroadcastTask bt = (BroadcastTask)obj;
		return new EqualsBuilder()
				.append(bt.getSetName(), getSetName())
				.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getSetName())
				.toHashCode();
	}
}
