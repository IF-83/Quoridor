package com.codecool.websocket.models.repository;

import com.codecool.websocket.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    //OK
}
