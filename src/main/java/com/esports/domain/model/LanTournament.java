package com.esports.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LanTournament extends Tournament {
    private String venue;
    private String city;

    public LanTournament() {
        super();
    }

    public LanTournament(Long id, String name, LocalDate startDate, LocalDate endDate,
                         BigDecimal prizePool, Integer maxTeams, String venue, String city) {
        super(id, name, startDate, endDate, prizePool, maxTeams);
        this.venue = venue;
        this.city = city;
    }

    @Override
    public String getTournamentType() {
        return "LAN";
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
