package com.codecool.websocket.service;
import java.util.ArrayList;
import java.util.List;

public class BoardGraph {

    List<FieldNode> nodes;
    public BoardGraph(GameLogic gameLogic) {
        nodes = new ArrayList<>();
        int boardSize = 81; //9 rows X 9 columns
        for (int i = 0; i < boardSize; i++) {
            if (i < 9){
                nodes.add(new FieldNode(2));
            } else if (i >= 72) {
                nodes.add(new FieldNode(1));
            } else {
                nodes.add(new FieldNode(0));
            }
        } // creating empty graph;
        System.out.println("empty graph created");
        // setting up edges:
        // vertical edges:
        int cell;
        int wallIndex = -1;
        for (cell = 0; cell <= 71; cell++){
            if (cell % 9 == 0){
                wallIndex = 34 * cell/9 + 17;
            } else {
                wallIndex += 2;
            }
            System.out.println("connecting nodes " + cell + " and " + (cell+9) + " with wall " + wallIndex);
            FieldNode upper = nodes.get(cell);
            FieldNode lower = nodes.get(cell + 9);
            if (gameLogic.getCells().get(wallIndex).getWallType().equals("empty")) {
                upper.setDown(lower);
                lower.setUp(upper);
                System.out.println("edge set up");
            }
        }

        //horizontal edges
        int firstCellInEightsCol = 7;


    } // end constructor



} // end class
