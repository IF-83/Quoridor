package com.codecool.websocket.service;
import java.util.ArrayList;
import java.util.List;

public class BoardGraph {

    List<FieldNode> nodes;
    public BoardGraph(GameLogic gameLogic) {
        int boardSize = 81; //9 rows X 9 columns
        for (int i = 0; i < boardSize; i++) {
            if (i < 9){
                nodes.add(new FieldNode(2));
            } else if (i >= 72) {
                nodes.add(new FieldNode(1));
            } else {
                nodes.add(new FieldNode(0));
            }
        }
    }

}
