package com.esports.infrastructure.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class PlayerEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamEnt team;

    public PlayerEnt() {
    }

    public PlayerEnt(Long id, String nickname, String role) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public TeamEnt getTeam() {
        return team;
    }

    public void setTeam(TeamEnt team) {
        this.team = team;
    }
}