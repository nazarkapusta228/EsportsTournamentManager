package com.esports.infrastructure.database.adapter;

import com.esports.domain.model.Player;
import com.esports.infrastructure.database.entity.PlayerEnt;

public class PlayerMapper {

    public static Player toDomain(PlayerEnt ent) {
        if (ent == null) return null;
        return new Player(ent.getId(), ent.getNickname(), ent.getRole());
    }

    public static PlayerEnt toEntity(Player domain) {
        if (domain == null) return null;
        return new PlayerEnt(domain.getId(), domain.getNickname(), domain.getRole());
    }
}