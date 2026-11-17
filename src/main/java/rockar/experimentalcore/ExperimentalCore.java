package rockar.experimentalcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import rockar.experimentalcore.listeners.JoinListener;

/**
 * The main class. Bukkit creates exactly one instance of this when the server
 * starts, and plugin.yml points at it with the "main:" line.
 *
 * It must extend JavaPlugin. onEnable() runs when the plugin is switched on,
 * onDisable() when the server stops. You never call either yourself - the
 * server does.
 */
public final class ExperimentalCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Copies the config.yml bundled inside the jar out to
        // plugins/ExperimentalCore/config.yml, but ONLY if it isn't there yet.
        // It never overwrites edits you have made.
        saveDefaultConfig();

        // Listeners must be registered, otherwise their @EventHandler methods
        // are never called - silently, with no error at all. Writing
        // @EventHandler is NOT enough on its own; this line is what hooks it up.
        //
        // The first argument is the listener, the second is this plugin, so
        // Bukkit knows who owns it and can unregister it when we unload.
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        // getLogger() automatically prefixes [ExperimentalCore].
        // (Bukkit.getLogger() does not - that is why the old plugins printed a
        // bare "Plugin enabled." with no clue which plugin said it.)
        getLogger().info("Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled.");
    }

    /**
     * Turns a config string with '&' colour codes into something the client can
     * display, e.g. "&6Hello" becomes gold text.
     *
     * Modern Paper speaks Adventure Components rather than plain Strings, and
     * sending a raw String means the player literally sees the characters
     * "&6Hello". That was the bug in the old rockarCore: its config had colour
     * codes that never worked.
     *
     * This lives in the main class rather than the listener so that anything
     * else added later - a command, a leave message - can reuse it.
     */
    public Component format(String raw) {
        if (raw == null) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }
}
