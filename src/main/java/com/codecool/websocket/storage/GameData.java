package com.codecool.websocket.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GameData {
    
    private ArrayList<Integer> games;


    public GameData (){
        games = new ArrayList<Integer>(List.of(0,1,2));
    }


    public Integer getGame(Integer gameId) {
        return games.get(gameId);
    }
    public Integer createGame(){
        return 1;
    }
}
