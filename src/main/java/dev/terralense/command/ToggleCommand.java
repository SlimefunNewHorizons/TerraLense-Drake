package dev.terralense.command;

import dev.terralense.core.Lang;
import dev.terralense.core.Messages;
import dev.terralense.hud.HudDelivery;
import dev.terralense.player.MinimapSession;
import dev.terralense.player.SessionManager;
import dev.terralense.storage.PlayerDataStore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** {@code /terralense toggle} - turns a player's minimap on or off. */
public final class ToggleCommand implements SubCommand {
    private final SessionManager sessions;
    private final PlayerDataStore store;
    private final HudDelivery delivery;

    public ToggleCommand(SessionManager sessions, PlayerDataStore store, HudDelivery delivery) {
        this.sessions = sessions;
        this.store = store;
        this.delivery = delivery;
    }

    @Override
    public String name() {
        return "toggle";
    }

    @Override
    public String permission() {
        return "terralense.toggle";
    }

    @Override
    public String usage() {
        return "toggle [on|off]";
    }

    @Override
    public String description() {
        return Lang.t("toggle-desc");
    }

    @Override
    public java.util.List<String> complete(CommandSender sender, String[] args) {
        return args.length == 1
                ? java.util.List.of("on", "off").stream().filter(o -> o.startsWith(args[0])).toList()
                : java.util.List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        MinimapSession session = sessions.session(player.getUniqueId());
        if (session == null) {
            sender.sendMessage(Messages.error(Lang.t("not-ready")));
            return;
        }
        boolean enabled = !session.preferences().minimapEnabled();
        if (args.length > 0) {
            switch (args[0].toLowerCase(java.util.Locale.ROOT)) {
                case "on" -> enabled = true;
                case "off" -> enabled = false;
                default -> {
                    sender.sendMessage(Messages.error(Lang.t("toggle-usage", "mapa")));
                    return;
                }
            }
        }
        session.preferences().setMinimapEnabled(enabled);
        session.markTerrainDirty();
        store.savePreferences(player.getUniqueId(), session.preferences());

        if (enabled) {
            sender.sendMessage(Messages.success(Lang.t("toggle-on")));
            sender.sendMessage(Messages.warn(Lang.t("client-warning")));
        } else {
            // Clear both surfaces immediately rather than leaving a stale frame or an orphaned boss
            // bar on screen.
            delivery.hide(player);
            sender.sendMessage(Messages.info(Lang.t("toggle-off")));
        }
    }
}
