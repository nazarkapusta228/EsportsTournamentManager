package com.esports.infrastructure.mongodb.document;

import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;

/** MongoDB representation of a team; players are referenced by ID. */
public class TeamDocument implements IdentifiedDocument {
    @BsonId
    private Long id;
    private String name;
    private String region;
    private List<Long> playerIds = new ArrayList<>();

    public TeamDocument() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public List<Long> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<Long> playerIds) { this.playerIds = playerIds; }
}
