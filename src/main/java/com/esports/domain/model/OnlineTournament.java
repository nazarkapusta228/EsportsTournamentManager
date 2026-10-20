package com.esports.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OnlineTournament extends Tournament {
    private String streamUrl;
    private String serverLocation;

    public OnlineTournament() {
        super();
    }

    public OnlineTournament(Long id, String name, LocalDate startDate, LocalDate endDate,
                            BigDecimal prizePool, Integer maxTeams, String streamUrl, String serverLocation) {
        super(id, name, startDate, endDate, prizePool, maxTeams);
        this.streamUrl = streamUrl;
        this.serverLocation = serverLocation;
    }

    @Override
    public String getTournamentType() {
        return "ONLINE";
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(String serverLocation) {
        this.serverLocation = serverLocation;
    }
}
