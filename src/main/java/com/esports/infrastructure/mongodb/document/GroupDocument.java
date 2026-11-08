package com.esports.infrastructure.mongodb.document;

import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;

/** MongoDB representation of a group; matches are queried by groupId. */
public class GroupDocument implements IdentifiedDocument {
    @BsonId
    private Long id;
    private String name;
    private Long tournamentId;
    private List<Long> teamIds = new ArrayList<>();

    public GroupDocument() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }
    public List<Long> getTeamIds() { return teamIds; }
    public void setTeamIds(List<Long> teamIds) { this.teamIds = teamIds; }
}
