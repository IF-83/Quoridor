package com.codecool.websocket.service.moveCheckers;

import com.codecool.websocket.models.Cell;

import java.util.List;

public class CheckUtility {
    private List<Cell> cells;
    private String nextPlayer;

    public CheckUtility(List<Cell> cells, String nextPlayer) {
        this.cells = cells;
        this.nextPlayer = nextPlayer;
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

}
