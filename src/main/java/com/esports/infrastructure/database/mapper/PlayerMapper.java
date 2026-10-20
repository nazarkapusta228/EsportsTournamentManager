package com.esports.infrastructure.database.mapper;

import com.esports.domain.model.AmateurPlayer;
import com.esports.domain.model.Player;
import com.esports.domain.model.ProPlayer;
import com.esports.infrastructure.database.entity.AmateurPlayerEnt;
import com.esports.infrastructure.database.entity.PlayerEnt;
import com.esports.infrastructure.database.entity.ProPlayerEnt;

public class PlayerMapper {

    public static Player toDomain(PlayerEnt entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof ProPlayerEnt) {
            ProPlayerEnt proEnt = (ProPlayerEnt) entity;
            return new ProPlayer(
                    proEnt.getId(),
                    proEnt.getNickname(),
                    proEnt.getRole(),
                    proEnt.getSalary(),
                    proEnt.getSponsor()
            );
        } else if (entity instanceof AmateurPlayerEnt) {
            AmateurPlayerEnt amateurEnt = (AmateurPlayerEnt) entity;
            return new AmateurPlayer(
                    amateurEnt.getId(),
                    amateurEnt.getNickname(),
                    amateurEnt.getRole(),
                    amateurEnt.getRank(),
                    amateurEnt.getHoursPlayed()
            );
        }

        throw new IllegalArgumentException("Unknown player type: " + entity.getClass().getName());
    }

    public static PlayerEnt toEntity(Player domain) {
        if (domain == null) {
            return null;
        }

        if (domain instanceof ProPlayer) {
            ProPlayer pro = (ProPlayer) domain;
            ProPlayerEnt entity = new ProPlayerEnt(
                    pro.getNickname(),
                    pro.getRole(),
                    pro.getSalary(),
                    pro.getSponsor()
            );
            entity.setId(pro.getId());
            return entity;
        } else if (domain instanceof AmateurPlayer) {
            AmateurPlayer amateur = (AmateurPlayer) domain;
            AmateurPlayerEnt entity = new AmateurPlayerEnt(
                    amateur.getNickname(),
                    amateur.getRole(),
                    amateur.getRank(),
                    amateur.getHoursPlayed()
            );
            entity.setId(amateur.getId());
            return entity;
        }

        throw new IllegalArgumentException("Unknown player type: " + domain.getClass().getName());
    }

    public static void updateEntity(Player domain, PlayerEnt entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setNickname(domain.getNickname());
        entity.setRole(domain.getRole());

        if (domain instanceof ProPlayer && entity instanceof ProPlayerEnt) {
            ProPlayer pro = (ProPlayer) domain;
            ProPlayerEnt proEnt = (ProPlayerEnt) entity;
            proEnt.setSalary(pro.getSalary());
            proEnt.setSponsor(pro.getSponsor());
        } else if (domain instanceof AmateurPlayer && entity instanceof AmateurPlayerEnt) {
            AmateurPlayer amateur = (AmateurPlayer) domain;
            AmateurPlayerEnt amateurEnt = (AmateurPlayerEnt) entity;
            amateurEnt.setRank(amateur.getRank());
            amateurEnt.setHoursPlayed(amateur.getHoursPlayed());
        }
    }
}
