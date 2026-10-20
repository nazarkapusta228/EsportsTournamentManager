package com.esports.infrastructure.database.mapper;

import com.esports.domain.model.Player;
import com.esports.domain.model.Team;
import com.esports.infrastructure.database.entity.PlayerEnt;
import com.esports.infrastructure.database.entity.TeamEnt;

import java.util.stream.Collectors;

public class TeamMapper {

    public static Team toDomain(TeamEnt entity) {
        if (entity == null) {
            return null;
        }

        Team team = new Team(entity.getId(), entity.getName(), entity.getRegion());
        
        if (entity.getPlayers() != null) {
            for (PlayerEnt playerEnt : entity.getPlayers()) {
                Player player = PlayerMapper.toDomain(playerEnt);
                team.getPlayers().add(player);
            }
        }

        return team;
    }

    public static TeamEnt toEntity(Team domain) {
        if (domain == null) {
            return null;
        }

        TeamEnt entity = new TeamEnt(domain.getName(), domain.getRegion());
        entity.setId(domain.getId());

        if (domain.getPlayers() != null) {
            for (Player player : domain.getPlayers()) {
                PlayerEnt playerEnt = PlayerMapper.toEntity(player);
                entity.getPlayers().add(playerEnt);
            }
        }

        return entity;
    }

    public static void updateEntity(Team domain, TeamEnt entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setRegion(domain.getRegion());

        // Update players list
        entity.getPlayers().clear();
        if (domain.getPlayers() != null) {
            for (Player player : domain.getPlayers()) {
                PlayerEnt playerEnt = PlayerMapper.toEntity(player);
                entity.getPlayers().add(playerEnt);
            }
        }
    }
}
