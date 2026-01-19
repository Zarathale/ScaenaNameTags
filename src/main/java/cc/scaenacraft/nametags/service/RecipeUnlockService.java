package cc.scaenacraft.nametags.service;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.logging.Logger;

public final class RecipeUnlockService {

    public enum Result {
        UNLOCKED,
        ALREADY_UNLOCKED,
        MISSING_RECIPE
    }

    private final Plugin plugin;
    private final Logger logger;
    private final NamespacedKey key;

    public RecipeUnlockService(Plugin plugin, String recipeId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.key = parseRecipeKey(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe-id: " + recipeId));
    }

    public Result unlockFor(Player player) {
        if (!isRecipePresent()) {
            logger.warning("Recipe missing or not registered: " + key + ". Check datapack load + recipe ID.");
            return Result.MISSING_RECIPE;
        }

        final boolean newlyDiscovered = player.discoverRecipe(key);
        return newlyDiscovered ? Result.UNLOCKED : Result.ALREADY_UNLOCKED;
    }

    private boolean isRecipePresent() {
        // Paper/Spigot has Bukkit.getRecipe(NamespacedKey) in modern versions.
        // If it ever returns null, we treat it as missing.
        try {
            return Bukkit.getRecipe(key) != null;
        } catch (Throwable ignored) {
            // If API changes unexpectedly, fall back to "best effort":
            // discoverRecipe will return false if the server can't resolve it.
            logger.warning("Unable to verify recipe presence via Bukkit.getRecipe(key). Falling back to best-effort unlock.");
            return true;
        }
    }

    private Optional<NamespacedKey> parseRecipeKey(String raw) {
        if (raw == null) return Optional.empty();
        final String s = raw.trim();
        final int idx = s.indexOf(':');
        if (idx <= 0 || idx >= s.length() - 1) return Optional.empty();
        final String namespace = s.substring(0, idx);
        final String key = s.substring(idx + 1);
        return Optional.of(new NamespacedKey(namespace, key));
    }
}
