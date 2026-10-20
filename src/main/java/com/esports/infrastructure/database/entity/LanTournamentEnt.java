package com.esports.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lan_tournaments")
@DiscriminatorValue("LAN")
public class LanTournamentEnt extends TournamentEnt {

    @Column(length = 255)
    private String venue;

    @Column(length = 100)
    private String city;

    public LanTournamentEnt() {
        super();
    }

    public LanTournamentEnt(String name, LocalDate startDate, LocalDate endDate,
                            BigDecimal prizePool, Integer maxTeams,
                            String venue, String city) {
        super(name, startDate, endDate, prizePool, maxTeams);
        this.venue = venue;
        this.city = city;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
