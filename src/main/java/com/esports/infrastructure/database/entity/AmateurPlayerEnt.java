package com.esports.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AMATEUR")
public class AmateurPlayerEnt extends PlayerEnt {

    @Column(length = 50)
    private String rank;

    @Column(name = "hours_played")
    private Integer hoursPlayed;

    public AmateurPlayerEnt() {
        super();
    }

    public AmateurPlayerEnt(String nickname, String role, String rank, Integer hoursPlayed) {
        super(nickname, role);
        this.rank = rank;
        this.hoursPlayed = hoursPlayed;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getHoursPlayed() {
        return hoursPlayed;
    }

    public void setHoursPlayed(Integer hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }
}
