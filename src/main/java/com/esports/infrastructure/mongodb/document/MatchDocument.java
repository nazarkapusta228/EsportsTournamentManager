package com.esports.infrastructure.mongodb.document;

import java.time.LocalDateTime;
import org.bson.codecs.pojo.annotations.BsonId;

/** MongoDB representation of a match; related entities are referenced by ID. */
public class MatchDocument implements IdentifiedDocument {
    @BsonId
    private Long id;
    private Long team1Id;
    private Long team2Id;
    private Integer team1Score;
    private Integer team2Score;
    private LocalDateTime matchDate;
    private String status;
    private Long groupId;
    private String round;

    public MatchDocument() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeam1Id() { return team1Id; }
    public void setTeam1Id(Long team1Id) { this.team1Id = team1Id; }
    public Long getTeam2Id() { return team2Id; }
    public void setTeam2Id(Long team2Id) { this.team2Id = team2Id; }
    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }
    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }
}
