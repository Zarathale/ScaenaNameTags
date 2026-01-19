package cc.scaenacraft.nametags.command;

import cc.scaenacraft.nametags.ScaenaNameTagsPlugin;
import cc.scaenacraft.nametags.service.RecipeUnlockService;
import cc.scaenacraft.nametags.text.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class NameTagsCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_UNLOCK = "scaena.nametags.unlock";
    private static final String PERM_GIVE = "scaena.nametags.give";

    private final ScaenaNameTagsPlugin plugin;
    private final RecipeUnlockService unlockService;
    private final Messages messages;
    private final boolean requirePermission;
    @SuppressWarnings("unused")
    private final boolean defaultAllow;

    public NameTagsCommand(
            ScaenaNameTagsPlugin plugin,
            RecipeUnlockService unlockService,
            Messages messages,
            boolean requirePermission,
            boolean defaultAllow
    ) {
        this.plugin = plugin;
        this.unlockService = unlockService;
        this.messages = messages;
        this.requirePermission = requirePermission;
        this.defaultAllow = defaultAllow;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Console can only use: /nametags give <player>
        if (!(sender instanceof Player player)) {
            if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
                return handleGive(sender, args);
            }
            sender.sendMessage("Console usage: /" + label + " give <player>");
            return true;
        }

        // /nametags  (unlock)
        if (args.length == 0) {
            return handleUnlock(player);
        }

        final String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("unlock")) {
            return handleUnlock(player);
        }

        if (sub.equals("give")) {
            return handleGive(player, args);
        }

        // Unknown subcommand -> treat as help
        messages.sendHelp(player);
        return true;
    }

    private boolean handleUnlock(Player player) {
        if (requirePermission && !player.hasPermission(PERM_UNLOCK)) {
            messages.sendNoPermission(player);
            return true;
        }

        final RecipeUnlockService.Result result = unlockService.unlockFor(player);

        switch (result) {
            case UNLOCKED -> messages.sendUnlocked(player);
            case ALREADY_UNLOCKED -> messages.sendAlreadyUnlocked(player);
            case MISSING_RECIPE -> messages.sendMissingRecipe(player);
        }

        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (requirePermission && !sender.hasPermission(PERM_GIVE)) {
            if (sender instanceof Player p) messages.sendNoPermission(p);
            else sender.sendMessage("This request requires clearance.");
            return true;
        }

        if (args.length < 2) {
            if (sender instanceof Player p) messages.sendGiveUsage(p);
            else sender.sendMessage("Usage: /nametags give <player>");
            return true;
        }

        final Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("Player not found: " + args[1]);
            return true;
        }

        final RecipeUnlockService.Result result = unlockService.unlockFor(target);

        // Tell the target (theatrical)
        switch (result) {
            case UNLOCKED -> messages.sendUnlocked(target);
            case ALREADY_UNLOCKED -> messages.sendAlreadyUnlocked(target);
            case MISSING_RECIPE -> messages.sendMissingRecipe(target);
        }

        // Tell the sender (simple)
        sender.sendMessage("Name tags recipe status for " + target.getName() + ": " + result.name());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> out = new ArrayList<>();

        if (args.length == 1) {
            final String start = args[0].toLowerCase(Locale.ROOT);
            if ("unlock".startsWith(start)) out.add("unlock");
            if ("give".startsWith(start) && sender.hasPermission(PERM_GIVE)) out.add("give");
            return out;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission(PERM_GIVE)) {
            final String start = args[1].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                final String name = p.getName();
                if (name.toLowerCase(Locale.ROOT).startsWith(start)) out.add(name);
            }
            return out;
        }

        return out;
    }
}
