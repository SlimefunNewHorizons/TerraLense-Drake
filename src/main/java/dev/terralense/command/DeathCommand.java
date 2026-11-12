package dev.terralense.command;

import dev.terralense.core.Lang;
import dev.terralense.core.Messages;
import dev.terralense.marker.DeathMarkerProvider;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** {@code /terralense death [clear]}. */
public final class DeathCommand implements SubCommand {
    private final DeathMarkerProvider deaths;

    public DeathCommand(DeathMarkerProvider deaths) {
        this.deaths = deaths;
    }

    @Override
    public String name() {
        return "death";
    }

    @Override
    public String permission() {
        return "terralense.use";
    }

    @Override
    public String usage() {
        return "death [clear]";
    }

    @Override
    public String description() {
        return Lang.t("death-desc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            if (deaths.clear(player.getUniqueId())) {
                sender.sendMessage(Messages.success(Lang.t("death-cleared")));
            } else {
                sender.sendMessage(Messages.info(Lang.t("death-none-marker")));
            }
            return;
        }

        deaths.current(player.getUniqueId()).ifPresentOrElse(location -> {
            org.bukkit.World world = player.getServer().getWorld(location.worldId());
            String worldName = world == null ? Lang.t("unloaded-world") : world.getName();
            sender.sendMessage(Messages.detail(Lang.t("death-last"),
                    Lang.t("death-where", location.x(), location.y(), location.z(), worldName)));

            if (world != null && world.getUID().equals(player.getWorld().getUID())) {
                int distance = (int) Math.round(Math.hypot(
                        location.x() - player.getLocation().getBlockX(),
                        location.z() - player.getLocation().getBlockZ()));
                sender.sendMessage(Messages.detail(Lang.t("death-distance"), Lang.t("death-distance-value", distance)));
            }
        }, () -> sender.sendMessage(Messages.info(Lang.t("death-none"))));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1 && "clear".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of("clear");
        }
        return List.of();
    }
}
