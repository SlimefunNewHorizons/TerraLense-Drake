package dev.terralense.command;

import dev.terralense.core.Lang;
import dev.terralense.core.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * Short Spanish entry points {@code /mapa} and {@code /wp}. They rewrite their arguments into a
 * {@code /terralense} call, so permissions, validation and Bedrock blocking stay in one place.
 */
public final class MapAlias implements CommandExecutor, TabCompleter {
    private final TerraLenseCommand root;
    private final boolean waypoints;

    private MapAlias(TerraLenseCommand root, boolean waypoints) {
        this.root = root;
        this.waypoints = waypoints;
    }

    public static MapAlias map(TerraLenseCommand root) {
        return new MapAlias(root, false);
    }

    public static MapAlias waypoints(TerraLenseCommand root) {
        return new MapAlias(root, true);
    }

    /** {@code /mapa ...} to {@code /terralense ...} arguments. */
    static String[] translateMap(String[] args) {
        if (args.length == 0) {
            return new String[] {"toggle"};
        }
        String head = args[0].toLowerCase(Locale.ROOT);
        String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length);
        return switch (head) {
            case "on", "off", "activar", "desactivar" -> new String[] {"toggle",
                    head.equals("on") || head.equals("activar") ? "on" : "off"};
            case "escala", "zoom" -> prepend("zoom", rest);
            case "pos", "posicion", "posición" -> prepend("position", rest);
            case "modo" -> prepend("mode", rest);
            case "grande" -> prepend("map", rest);
            case "ayuda" -> new String[] {"help"};
            default -> args;
        };
    }

    /** {@code /wp ...} to {@code /terralense waypoint ...} arguments. */
    static String[] translateWaypoint(String[] args) {
        if (args.length == 0) {
            return new String[] {"waypoint"};
        }
        String head = args[0].toLowerCase(Locale.ROOT);
        String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length);
        String action = switch (head) {
            case "add", "agregar", "nuevo" -> "add";
            case "del", "borrar", "remove", "eliminar" -> "remove";
            case "list", "lista" -> "list";
            default -> args[0];
        };
        return prepend("waypoint", prepend(action, rest));
    }

    private static String[] prepend(String first, String[] rest) {
        String[] out = new String[rest.length + 1];
        out[0] = first;
        System.arraycopy(rest, 0, out, 1, rest.length);
        return out;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!waypoints && args.length == 1 && args[0].equalsIgnoreCase("ayuda")) {
            sendHelp(sender);
            return true;
        }
        String[] translated = waypoints ? translateWaypoint(args) : translateMap(args);
        return root.onCommand(sender, command, label, translated);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label,
                                      String[] args) {
        if (waypoints) {
            if (args.length == 1) {
                return filter(List.of("add", "del", "list"), args[0]);
            }
            return root.onTabComplete(sender, command, label,
                    translateWaypoint(args));
        }
        if (args.length == 1) {
            return filter(List.of("on", "off", "escala", "pos", "modo", "grande", "ayuda"),
                    args[0]);
        }
        return root.onTabComplete(sender, command, label, translateMap(args));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Messages.info(Lang.t("help-title")));
        for (String key : List.of("alias-help-mapa", "alias-help-escala", "alias-help-pos",
                "alias-help-modo", "alias-help-grande", "alias-help-wp")) {
            sender.sendMessage(Messages.detailLine("", Lang.t(key)));
        }
    }

    private static List<String> filter(List<String> options, String prefix) {
        List<String> out = new ArrayList<>();
        for (String option : options) {
            if (option.startsWith(prefix.toLowerCase(Locale.ROOT))) {
                out.add(option);
            }
        }
        return out;
    }
}
