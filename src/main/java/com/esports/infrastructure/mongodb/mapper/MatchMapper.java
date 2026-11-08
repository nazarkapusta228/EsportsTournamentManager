package com.esports.infrastructure.mongodb.mapper;

import com.esports.domain.model.Group;
import com.esports.domain.model.Match;
import com.esports.domain.model.MatchStatus;
import com.esports.domain.model.Team;
import com.esports.infrastructure.mongodb.document.MatchDocument;

public final class MatchMapper {
    private MatchMapper() { }

    public static MatchDocument toDocument(Match match) {
        if (match == null) return null;
        MatchDocument document = new MatchDocument();
        document.setId(match.getId());
        document.setTeam1Id(match.getTeam1() == null ? null : match.getTeam1().getId());
        document.setTeam2Id(match.getTeam2() == null ? null : match.getTeam2().getId());
        document.setTeam1Score(match.getTeam1Score());
        document.setTeam2Score(match.getTeam2Score());
        document.setMatchDate(match.getMatchDate());
        document.setStatus(match.getStatus() == null ? null : match.getStatus().name());
        document.setGroupId(match.getGroup() == null ? null : match.getGroup().getId());
        document.setRound(match.getRound());
        return document;
    }

    public static Match toDomain(MatchDocument document, Team team1, Team team2, Group group) {
        if (document == null) return null;
        Match match = new Match(document.getId(), team1, team2, document.getMatchDate(), document.getRound());
        match.setTeam1Score(document.getTeam1Score());
        match.setTeam2Score(document.getTeam2Score());
        match.setStatus(document.getStatus() == null ? null : MatchStatus.valueOf(document.getStatus()));
        match.setGroup(group);
        return match;
    }
}
