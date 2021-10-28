package com.codecool.websocket.storage;


import com.codecool.websocket.models.MoveOutcomeTypes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.*;

import javax.persistence.*;
import java.util.List;


//@Builder
@Data
@Entity
@NoArgsConstructor
public class Game {

//    @Singular
//    @EqualsAndHashCode.Exclude
//    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Transient
    private List<Cell> cells;

    @Lob
    private String cellsJson;

    private String nextPlayer;
    private int availableWallsPlayer1 = 10;
    private int availableWallsPlayer2 = 10;


    @Id
    @GeneratedValue( strategy= GenerationType.AUTO )
    private Long gameId;

    public void setJsonFromCells(){
        System.out.println("entered json creatiion");
        this.cellsJson = new Gson().toJson(cells, new TypeToken<List<Cell>>() {}.getType());
        System.out.println("passed through json creatiion");

    }


    public void setCellsFromJson(){
        Gson gson = new Gson();
        this.cells = gson.fromJson(this.cellsJson,new TypeToken<List<Cell>>() {}.getType());
        //cells.forEach(x -> x.setGame(this));
    }

    public MoveOutcomeTypes tryMove (int cellID) {
        MoveOutcomeTypes result;
        Cell cell = cells.get(cellID - 1);
        if (cell.getType().equals("stepField")) {
            result = checkStep(cellID);
        } else if (!cell.getType().equals("corner")){
            result = checkWallPlacement(cellID);
        } else {
            result = MoveOutcomeTypes.INVALID_WALL_PLACEMENT;
        }
        return result;
    }

    //TODO: complete function
    private MoveOutcomeTypes checkWallPlacement(int cellID) {
        return null;
    }


    private int findNextPlayerCellID() {
        int currentCellID = -1;
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).getPlayer().equals(nextPlayer)) {
                currentCellID = i + 1; // cell ID numbering starts from 1
                break;
            }
        }
        return currentCellID;
    }

    private MoveOutcomeTypes checkStep(int cellID) {
        int currentCellID = findNextPlayerCellID();
        int difference = cellID - currentCellID;
        int absDifference = Math.abs(difference);
        // STEP
        if (absDifference == 2 || absDifference == 34) {
            if (isWallBetween(currentCellID, cellID) || isOccupied(cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            // STRAIGHT JUMP
        } else if (absDifference == 4 || absDifference == 68) {
            if (isWallBetween(currentCellID, cellID) || !isPlayerBetween(currentCellID, cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            // DIAGONAL JUMP
        } else if (absDifference == 36 || absDifference == 32) {
            if (!isValidDiagonalJump(absDifference, difference, currentCellID, cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
        }
        // TODO: invalidate jumps that are bigger than possible
        cells.get(currentCellID - 1).setPlayer("player0"); // -1 to transform ID into index
        cells.get(cellID - 1).setPlayer(nextPlayer); // -1 to transform ID into index
        return MoveOutcomeTypes.SUCCESS;

    }

    private boolean isWallBetween (int currentCellID, int targetCellID) {
        int cellIDToCheck = (currentCellID + targetCellID) / 2;
        return (cells.get(cellIDToCheck).getType().equals("wall"));
    }

    private boolean isOccupied (int targetCellID) {
        return !cells.get(targetCellID).getPlayer().equals("player0");
    }

    private boolean isPlayerBetween (int currentCellID, int targetCellID) {
        int cellIDToCheck = (currentCellID + targetCellID) / 2;
        return !cells.get(cellIDToCheck).getPlayer().equals("player0");
    }

    // TODO: check of opponent is on edge of board
    private boolean isValidDiagonalJump(int absDifference, int difference, int currentCellID, int cellID) {
        // -1 is to transform ID into index, +-2 is check the adjacent cells in that row
        int diffSign = (int) Math.signum(difference);
        if (absDifference == 32) {
            int cellIDTopLeft = Math.min(currentCellID, cellID) - 2;
            int cellIDBottomRight = Math.max(currentCellID, cellID) + 2;
            if (!cells.get(cellIDTopLeft - 1).getPlayer().equals("player0")) {
                int cellIDBehindOpponent = diffSign>0 ? cellIDTopLeft-1 : cellIDTopLeft-17;
                if (isWall (cellIDBehindOpponent)) {
                    return true;
                }
            } else if (!cells.get(cellIDBottomRight - 1).getPlayer().equals("player0")) {
                int cellIDBehindOpponent = diffSign>0 ? cellIDBottomRight+17 : cellIDBottomRight+1;
                if (isWall (cellIDBehindOpponent)) {
                    return true;
                }
            }
        } else if (absDifference == 36) {
            int cellIDTopRight = Math.min(currentCellID, cellID) + 2;
            int cellIBottomLeft = Math.max(currentCellID, cellID) - 2;
            if (!cells.get(cellIDTopRight - 1).getPlayer().equals("player0")) {
                int cellIDBehindOpponent = diffSign>0 ? cellIDTopRight+1 : cellIDTopRight-17;
                if (isWall (cellIDBehindOpponent)) {
                    return true;
                }
            } else if (!cells.get(cellIBottomLeft - 1).getPlayer().equals("player0")) {
                int cellIDBehindOpponent = diffSign>0 ? cellIBottomLeft+17 : cellIBottomLeft-1;
                if (isWall (cellIDBehindOpponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWall (int cellID) {
        return cells.get(cellID - 1).getWallType().equals("empty");
    }
}

