package com.esports.infrastructure.database.entity;

import com.esports.domain.model.MatchStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "matches")
public class MatchEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matches_seq")
    @SequenceGenerator(name = "matches_seq", sequenceName = "matches_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id", nullable = false)
    private TeamEnt team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id", nullable = false)
    private TeamEnt team2;

    @Column(name = "team1_score")
    private Integer team1Score;

    @Column(name = "team2_score")
    private Integer team2Score;

    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEnt group;

    @Column(length = 50)
    private String round;

    @Version
    private Long version;

    public MatchEnt() {
    }

    public MatchEnt(TeamEnt team1, TeamEnt team2, LocalDateTime matchDate, String round) {
        this.team1 = team1;
        this.team2 = team2;
        this.matchDate = matchDate;
        this.round = round;
        this.status = MatchStatus.SCHEDULED;
        this.team1Score = 0;
        this.team2Score = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamEnt getTeam1() {
        return team1;
    }

    public void setTeam1(TeamEnt team1) {
        this.team1 = team1;
    }

    public TeamEnt getTeam2() {
        return team2;
    }

    public void setTeam2(TeamEnt team2) {
        this.team2 = team2;
    }

    public Integer getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(Integer team1Score) {
        this.team1Score = team1Score;
    }

    public Integer getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(Integer team2Score) {
        this.team2Score = team2Score;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public GroupEnt getGroup() {
        return group;
    }

    public void setGroup(GroupEnt group) {
        this.group = group;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchEnt matchEnt = (MatchEnt) o;
        return Objects.equals(id, matchEnt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
