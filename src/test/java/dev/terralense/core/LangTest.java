package dev.terralense.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.terralense.compatibility.BedrockGuard;
import dev.terralense.config.TerraLenseConfig;
import dev.terralense.player.PlayerPreferences;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class LangTest {
    @AfterEach
    void reset() {
        Lang.select("es");
    }

    private static YamlConfiguration bundle(String code) throws Exception {
        try (InputStream in = LangTest.class.getResourceAsStream("/lang/" + code + ".yml")) {
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    @Test
    void spanishIsDefaultAndEnglishAvailable() {
        assertEquals("Minimapa activado.", Lang.t("toggle-on"));
        Lang.select("en");
        assertEquals("Minimap enabled.", Lang.t("toggle-on"));
        Lang.select("xx");
        assertEquals("Minimap enabled.", Lang.t("toggle-on"));
    }

    @Test
    void bothBundlesHaveSameKeys() throws Exception {
        assertEquals(bundle("en").getKeys(false), bundle("es").getKeys(false));
    }

    @Test
    void placeholdersAreFilled() {
        assertEquals("Waypoint 'casa' guardado.", Lang.t("wp-saved", "casa"));
    }

    @Test
    void clientWarningMentionsVersionAndVia() {
        assertTrue(Lang.t("client-warning").contains("26.2"));
        assertTrue(Lang.t("join").contains("ViaVersion"));
    }

    @Test
    void bedrockGuardIsFalseWithoutFloodgate() {
        assertFalse(BedrockGuard.isBedrock(UUID.randomUUID()));
    }

    @Test
    void newPlayersStartWithMinimapOff() {
        TerraLenseConfig config = TerraLenseConfig.load(new YamlConfiguration(),
                new Log(Logger.getLogger("LangTest")));
        assertFalse(PlayerPreferences.defaultsFrom(config).minimapEnabled());
    }
}
