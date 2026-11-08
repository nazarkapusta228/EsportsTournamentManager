package com.esports.infrastructure.mongodb.mapper;

import com.esports.domain.model.Team;
import com.esports.infrastructure.mongodb.document.TeamDocument;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class TeamMapper {
    private TeamMapper() { }

    public static TeamDocument toDocument(Team team) {
        if (team == null) return null;
        TeamDocument document = new TeamDocument();
        document.setId(team.getId());
        document.setName(team.getName());
        document.setRegion(team.getRegion());
        document.setPlayerIds(team.getPlayers().stream()
                .map(player -> player.getId())
                .collect(Collectors.toCollection(ArrayList::new)));
        return document;
    }

    public static Team toDomain(TeamDocument document) {
        if (document == null) return null;
        return new Team(document.getId(), document.getName(), document.getRegion());
    }
}
