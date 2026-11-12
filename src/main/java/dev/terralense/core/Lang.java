package dev.terralense.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Player-facing text lookup. Strings live in {@code lang/<code>.yml} inside the jar; the selected
 * language falls back to English, and a missing key falls back to the key itself. Placeholders are
 * {@code {0}}, {@code {1}}, ...
 */
public final class Lang {
    public static final String DEFAULT = "es";
    private static final String FALLBACK = "en";

    private static final YamlConfiguration FALLBACK_BUNDLE = load(FALLBACK);
    private static volatile YamlConfiguration selected = load(DEFAULT);

    private Lang() {
    }

    /** Selects the language; unknown codes fall back to English. */
    public static void select(String code) {
        selected = load(code == null ? DEFAULT : code.trim().toLowerCase(Locale.ROOT));
    }

    public static String t(String key, Object... args) {
        String text = selected.getString(key);
        if (text == null) {
            text = FALLBACK_BUNDLE.getString(key, key);
        }
        for (int i = 0; i < args.length; i++) {
            text = text.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return text;
    }

    private static YamlConfiguration load(String code) {
        YamlConfiguration yaml = new YamlConfiguration();
        String safe = code.matches("[a-z]{2,3}") ? code : FALLBACK;
        try (InputStream in = Lang.class.getResourceAsStream("/lang/" + safe + ".yml")) {
            if (in == null) {
                return safe.equals(FALLBACK) ? yaml : load(FALLBACK);
            }
            yaml.loadFromString(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException ex) {
            throw new IllegalStateException("Cannot read lang/" + safe + ".yml", ex);
        }
        return yaml;
    }
}
