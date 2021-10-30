package com.codecool.websocket.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
public class Cell {

    @ManyToOne
    private Game game;

    private String type;
    private String player;
    private String direction;
    private String wallType;
    private String id;

    @Id
    @GeneratedValue( strategy= GenerationType.AUTO )
    private Long cellId;
}


