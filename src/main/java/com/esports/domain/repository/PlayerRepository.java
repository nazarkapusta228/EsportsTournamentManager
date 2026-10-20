package com.esports.domain.repository;

import com.esports.domain.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    void save(Player player);
    void update(Player player);
    void delete(Long id);
    Optional<Player> findById(Long id);
    List<Player> findAll();
    Optional<Player> findByNickname(String nickname);
    List<Player> findByPlayerType(String playerType);
}
