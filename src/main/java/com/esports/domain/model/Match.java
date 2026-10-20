package com.esports.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {
    private Long id;
    private Team team1;
    private Team team2;
    private Integer team1Score;
    private Integer team2Score;
    private LocalDateTime matchDate;
    private MatchStatus status;
    private Group group;
    private String round;

    public Match() {
    }

    public Match(Long id, Team team1, Team team2, LocalDateTime matchDate, String round) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.matchDate = matchDate;
        this.round = round;
        this.status = MatchStatus.SCHEDULED;
        this.team1Score = 0;
        this.team2Score = 0;
    }

    // Business logic: Set match result
    public void setResult(Integer team1Score, Integer team2Score) {
        if (status == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change result of completed match");
        }
        if (team1Score < 0 || team2Score < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.status = MatchStatus.COMPLETED;
    }

    // Business logic: Get winner
    public Team getWinner() {
        if (status != MatchStatus.COMPLETED) {
            return null;
        }
        if (team1Score > team2Score) {
            return team1;
        } else if (team2Score > team1Score) {
            return team2;
        }
        return null; // Draw
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
