package com.codecool.websocket.service;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.MoveOutcomeType;

import java.util.List;

public class BoardState {

    public Cell getCell (int CellID) {
        return cells.get(CellID - 1);
    }

    public int getCurrentCellID() {
        return currentCellID;
    }

    private int FIRST_CELL_ID = 1;
    private int LAST_CELL_ID = 289;
    private List<Cell> cells;
    private String nextPlayer;
    private int currentCellID;

    public BoardState(List<Cell> cells, String nextPlayer) {
        this.cells = cells;
        this.nextPlayer = nextPlayer;
        currentCellID = findNextPlayerCellID();
    }

    public int findNextPlayerCellID() {
        int currentCellID = -1;
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).getPlayer().equals(nextPlayer)) {
                currentCellID = i + 1; // +1 transforms index into ID
                break;
            }
        }
        return currentCellID;
    }

    public boolean isWall (int cellID) {
        return cells.get(cellID - 1).getWallType().equals("solid");
    }

    public boolean isWallBetween (int targetCellID) {
        int smaller = Math.min(currentCellID, targetCellID);
        if (Math.abs(currentCellID - targetCellID) > 4) {
            for (int i = 0; i + smaller < Math.max(currentCellID, targetCellID); i += 17) {
                if (isWall(smaller + i)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i + smaller < Math.max(currentCellID, targetCellID); i += 2) {
                if (isWall(smaller + i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOccupied (int targetCellID) {
        return !cells.get(targetCellID -1).getPlayer().equals("player0");
    }

    public void executeStepOrJump (int targetCellID) {//maybe try-catch
        cells.get(currentCellID - 1).setPlayer("player0"); // -1 to transform ID into index
        cells.get(targetCellID - 1).setPlayer(nextPlayer); // -1 to transform ID into index
    }

    public boolean isPlayerBetween (int targetCellID) {
        int cellIDToCheck = (currentCellID + targetCellID) / 2;
        return !cells.get(cellIDToCheck -1).getPlayer().equals("player0");
    }

    public boolean isOppOnEdgeOfBoard (int cellIDBehindOpponent, int addition) {
        if (Math.abs(addition) > 1) {
            return isOutOfBoard(cellIDBehindOpponent);
        } else {
            return isOutOfBoard(cellIDBehindOpponent) || isHorizWall(cellIDBehindOpponent);
        }
    }

    public boolean isOutOfBoard (int cellID) {
        return cellID < FIRST_CELL_ID || cellID > LAST_CELL_ID;
    }

    public boolean isHorizWall (int cellID) {
        return cells.get(cellID - 1).getDirection().equals("horizontal");
    }


    //Sets enum to "PLAYER_SURROUNDED
    public boolean isPlayerBlocked() {
        return false;
    }

}
