package com.esports.infrastructure.database.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "players")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "player_type", discriminatorType = DiscriminatorType.STRING)
public abstract class PlayerEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "players_seq")
    @SequenceGenerator(name = "players_seq", sequenceName = "players_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @Version
    private Long version;

    public PlayerEnt() {
    }

    public PlayerEnt(String nickname, String role) {
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
        PlayerEnt playerEnt = (PlayerEnt) o;
        return Objects.equals(id, playerEnt.id) && Objects.equals(nickname, playerEnt.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname);
    }
}
