package com.codecool.websocket.storage.repository;

import com.codecool.websocket.storage.Cell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CellRepository extends JpaRepository<Cell, Long> {
}
