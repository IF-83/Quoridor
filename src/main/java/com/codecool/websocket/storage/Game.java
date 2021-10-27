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
        int difference = Math.abs(cellID - currentCellID);
        // STEP
        if (difference == 2 || difference == 34) {
            if (isWallBetween(currentCellID, cellID) || isOccupied(cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
        // STRAIGHT JUMP
        } else if (difference == 4 || difference == 68) {
            if (isWallBetween(currentCellID, cellID) || !isPlayerBetween(currentCellID, cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
        // DIAGONAL JUMP
        } else if (difference == 36 || difference == 32) {
            //TODO: test isPlayerForDiagonalJump
            if (!isPlayerForDiagonalJump(difference, currentCellID, cellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            //TODO: create isWallForDiagonalJump and IsEndOfBoardForDiagonalJump AND connect with isPlayerForDiagonalJump
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

    private boolean isPlayerForDiagonalJump (int difference, int currentCellID, int cellID) {
        // -1 is to transform ID into index, +-2 is check the adjacent cells in that row
        if (difference == 32) {
            if (!cells.get(Math.min(currentCellID, cellID) -1 - 2).getPlayer().equals("player0")) {
                return true;
            } else if (!cells.get(Math.max(currentCellID, cellID) - 1 + 2).getPlayer().equals("player0")) {
                return true;
            }
        } else if (difference == 36) {
            if (!cells.get(Math.min(currentCellID, cellID) - 1 + 2).getPlayer().equals("player0")) {
                return true;
            } else if (!cells.get(Math.max(currentCellID, cellID) - 1 - 2).getPlayer().equals("player0")) {
                return true;
            }
        }
        return false;
    }
}

