package com.esports.infrastructure.mongodb.mapper;

import com.esports.domain.model.AmateurPlayer;
import com.esports.domain.model.Player;
import com.esports.domain.model.ProPlayer;
import com.esports.infrastructure.mongodb.document.PlayerDocument;

public final class PlayerMapper {
    private PlayerMapper() { }

    public static PlayerDocument toDocument(Player player) {
        if (player == null) return null;
        PlayerDocument document = new PlayerDocument();
        document.setId(player.getId());
        document.setNickname(player.getNickname());
        document.setRole(player.getRole());
        document.setType(player.getPlayerType());
        if (player instanceof ProPlayer pro) {
            document.setSalary(pro.getSalary());
            document.setSponsor(pro.getSponsor());
        } else if (player instanceof AmateurPlayer amateur) {
            document.setRank(amateur.getRank());
            document.setHoursPlayed(amateur.getHoursPlayed());
        } else {
            throw new IllegalArgumentException("Unsupported player type: " + player.getClass().getName());
        }
        return document;
    }

    public static Player toDomain(PlayerDocument document) {
        if (document == null) return null;
        return switch (document.getType()) {
            case "PRO" -> new ProPlayer(document.getId(), document.getNickname(), document.getRole(),
                    document.getSalary(), document.getSponsor());
            case "AMATEUR" -> new AmateurPlayer(document.getId(), document.getNickname(), document.getRole(),
                    document.getRank(), document.getHoursPlayed());
            default -> throw new IllegalArgumentException("Unknown player type: " + document.getType());
        };
    }
}
