package com.esports.infrastructure.mongodb;

import com.esports.domain.model.Group;
import com.esports.domain.model.LanTournament;
import com.esports.domain.model.Match;
import com.esports.domain.model.OnlineTournament;
import com.esports.domain.model.ProPlayer;
import com.esports.domain.model.Team;
import com.esports.domain.model.Tournament;
import com.esports.domain.model.TournamentStatus;
import com.esports.infrastructure.mongodb.adapter.MongoGroupRepositoryAdapter;
import com.esports.infrastructure.mongodb.adapter.MongoMatchRepositoryAdapter;
import com.esports.infrastructure.mongodb.adapter.MongoPlayerRepositoryAdapter;
import com.esports.infrastructure.mongodb.adapter.MongoTeamRepositoryAdapter;
import com.esports.infrastructure.mongodb.adapter.MongoTournamentRepositoryAdapter;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MongoAdapterIntegrationTest {
    private static MongoDatabase database;

    @BeforeAll
    static void setUp() {
        database = MongoDatabaseProvider.getDatabase("adapter_test_" + UUID.randomUUID().toString().replace("-", ""));
        database.runCommand(new org.bson.Document("ping", 1));
    }

    @AfterAll
    static void tearDown() {
        if (database != null) database.drop();
    }

    @Test
    void adaptersPreservePolymorphismAndRelationships() {
        MongoPlayerRepositoryAdapter playerAdapter = new MongoPlayerRepositoryAdapter(database);
        MongoTeamRepositoryAdapter teamAdapter = new MongoTeamRepositoryAdapter(database);
        MongoTournamentRepositoryAdapter tournamentAdapter = new MongoTournamentRepositoryAdapter(database);
        MongoGroupRepositoryAdapter groupAdapter = new MongoGroupRepositoryAdapter(database);
        MongoMatchRepositoryAdapter matchAdapter = new MongoMatchRepositoryAdapter(database);

        Team teamA = newTeam("A");
        Team teamB = newTeam("B");
        saveTeam(teamA, playerAdapter, teamAdapter);
        saveTeam(teamB, playerAdapter, teamAdapter);
        assertNotNull(teamA.getId());
        assertEquals(3, teamAdapter.findById(teamA.getId()).orElseThrow().getPlayers().size());
        assertEquals(6, playerAdapter.findAll().size());

        OnlineTournament tournament = new OnlineTournament(null, "adapter-online", LocalDate.now(),
                LocalDate.now().plusDays(2), new BigDecimal("1000.00"), 8,
                "https://stream.example", "EU-West");
        tournament.setStatus(TournamentStatus.REGISTRATION_OPEN);
        tournament.registerTeam(teamA);
        tournament.registerTeam(teamB);
        tournamentAdapter.save(tournament);

        Tournament loadedTournament = tournamentAdapter.findById(tournament.getId()).orElseThrow();
        assertInstanceOf(OnlineTournament.class, loadedTournament);
        assertEquals(2, loadedTournament.getRegisteredTeams().size());
        assertEquals("https://stream.example", ((OnlineTournament) loadedTournament).getStreamUrl());

        Group group = new Group(null, "adapter-group", loadedTournament);
        group.addTeam(teamA);
        Match match = new Match(null, teamA, teamB, LocalDateTime.now(), "Final");
        group.addMatch(match);
        groupAdapter.save(group);
        match.setGroup(group);
        matchAdapter.save(match);

        Group loadedGroup = groupAdapter.findById(group.getId()).orElseThrow();
        assertEquals(tournament.getId(), loadedGroup.getTournament().getId());
        assertEquals(1, loadedGroup.getTeams().size());
        assertEquals(1, loadedGroup.getMatches().size());
        assertEquals(group.getId(), loadedGroup.getMatches().get(0).getGroup().getId());
        assertEquals("Final", matchAdapter.findByGroupId(group.getId()).get(0).getRound());

        assertThrows(IllegalStateException.class, () -> playerAdapter.delete(teamA.getPlayers().get(0).getId()));
        assertThrows(IllegalStateException.class, () -> teamAdapter.delete(teamA.getId()));
        assertThrows(IllegalStateException.class, () -> tournamentAdapter.delete(tournament.getId()));
        assertThrows(IllegalStateException.class, () -> groupAdapter.delete(group.getId()));

        LanTournament lan = new LanTournament(null, "adapter-lan", LocalDate.now(),
                LocalDate.now().plusDays(3), new BigDecimal("2000.00"), 16,
                "Arena", "Warsaw");
        tournamentAdapter.save(lan);
        Tournament loadedLan = tournamentAdapter.findByName("adapter-lan").orElseThrow();
        assertInstanceOf(LanTournament.class, loadedLan);
    }

    private static Team newTeam(String suffix) {
        Team team = new Team(null, "adapter-team-" + suffix, "EU");
        team.addPlayer(new ProPlayer(null, "adapter-awp-" + suffix, "AWPer",
                new BigDecimal("5000.00"), "Sponsor"));
        team.addPlayer(new ProPlayer(null, "adapter-rifle-" + suffix, "Rifler",
                new BigDecimal("4000.00"), "Sponsor"));
        team.addPlayer(new ProPlayer(null, "adapter-support-" + suffix, "Support",
                new BigDecimal("3000.00"), "Sponsor"));
        return team;
    }

    private static void saveTeam(Team team, MongoPlayerRepositoryAdapter playerAdapter,
                                 MongoTeamRepositoryAdapter teamAdapter) {
        for (var player : team.getPlayers()) {
            playerAdapter.save(player);
        }
        teamAdapter.save(team);
    }
}
