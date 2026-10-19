package com.esports.infrastructure.database.adapter;

import com.esports.domain.model.Player;
import com.esports.domain.model.Team;
import com.esports.infrastructure.database.entity.PlayerEnt;
import com.esports.infrastructure.database.entity.TeamEnt;

import java.util.stream.Collectors;

public class TeamMapper {

    public static Team toDomain(TeamEnt ent) {
        if (ent == null) return null;
        Team team = new Team(ent.getId(), ent.getName(), ent.getRegion());
        if (ent.getPlayers() != null) {
            team.setPlayers(ent.getPlayers().stream()
                    .map(PlayerMapper::toDomain)
                    .collect(Collectors.toList()));
        }
        return team;
    }

    public static TeamEnt toEntity(Team domain) {
        if (domain == null) return null;
        TeamEnt ent = new TeamEnt(domain.getId(), domain.getName(), domain.getRegion());
        if (domain.getPlayers() != null) {
            for (Player player : domain.getPlayers()) {
                PlayerEnt playerEnt = PlayerMapper.toEntity(player);
                ent.addPlayer(playerEnt);
            }
        }
        return ent;
    }
}