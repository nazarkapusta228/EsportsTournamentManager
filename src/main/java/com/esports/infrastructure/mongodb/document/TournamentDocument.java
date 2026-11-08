package com.esports.infrastructure.mongodb.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;

/** MongoDB representation of LAN and online tournaments. */
public class TournamentDocument implements IdentifiedDocument {
    @BsonId
    private Long id;
    private String type;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private String status;
    private Integer maxTeams;
    private List<Long> registeredTeamIds = new ArrayList<>();
    private String streamUrl;
    private String serverLocation;
    private String venue;
    private String city;

    public TournamentDocument() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getPrizePool() { return prizePool; }
    public void setPrizePool(BigDecimal prizePool) { this.prizePool = prizePool; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMaxTeams() { return maxTeams; }
    public void setMaxTeams(Integer maxTeams) { this.maxTeams = maxTeams; }
    public List<Long> getRegisteredTeamIds() { return registeredTeamIds; }
    public void setRegisteredTeamIds(List<Long> registeredTeamIds) { this.registeredTeamIds = registeredTeamIds; }
    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
    public String getServerLocation() { return serverLocation; }
    public void setServerLocation(String serverLocation) { this.serverLocation = serverLocation; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
