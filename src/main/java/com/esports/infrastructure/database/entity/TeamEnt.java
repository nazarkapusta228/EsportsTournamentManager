package com.esports.infrastructure.database.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "teams")
public class TeamEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teams_seq")
    @SequenceGenerator(name = "teams_seq", sequenceName = "teams_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String region;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<PlayerEnt> players = new ArrayList<>();

    @Version
    private Long version;

    public TeamEnt() {
    }

    public TeamEnt(String name, String region) {
        this.name = name;
        this.region = region;
    }

    public void addPlayer(PlayerEnt player) {
        this.players.add(player);
    }

    public void removePlayer(PlayerEnt player) {
        this.players.remove(player);
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

    public List<PlayerEnt> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEnt> players) {
        this.players = players;
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
        TeamEnt teamEnt = (TeamEnt) o;
        return Objects.equals(id, teamEnt.id) && Objects.equals(name, teamEnt.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
