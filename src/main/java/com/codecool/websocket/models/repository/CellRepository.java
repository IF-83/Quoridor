package com.codecool.websocket.models.repository;

import com.codecool.websocket.models.Cell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CellRepository extends JpaRepository<Cell, Long> {
}
