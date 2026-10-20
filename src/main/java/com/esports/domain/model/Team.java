package com.esports.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 7;

    private Long id;
    private String name;
    private String region;
    private List<Player> players = new ArrayList<>();

    public Team() {
    }

    public Team(Long id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    // Business logic: Add player with validation
    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Team already has maximum number of players (" + MAX_PLAYERS + ")");
        }
        if (players.contains(player)) {
            throw new IllegalStateException("Player " + player.getNickname() + " is already in the team");
        }
        this.players.add(player);
    }

    // Business logic: Remove player
    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    // Business logic: Check if team is eligible for tournament
    public boolean isEligibleForTournament() {
        return players.size() >= MIN_PLAYERS;
    }

    // Business logic: Validate team composition
    public void validateTeamComposition() {
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("Team must have at least " + MIN_PLAYERS + " players");
        }
        if (players.size() > MAX_PLAYERS) {
            throw new IllegalStateException("Team cannot have more than " + MAX_PLAYERS + " players");
        }
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) && Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
