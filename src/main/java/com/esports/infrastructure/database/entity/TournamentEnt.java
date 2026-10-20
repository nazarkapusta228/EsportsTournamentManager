package com.esports.infrastructure.database.entity;

import com.esports.domain.model.TournamentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tournaments")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tournament_type", discriminatorType = DiscriminatorType.STRING)
public abstract class TournamentEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournaments_seq")
    @SequenceGenerator(name = "tournaments_seq", sequenceName = "tournaments_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "prize_pool", precision = 15, scale = 2)
    private BigDecimal prizePool;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(name = "max_teams", nullable = false)
    private Integer maxTeams;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tournament_registrations",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<TeamEnt> registeredTeams = new ArrayList<>();

    @Version
    private Long version;

    public TournamentEnt() {
    }

    public TournamentEnt(String name, LocalDate startDate, LocalDate endDate,
                         BigDecimal prizePool, Integer maxTeams) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizePool = prizePool;
        this.maxTeams = maxTeams;
        this.status = TournamentStatus.UPCOMING;
    }

    public void addTeam(TeamEnt team) {
        this.registeredTeams.add(team);
    }

    public void removeTeam(TeamEnt team) {
        this.registeredTeams.remove(team);
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

    public List<TeamEnt> getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(List<TeamEnt> registeredTeams) {
        this.registeredTeams = registeredTeams;
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
        TournamentEnt that = (TournamentEnt) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
