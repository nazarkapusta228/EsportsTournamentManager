package com.esports.infrastructure.mongodb.mapper;

import com.esports.domain.model.Group;
import com.esports.infrastructure.mongodb.document.GroupDocument;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class GroupMapper {
    private GroupMapper() { }

    public static GroupDocument toDocument(Group group) {
        if (group == null) return null;
        GroupDocument document = new GroupDocument();
        document.setId(group.getId());
        document.setName(group.getName());
        document.setTournamentId(group.getTournament() == null ? null : group.getTournament().getId());
        document.setTeamIds(group.getTeams().stream()
                .map(team -> team.getId())
                .collect(Collectors.toCollection(ArrayList::new)));
        return document;
    }

    public static Group toDomain(GroupDocument document) {
        return document == null ? null : new Group(document.getId(), document.getName(), null);
    }
}
