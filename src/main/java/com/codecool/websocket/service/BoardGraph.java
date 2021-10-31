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
        System.out.println("vertical");
        for (cell = 0; cell <= 71; cell++){
            if (cell % 9 == 0){
                wallIndex = 34 * cell/9 + 17;
            } else {
                wallIndex += 2;
            }
            //System.out.println("connecting nodes " + cell + " and " + (cell+9) + " with wall " + wallIndex);
            FieldNode upper = nodes.get(cell);
            FieldNode lower = nodes.get(cell + 9);
            if (gameLogic.getCells().get(wallIndex).getWallType().equals("empty")) {
                upper.setDown(lower);
                lower.setUp(upper);
                //System.out.println("edge set up");
            }
        }

        //horizontal edges
        System.out.println("horizontal");
        for (cell = 0, wallIndex = 1; cell < 80; cell = (cell + 9) % 80, wallIndex = (wallIndex + 34) % 304) {
            FieldNode leftNode = nodes.get(cell);
            FieldNode rightNode = nodes.get(cell + 1);
            //System.out.println("connecting nodes " + cell + " and " + (cell+1) + " with wall " + wallIndex);
            if (gameLogic.getCells().get(wallIndex).getWallType().equals("empty")){
                leftNode.setRight(rightNode);
                rightNode.setLeft(leftNode);
                //System.out.println("edge set up");
            }
            if (cell == 79) break; //after 79 cell would be 8, but that is in the last col
        }

    } // end constructor



} // end class
