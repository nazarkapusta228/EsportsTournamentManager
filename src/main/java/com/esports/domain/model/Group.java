package com.esports.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group {
    private Long id;
    private String name;
    private Tournament tournament;
    private List<Team> teams = new ArrayList<>();
    private List<Match> matches = new ArrayList<>();

    public Group() {
    }

    public Group(Long id, String name, Tournament tournament) {
        this.id = id;
        this.name = name;
        this.tournament = tournament;
    }

    // Business logic: Add team to group
    public void addTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }
        if (teams.contains(team)) {
            throw new IllegalStateException("Team " + team.getName() + " is already in this group");
        }
        teams.add(team);
    }

    // Business logic: Add match to group
    public void addMatch(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null");
        }
        matches.add(match);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
