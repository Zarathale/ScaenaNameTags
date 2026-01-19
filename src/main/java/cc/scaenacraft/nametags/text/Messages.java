package cc.scaenacraft.nametags.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class Messages {

    private final MiniMessage mm;

    // Palette:
    // Gold   #FECB00
    // Orange #EA7125
    // Gray   #484847
    // White  #FFFFFF

    private static final String HEADER =
            "<gradient:#EA7125:#FECB00><bold>✦ ScaenaCraft Props Desk ✦</bold></gradient>";

    private static final String UNLOCKED =
            HEADER + "\n" +
            "<color:#FECB00>You have been issued the <bold>Name Tag Permit</bold>.</color>\n" +
            "<color:#FFFFFF>The recipe is now unlocked.</color>\n" +
            "<color:#484847>Craft with <color:#FECB00>Paper</color> + <color:#FECB00>Copper Nugget</color>.</color>";

    private static final String ALREADY =
            HEADER + "\n" +
            "<color:#FFFFFF>Your <bold>Name Tag</bold> recipe is already in your script.</color>\n" +
            "<color:#484847>No further paperwork required.</color>";

    private static final String NO_PERMISSION =
            HEADER + "\n" +
            "<color:#FFFFFF>This request requires clearance.</color>\n" +
            "<color:#484847>If you believe this is a mistake, ask an admin.</color>";

    private static final String MISSING_RECIPE =
            HEADER + "\n" +
            "<color:#FFFFFF>The permit desk cannot locate the recipe ledger.</color>\n" +
            "<color:#484847>Please notify an admin.</color>";

    private static final String HELP =
            HEADER + "\n" +
            "<color:#FFFFFF>Usage:</color>\n" +
            "<color:#FECB00>/nametags</color><color:#484847> to unlock</color>\n" +
            "<color:#FECB00>/nametags give &lt;player&gt;</color><color:#484847> admin issue</color>";

    private static final String GIVE_USAGE =
            HEADER + "\n" +
            "<color:#FFFFFF>Admin usage:</color>\n" +
            "<color:#FECB00>/nametags give &lt;player&gt;</color>";

    public Messages(MiniMessage mm) {
        this.mm = mm;
    }

    public void sendUnlocked(Player p) { p.sendMessage(c(UNLOCKED)); }
    public void sendAlreadyUnlocked(Player p) { p.sendMessage(c(ALREADY)); }
    public void sendNoPermission(Player p) { p.sendMessage(c(NO_PERMISSION)); }
    public void sendMissingRecipe(Player p) { p.sendMessage(c(MISSING_RECIPE)); }
    public void sendHelp(Player p) { p.sendMessage(c(HELP)); }
    public void sendGiveUsage(Player p) { p.sendMessage(c(GIVE_USAGE)); }

    private Component c(String mini) {
        return mm.deserialize(mini);
    }
}
