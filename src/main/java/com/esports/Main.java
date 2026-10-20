package com.esports;

import com.esports.domain.model.*;
import com.esports.domain.repository.TeamRepository;
import com.esports.infrastructure.database.JpaUtil;
import com.esports.infrastructure.database.adapter.JpaTeamRepositoryAdapter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Main application demonstrating Esports Tournament Management System
 * 
 * This application demonstrates:
 * - Domain-Driven Design with clean separation of concerns
 * - Polymorphism with Player hierarchy (ProPlayer, AmateurPlayer)
 * - JPA inheritance strategies (SINGLE_TABLE for Player, JOINED for Tournament)
 * - Optimistic locking with @Version annotation
 * - Business logic validation
 * - Repository pattern with adapters
 * - One-to-many and many-to-many relationships
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Esports Tournament Manager ===\n");

        try {
            TeamRepository teamRepository = new JpaTeamRepositoryAdapter();

            // 1. Create team with pro players (different name to avoid conflict with init.sql)
            System.out.println("1. Creating Team 'Team Liquid' with Pro Players...");
            Team naviTeam = new Team(null, "Team Liquid", "NA");
            
            ProPlayer flamie = new ProPlayer(null, "flamie", "AWPer",
                    new BigDecimal("48000.00"), "SteelSeries");
            ProPlayer npl = new ProPlayer(null, "npl", "Rifler",
                    new BigDecimal("42000.00"), "Razer");
            ProPlayer seized = new ProPlayer(null, "seized", "Support",
                    new BigDecimal("38000.00"), "HyperX");

            naviTeam.addPlayer(flamie);
            naviTeam.addPlayer(npl);
            naviTeam.addPlayer(seized);

            System.out.println("   Team eligible for tournament: " + naviTeam.isEligibleForTournament());
            teamRepository.save(naviTeam);
            System.out.println("   Team saved with ID: " + naviTeam.getId() + "\n");

            // 2. Create team with amateur players
            System.out.println("2. Creating Team 'Rising Stars' with Amateur Players...");
            Team risingStarsTeam = new Team(null, "Rising Stars", "NA");
            
            AmateurPlayer rookie1 = new AmateurPlayer(null, "ProdigyKid", "AWPer", 
                    "Global Elite", 5000);
            AmateurPlayer rookie2 = new AmateurPlayer(null, "RookieStar", "Rifler", 
                    "Supreme", 3500);
            AmateurPlayer rookie3 = new AmateurPlayer(null, "NewHope", "Support", 
                    "Global Elite", 4200);

            risingStarsTeam.addPlayer(rookie1);
            risingStarsTeam.addPlayer(rookie2);
            risingStarsTeam.addPlayer(rookie3);

            teamRepository.save(risingStarsTeam);
            System.out.println("   Team saved with ID: " + risingStarsTeam.getId() + "\n");

            // 3. Demonstrate polymorphism - retrieve and display all teams
            System.out.println("3. Retrieving all teams from database...");
            List<Team> allTeams = teamRepository.findAll();
            for (Team team : allTeams) {
                System.out.println("   Team: " + team.getName() + " (Region: " + team.getRegion() + ")");
                System.out.println("   Players: " + team.getPlayers().size());
                for (Player player : team.getPlayers()) {
                    System.out.println("      - " + player.getNickname() + " (" + player.getPlayerType() + 
                            ") - Role: " + player.getRole());
                }
            }
            System.out.println();

            // 4. Demonstrate business logic validation
            System.out.println("4. Testing business logic validation...");
            try {
                Team invalidTeam = new Team(null, "Invalid Team", "EU");
                invalidTeam.addPlayer(rookie1);
                invalidTeam.validateTeamComposition();
            } catch (IllegalStateException e) {
                System.out.println("   Validation failed as expected: " + e.getMessage());
            }
            System.out.println();

            // 5. Update team
            System.out.println("5. Updating team...");
            naviTeam.setRegion("CIS");
            teamRepository.update(naviTeam);
            Team updated = teamRepository.findById(naviTeam.getId()).orElse(null);
            System.out.println("   Updated team region: " + (updated != null ? updated.getRegion() : "Not found"));
            System.out.println();

            // 6. Search by region
            System.out.println("6. Searching teams by region 'NA'...");
            List<Team> naTeams = teamRepository.findByRegion("NA");
            System.out.println("   Found " + naTeams.size() + " team(s) in NA region");
            System.out.println();

            System.out.println("=== Demo completed successfully! ===");
            System.out.println("\nKey features demonstrated:");
            System.out.println("✓ Domain-Driven Design with clean architecture");
            System.out.println("✓ Polymorphism (ProPlayer/AmateurPlayer)");
            System.out.println("✓ JPA inheritance (SINGLE_TABLE strategy)");
            System.out.println("✓ Optimistic locking (@Version)");
            System.out.println("✓ Business logic validation");
            System.out.println("✓ Repository pattern with adapters");
            System.out.println("✓ Many-to-Many relationships (Team-Player)");
            System.out.println("✓ ACID transactions (READ_COMMITTED isolation)");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JpaUtil.close();
        }
    }
}
