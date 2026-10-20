package com.esports.infrastructure.database.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
public class GroupEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_seq")
    @SequenceGenerator(name = "groups_seq", sequenceName = "groups_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEnt tournament;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_teams",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<TeamEnt> teams = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchEnt> matches = new ArrayList<>();

    @Version
    private Long version;

    public GroupEnt() {
    }

    public GroupEnt(String name, TournamentEnt tournament) {
        this.name = name;
        this.tournament = tournament;
    }

    public void addTeam(TeamEnt team) {
        this.teams.add(team);
    }

    public void addMatch(MatchEnt match) {
        this.matches.add(match);
        match.setGroup(this);
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

    public TournamentEnt getTournament() {
        return tournament;
    }

    public void setTournament(TournamentEnt tournament) {
        this.tournament = tournament;
    }

    public List<TeamEnt> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamEnt> teams) {
        this.teams = teams;
    }

    public List<MatchEnt> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchEnt> matches) {
        this.matches = matches;
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
        GroupEnt groupEnt = (GroupEnt) o;
        return Objects.equals(id, groupEnt.id) && Objects.equals(name, groupEnt.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
