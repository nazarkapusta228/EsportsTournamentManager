package com.esports;

import com.esports.domain.model.Player;
import com.esports.domain.model.Team;
import com.esports.domain.model.Tournament;
import com.esports.domain.repository.TeamRepository;
import com.esports.domain.repository.TournamentRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.adapter.MongoTeamRepositoryAdapter;
import com.esports.infrastructure.mongodb.adapter.MongoTournamentRepositoryAdapter;

import java.util.List;

/** Application entry point for the MongoDB implementation. */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Esports Tournament Manager (MongoDB) ===\n");

        try {
            TeamRepository teamRepository = new MongoTeamRepositoryAdapter();
            TournamentRepository tournamentRepository = new MongoTournamentRepositoryAdapter();

            List<Team> teams = teamRepository.findAll();
            System.out.println("Teams:");
            for (Team team : teams) {
                System.out.printf("- %s (%s), %d players%n", team.getName(), team.getRegion(),
                        team.getPlayers().size());
                for (Player player : team.getPlayers()) {
                    System.out.printf("  - %s [%s, %s]%n", player.getNickname(),
                            player.getPlayerType(), player.getRole());
                }
            }

            List<Tournament> tournaments = tournamentRepository.findAll();
            System.out.println("\nTournaments:");
            for (Tournament tournament : tournaments) {
                System.out.printf("- %s [%s, %s], %d registered teams%n",
                        tournament.getName(), tournament.getTournamentType(), tournament.getStatus(),
                        tournament.getRegisteredTeams().size());
            }
        } catch (Exception e) {
            System.err.println("MongoDB application failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoDatabaseProvider.close();
        }
    }
}
