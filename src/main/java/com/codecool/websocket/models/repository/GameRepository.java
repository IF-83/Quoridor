package com.codecool.websocket.models.repository;

import com.codecool.websocket.models.GameData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameData, Long> {
    //OK
}
