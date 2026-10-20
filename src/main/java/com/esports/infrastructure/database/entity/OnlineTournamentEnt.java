package com.esports.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "online_tournaments")
@DiscriminatorValue("ONLINE")
public class OnlineTournamentEnt extends TournamentEnt {

    @Column(name = "stream_url", length = 255)
    private String streamUrl;

    @Column(name = "server_location", length = 100)
    private String serverLocation;

    public OnlineTournamentEnt() {
        super();
    }

    public OnlineTournamentEnt(String name, LocalDate startDate, LocalDate endDate,
                               BigDecimal prizePool, Integer maxTeams,
                               String streamUrl, String serverLocation) {
        super(name, startDate, endDate, prizePool, maxTeams);
        this.streamUrl = streamUrl;
        this.serverLocation = serverLocation;
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
