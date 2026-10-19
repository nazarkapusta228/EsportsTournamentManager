package com.esports.domain.model;

import java.util.Objects;

public class Player {
    private Long id;
    private String nickname;
    private String role; // Наприклад: Rifler, AWPer, In-Game Leader тощо

    public Player() {
    }

    public Player(Long id, String nickname, String role) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) && Objects.equals(nickname, player.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname);
    }
}