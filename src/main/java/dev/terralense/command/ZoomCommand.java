package dev.terralense.command;

import dev.terralense.config.TerraLenseConfig;
import dev.terralense.core.Lang;
import dev.terralense.core.Messages;
import dev.terralense.player.MinimapSession;
import dev.terralense.player.SessionManager;
import dev.terralense.storage.PlayerDataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** {@code /terralense zoom <blocks-per-cell>}. */
public final class ZoomCommand implements SubCommand {
    private final SessionManager sessions;
    private final PlayerDataStore store;
    private final Supplier<TerraLenseConfig> config;

    public ZoomCommand(SessionManager sessions,
                       PlayerDataStore store,
                       Supplier<TerraLenseConfig> config) {
        this.sessions = sessions;
        this.store = store;
        this.config = config;
    }

    @Override
    public String name() {
        return "zoom";
    }

    @Override
    public String permission() {
        return "terralense.use";
    }

    @Override
    public String usage() {
        return "zoom <level>";
    }

    @Override
    public String description() {
        return Lang.t("zoom-desc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TerraLenseConfig current = config.get();
        if (args.length < 1) {
            sender.sendMessage(Messages.error(Lang.t("zoom-usage", current.minZoom(), current.maxZoom())));
            return;
        }
        int requested;
        try {
            requested = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(Messages.error(Lang.t("not-a-number", args[0])));
            return;
        }
        if (requested < current.minZoom() || requested > current.maxZoom()) {
            sender.sendMessage(Messages.error(Lang.t("zoom-range", current.minZoom(), current.maxZoom())));
            return;
        }

        Player player = (Player) sender;
        MinimapSession session = sessions.session(player.getUniqueId());
        if (session == null) {
            sender.sendMessage(Messages.error(Lang.t("not-ready")));
            return;
        }
        session.preferences().setZoom(requested);
        session.markTerrainDirty();
        store.savePreferences(player.getUniqueId(), session.preferences());
        sender.sendMessage(Messages.success(Lang.t("zoom-set", requested)));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        TerraLenseConfig current = config.get();
        List<String> options = new ArrayList<>();
        for (int zoom = current.minZoom(); zoom <= current.maxZoom(); zoom++) {
            String value = Integer.toString(zoom);
            if (value.startsWith(args[0])) {
                options.add(value);
            }
        }
        return options;
    }
}
