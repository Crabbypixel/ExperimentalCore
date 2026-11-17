package rockar.experimentalcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import rockar.experimentalcore.ExperimentalCore;

/**
 * Shows the welcome message when a player joins.
 *
 * A Listener is a class whose @EventHandler methods Bukkit calls when the
 * matching event happens. "implements Listener" adds no methods of its own -
 * it is a marker that tells Bukkit this class is worth scanning for handlers.
 */
public class JoinListener implements Listener {

    /**
     * A reference back to the main class, handed in by onEnable().
     *
     * We need it for getConfig() and format(). Passing the plugin in like this,
     * rather than reaching for a global, is the normal Bukkit pattern: it keeps
     * the listener testable and makes the dependency obvious.
     */
    private final ExperimentalCore plugin;

    public JoinListener(ExperimentalCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Called every time a player joins.
     *
     * The method NAME does not matter - you could call this anything. What
     * subscribes it to joins is the parameter type, PlayerJoinEvent. Bukkit
     * finds methods marked @EventHandler and reads their parameter to decide
     * what each one listens for.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // The event object carries the details of what happened. For a join,
        // that is mainly "who".
        Player player = event.getPlayer();

        // Reading the config fresh each time means editing the server's
        // config.yml and restarting is enough - no rebuild.
        // The second argument is the fallback if the key is missing.
        if (!plugin.getConfig().getBoolean("join.message-enabled", true)) {
            return;
        }

        // getStringList returns an empty list (not null) when the key is
        // missing, so this loop is safe even with a half-written config.
        for (String line : plugin.getConfig().getStringList("join.message")) {
            // format() turns the '&' colour codes into a real Component.
            // Sending the raw String would print "&6Welcome" literally.
            player.sendMessage(plugin.format(line));
        }

        // Worth knowing: there are TWO messages when someone joins.
        //
        //   player.sendMessage(...)  - private, only this player sees it.
        //                              That is what we just did.
        //   event.joinMessage(...)   - the yellow "X joined the game" line
        //                              that everyone sees. Pass null to hide it.
        //
        // A leave message is the same shape as this method with one word
        // changed: PlayerQuitEvent instead of PlayerJoinEvent. It would go in
        // this same class and need no new registration, because registration
        // is per-class, not per-method.
    }
}
