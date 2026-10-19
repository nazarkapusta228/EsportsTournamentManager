package com.esports.infrastructure.database.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class TeamEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String region;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEnt> players = new ArrayList<>();

    public TeamEnt() {
    }

    public TeamEnt(Long id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    public void addPlayer(PlayerEnt player) {
        players.add(player);
        player.setTeam(this);
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
}