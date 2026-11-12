package dev.terralense.command;

import dev.terralense.core.MinimapService;
import dev.terralense.core.Lang;
import dev.terralense.core.Messages;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** {@code /terralense map} - shows an enlarged, wider-area map. */
public final class MapCommand implements SubCommand {
    private final MinimapService minimaps;

    public MapCommand(MinimapService minimaps) {
        this.minimaps = minimaps;
    }

    @Override
    public String name() {
        return "map";
    }

    @Override
    public List<String> aliases() {
        return List.of("full", "fullscreen", "large");
    }

    @Override
    public String permission() {
        return "terralense.use";
    }

    @Override
    public String usage() {
        return "map [zoom]";
    }

    @Override
    public String description() {
        return Lang.t("map-desc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        int zoom = FULLSCREEN_DEFAULT_ZOOM;
        if (args.length > 0) {
            try {
                zoom = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(Messages.error(Lang.t("not-a-number", args[0])));
                return;
            }
            if (zoom < 1 || zoom > 16) {
                player.sendMessage(Messages.error(Lang.t("map-range")));
                return;
            }
        }
        minimaps.requestFullscreen(player, zoom);
    }

    /** Coarser than the HUD so the large view covers noticeably more ground. */
    private static final int FULLSCREEN_DEFAULT_ZOOM = 8;

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("2", "4", "8", "16").stream()
                    .filter(option -> option.startsWith(args[0]))
                    .toList();
        }
        return List.of();
    }
}
