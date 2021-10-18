package com.codecool.websocket.storage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

//@Builder
@Data
@Entity
@NoArgsConstructor
public class Game {
//    @OneToMany
//    private List<Cell> cells;
    private String nextPlayer;
    @Id
    @GeneratedValue
    private Long gameId;
}

