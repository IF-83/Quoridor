package com.codecool.websocket.storage;


import lombok.*;

import javax.persistence.*;
import java.util.List;


//@OneToMany(mappedBy = "school", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//@EqualsAndHashCode.Exclude
//private Set<Student> students;



//@Builder
@Data
@Entity
@NoArgsConstructor
public class Game {

//    @Singular
//    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Cell> cells;

    private String nextPlayer;

    @Id
    @GeneratedValue( strategy= GenerationType.AUTO )
    private Long gameId;
}

