package dev.terralense.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class MapAliasTest {
    @Test
    void mapaTranslatesToTerraLenseSubcommands() {
        assertArrayEquals(new String[] {"toggle"}, MapAlias.translateMap(new String[] {}));
        assertArrayEquals(new String[] {"toggle", "on"}, MapAlias.translateMap(new String[] {"on"}));
        assertArrayEquals(new String[] {"toggle", "off"}, MapAlias.translateMap(new String[] {"OFF"}));
        assertArrayEquals(new String[] {"zoom", "4"}, MapAlias.translateMap(new String[] {"escala", "4"}));
        assertArrayEquals(new String[] {"position", "left"}, MapAlias.translateMap(new String[] {"pos", "left"}));
        assertArrayEquals(new String[] {"mode", "north"}, MapAlias.translateMap(new String[] {"modo", "north"}));
        assertArrayEquals(new String[] {"map", "8"}, MapAlias.translateMap(new String[] {"grande", "8"}));
        assertArrayEquals(new String[] {"status"}, MapAlias.translateMap(new String[] {"status"}));
    }

    @Test
    void wpTranslatesToWaypointActions() {
        assertArrayEquals(new String[] {"waypoint", "add", "casa"},
                MapAlias.translateWaypoint(new String[] {"add", "casa"}));
        assertArrayEquals(new String[] {"waypoint", "remove", "casa"},
                MapAlias.translateWaypoint(new String[] {"del", "casa"}));
        assertArrayEquals(new String[] {"waypoint", "list"},
                MapAlias.translateWaypoint(new String[] {"list"}));
        assertArrayEquals(new String[] {"waypoint"}, MapAlias.translateWaypoint(new String[] {}));
        assertArrayEquals(new String[] {"waypoint", "tp", "casa"},
                MapAlias.translateWaypoint(new String[] {"tp", "casa"}));
    }
}
