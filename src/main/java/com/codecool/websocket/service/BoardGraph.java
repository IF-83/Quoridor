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
        int firstCellInEightsRow = 63;
        int firstWallInRow = 17;
        int wallIndex = -1;
        for (int i = 0; i <= firstCellInEightsRow; i += 9){
            wallIndex = firstWallInRow;
            for (int j = i; j <= i + 8; j++) {  // i + 8 is the last cell in the row
                FieldNode upperField = nodes.get(j);
                FieldNode lowerField = nodes.get(j+9);
                System.out.println("setting up edges between fields " + j + " and " + (j+9));
                System.out.println("wall between them is: " + wallIndex);
                if (gameLogic.getCells().get(wallIndex).getWallType().equals("empty")) {
                    upperField.setDown(lowerField);
                    lowerField.setUp(upperField);
                    System.out.println("edge is set");
                }
                wallIndex += 2;  //next is a stepField
            }
            firstWallInRow += 34; // jump 2 rows
        }
    } // end constructor



} // end class
