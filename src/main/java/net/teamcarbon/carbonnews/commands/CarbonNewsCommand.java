package net.teamcarbon.carbonnews.commands;

import net.teamcarbon.carbonnews.CarbonNews;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CarbonNewsCommand implements CommandExecutor {

	CarbonNews plugin;
	public CarbonNewsCommand(CarbonNews p) { plugin = p; }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1 || CarbonNews.anyEq(args[0], "help", "h", "?")) {
			return fwd(sender, "carbonnewshelp", args);
		} else if (CarbonNews.anyEq(args[0], "toggle", "t")) {
			return fwd(sender, "carbonnewstoggle", args);
		} else if (CarbonNews.anyEq(args[0], "list", "l")) {
			return fwd(sender, "carbonnewslist", args);
		} else if (CarbonNews.anyEq(args[0], "listsets", "listset", "lists", "ls")) {
			return fwd(sender, "carbonnewslist s", args);
		} else if (CarbonNews.anyEq(args[0], "listmessages", "listmessage", "listmsgs", "listmsg", "listm", "lm")) {
			return fwd(sender, "carbonnewslist m", args);
		} else if (CarbonNews.anyEq(args[0], "setinfo", "info", "si", "i")) {
			return fwd(sender, "carbonnewsinfo", args);
		} else if (CarbonNews.anyEq(args[0], "reload", "rl", "r")) {
			return fwd(sender, "carbonnewsreload", args);
		}
		return fwd(sender, "carbonnewshelp", args);
	}

	private boolean fwd(CommandSender s, String c, String[] args) { return plugin.getServer().dispatchCommand(s, c + otherArgs(args)); }

	private String otherArgs(String[] args) {
		if (args == null) return "";
		String[] others = Arrays.copyOfRange(args, 1, args.length-1);
		StringBuilder sb = new StringBuilder();
		for (String o : others) { if (!o.isEmpty()) { sb.append(" " + o); } }
		return sb.toString();
	}
}
