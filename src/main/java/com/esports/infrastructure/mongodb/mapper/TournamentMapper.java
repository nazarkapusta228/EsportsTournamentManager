package com.esports.infrastructure.mongodb.mapper;

import com.esports.domain.model.LanTournament;
import com.esports.domain.model.OnlineTournament;
import com.esports.domain.model.Tournament;
import com.esports.domain.model.TournamentStatus;
import com.esports.infrastructure.mongodb.document.TournamentDocument;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class TournamentMapper {
    private TournamentMapper() { }

    public static TournamentDocument toDocument(Tournament tournament) {
        if (tournament == null) return null;
        TournamentDocument document = new TournamentDocument();
        document.setId(tournament.getId());
        document.setType(tournament.getTournamentType());
        document.setName(tournament.getName());
        document.setStartDate(tournament.getStartDate());
        document.setEndDate(tournament.getEndDate());
        document.setPrizePool(tournament.getPrizePool());
        document.setStatus(tournament.getStatus() == null ? null : tournament.getStatus().name());
        document.setMaxTeams(tournament.getMaxTeams());
        document.setRegisteredTeamIds(tournament.getRegisteredTeams().stream()
                .map(team -> team.getId())
                .collect(Collectors.toCollection(ArrayList::new)));
        if (tournament instanceof OnlineTournament online) {
            document.setStreamUrl(online.getStreamUrl());
            document.setServerLocation(online.getServerLocation());
        } else if (tournament instanceof LanTournament lan) {
            document.setVenue(lan.getVenue());
            document.setCity(lan.getCity());
        } else {
            throw new IllegalArgumentException("Unsupported tournament type: " + tournament.getClass().getName());
        }
        return document;
    }

    public static Tournament toDomain(TournamentDocument document) {
        if (document == null) return null;
        Tournament tournament = switch (document.getType()) {
            case "ONLINE" -> new OnlineTournament(document.getId(), document.getName(), document.getStartDate(),
                    document.getEndDate(), document.getPrizePool(), document.getMaxTeams(),
                    document.getStreamUrl(), document.getServerLocation());
            case "LAN" -> new LanTournament(document.getId(), document.getName(), document.getStartDate(),
                    document.getEndDate(), document.getPrizePool(), document.getMaxTeams(),
                    document.getVenue(), document.getCity());
            default -> throw new IllegalArgumentException("Unknown tournament type: " + document.getType());
        };
        if (document.getStatus() != null) {
            tournament.setStatus(TournamentStatus.valueOf(document.getStatus()));
        }
        return tournament;
    }
}
