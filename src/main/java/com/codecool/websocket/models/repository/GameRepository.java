package com.codecool.websocket.storage.repository;

import com.codecool.websocket.storage.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    //OK
}
