package com.min01.getintheteam.client.screen;

import com.min01.getintheteam.network.TeamMemberData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamListHelper {

    /**
     * Builds the team member list from the client-synced scoreboard.
     * The client scoreboard receives team membership updates from the server,
     * so this reflects the same data shown by /team list.
     */
    public static List<TeamMemberData> buildClientTeamList(Team team, ClientLevel level) {
        List<TeamMemberData> members = new ArrayList<>();
        if (team == null) return members;

        for (String memberId : team.getPlayers()) {
            String name = memberId;
            String uuid = memberId;
            String entityType = "";

            if (level != null) {
                try {
                    UUID memberUUID = UUID.fromString(memberId);
                    // Find the entity in the client world to resolve name/type/face
                    for (Entity e : level.entitiesForRendering()) {
                        if (e.getUUID().equals(memberUUID)) {
                            name = e.getName().getString();
                            entityType = e.getType().getDescriptionId();
                            break;
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    // Not a UUID - it's a player name
                }
            }

            members.add(new TeamMemberData(name, uuid, entityType));
        }
        return members;
    }
}