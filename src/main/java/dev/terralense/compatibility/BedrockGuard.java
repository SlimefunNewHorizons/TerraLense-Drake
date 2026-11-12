package dev.terralense.compatibility;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Detects Bedrock players through Floodgate, a soft dependency resolved by reflection so the
 * plugin loads and runs unchanged when Floodgate is not installed. Bedrock clients ignore Java
 * resource packs, so the glyph minimap would render as empty boxes for them.
 */
public final class BedrockGuard {
    private static final Method GET_INSTANCE;
    private static final Method IS_FLOODGATE_PLAYER;

    static {
        Method getInstance = null;
        Method isPlayer = null;
        try {
            Class<?> type = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            getInstance = type.getMethod("getInstance");
            isPlayer = type.getMethod("isFloodgatePlayer", UUID.class);
        } catch (ReflectiveOperationException | LinkageError ex) {
            // Floodgate not installed: nobody is treated as Bedrock.
        }
        GET_INSTANCE = getInstance;
        IS_FLOODGATE_PLAYER = isPlayer;
    }

    private BedrockGuard() {
    }

    /** True when Floodgate is present and reports this player as a Bedrock client. */
    public static boolean isBedrock(UUID playerId) {
        if (GET_INSTANCE == null || IS_FLOODGATE_PLAYER == null) {
            return false;
        }
        try {
            // Looked up per call: Floodgate may finish initialising after this plugin loads.
            Object api = GET_INSTANCE.invoke(null);
            return api != null && Boolean.TRUE.equals(IS_FLOODGATE_PLAYER.invoke(api, playerId));
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return false;
        }
    }
}
