package com.codecool.websocket.storage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
public class Game {

    private String nextPlayer;
    @Id
    @GeneratedValue
    private Long gameId;
}
