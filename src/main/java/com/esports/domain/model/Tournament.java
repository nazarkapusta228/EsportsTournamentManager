package com.esports.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Tournament {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private TournamentStatus status;
    private Integer maxTeams;
    private List<Team> registeredTeams = new ArrayList<>();

    public Tournament() {
    }

    public Tournament(Long id, String name, LocalDate startDate, LocalDate endDate,
                      BigDecimal prizePool, Integer maxTeams) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizePool = prizePool;
        this.maxTeams = maxTeams;
        this.status = TournamentStatus.UPCOMING;
    }

    public abstract String getTournamentType();

    // Business logic: Register team
    public void registerTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }
        if (status != TournamentStatus.REGISTRATION_OPEN && status != TournamentStatus.UPCOMING) {
            throw new IllegalStateException("Registration is not open for this tournament");
        }
        if (!team.isEligibleForTournament()) {
            throw new IllegalStateException("Team " + team.getName() + " is not eligible (needs at least " 
                    + Team.MIN_PLAYERS + " players)");
        }
        if (registeredTeams.size() >= maxTeams) {
            throw new IllegalStateException("Tournament is full (max " + maxTeams + " teams)");
        }
        if (registeredTeams.contains(team)) {
            throw new IllegalStateException("Team " + team.getName() + " is already registered");
        }
        registeredTeams.add(team);
    }

    // Business logic: Unregister team
    public void unregisterTeam(Team team) {
        if (status != TournamentStatus.REGISTRATION_OPEN && status != TournamentStatus.UPCOMING) {
            throw new IllegalStateException("Cannot unregister when registration is closed");
        }
        registeredTeams.remove(team);
    }

    // Business logic: Check if tournament can start
    public boolean canStart() {
        return registeredTeams.size() >= 2 && status == TournamentStatus.REGISTRATION_CLOSED;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(BigDecimal prizePool) {
        this.prizePool = prizePool;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public List<Team> getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(List<Team> registeredTeams) {
        this.registeredTeams = registeredTeams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
