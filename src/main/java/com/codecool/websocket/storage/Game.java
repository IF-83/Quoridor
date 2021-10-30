package com.codecool.websocket.storage;


import com.codecool.websocket.models.MoveOutcomeTypes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.*;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;


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

    private String winner;
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


    private MoveOutcomeTypes checkWallPlacement(int cellID) {
        Cell cell = cells.get(cellID - 1);

        if (cell.getWallType().equals("solid")){
            return MoveOutcomeTypes.INVALID_WALL_PLACEMENT;
        }

        if (nextPlayer.equals("player1")
            && availableWallsPlayer1 == 0){
            return MoveOutcomeTypes.NO_WALLS_LEFT;
        }

        if (nextPlayer.equals("player2")
                && availableWallsPlayer2 == 0){
            return MoveOutcomeTypes.NO_WALLS_LEFT;
        }

        Cell adjacentCorner = null;
        Cell adjacentWall = null;
        if (cell.getDirection().equals("vertical")){
            adjacentCorner = cells.get(cellID + 16);
            adjacentWall = cells.get(cellID + 33);
        } else if (cell.getDirection().equals("horizontal")){
            adjacentCorner = cells.get(cellID);
            adjacentWall = cells.get(cellID + 1);
        } else {
            return MoveOutcomeTypes.INVALID_WALL_PLACEMENT;
        }

        if (adjacentCorner.getWallType().equals("solid")
                || adjacentWall.getWallType().equals("solid")){
                return MoveOutcomeTypes.INVALID_WALL_PLACEMENT;
        }

        cell.setWallType("solid");
        adjacentCorner.setWallType("solid");
        adjacentWall.setWallType("solid");

        if (nextPlayer.equals("player1")){
            this.availableWallsPlayer1--;
        }

        if (nextPlayer.equals("player2")){
            this.availableWallsPlayer2--;
        }

        return MoveOutcomeTypes.SUCCESS;
    }



    private int findNextPlayerCellID() {
        int currentCellID = -1;
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).getPlayer().equals(nextPlayer)) {
                currentCellID = i + 1; // +1 transforms index into ID
                break;
            }
        }
        return currentCellID;
    }

    private MoveOutcomeTypes checkStep(int targetCellID) {
        int currentCellID = findNextPlayerCellID();
        int difference = targetCellID - currentCellID;
        int absDifference = Math.abs(difference);
        boolean caseStep = absDifference == 2 || absDifference == 34; // 2 and 34 are the ID differences of horizontal and vertical steps
        boolean caseJump = absDifference == 4 || absDifference == 68; // 4 and 68 are the ID differences of horizontal and vertical jumps
        boolean caseDiagJump = absDifference == 36 || absDifference == 32; // 36 and 32 are the ID differences of diagonal jumps
        if (caseStep) {
            if (isWallBetween(currentCellID, targetCellID) || isOccupied(targetCellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        } else if (caseJump) {
            if (isWallBetween(currentCellID, targetCellID) || !isPlayerBetween(currentCellID, targetCellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        } else if (caseDiagJump) {
            if (!isValidDiagonalJump(absDifference, difference, currentCellID, targetCellID)) {
                return MoveOutcomeTypes.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        }
        return MoveOutcomeTypes.INVALID_STEP;
    }

    private MoveOutcomeTypes executeStepOrJump (int currentCellID, int targetCellID) {
        cells.get(currentCellID - 1).setPlayer("player0"); // -1 to transform ID into index
        cells.get(targetCellID - 1).setPlayer(nextPlayer); // -1 to transform ID into index
        return MoveOutcomeTypes.SUCCESS;
    }

    private boolean isWallBetween (int currentCellID, int targetCellID) {
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

    private boolean isOccupied (int targetCellID) {
        return !cells.get(targetCellID -1).getPlayer().equals("player0");
    }

    private boolean isPlayerBetween (int currentCellID, int targetCellID) {
        int cellIDToCheck = (currentCellID + targetCellID) / 2;
        return !cells.get(cellIDToCheck -1).getPlayer().equals("player0");
    }

    private boolean isValidDiagonalJump(int absDifference, int difference, int currentCellID, int cellID) {
        // +-2 is to check the adjacent stepFields in a row
        // +-17 is to check adjacent cells in a column
        // +-1 is to check adjacent cells in a row
        int diffSign = (int) Math.signum(difference);
        // /-jump
        if (absDifference == 32) {
            int cellIDTopLeft = Math.min(currentCellID, cellID) - 2;
            int cellIDBottomRight = Math.max(currentCellID, cellID) + 2;
            if (hasDiagJumpReqs(cellIDTopLeft, diffSign, -1, -17)) {
                return true;
            }
            if (hasDiagJumpReqs(cellIDBottomRight, diffSign, +17, +1)) {
                return true;
            }
        // \-jump
        } else if (absDifference == 36) {
            int cellIDTopRight = Math.min(currentCellID, cellID) + 2;
            int cellIBottomLeft = Math.max(currentCellID, cellID) - 2;
            if (hasDiagJumpReqs(cellIDTopRight, diffSign, +1, -17)) {
                return true;
            }
            if (hasDiagJumpReqs(cellIBottomLeft, diffSign, +17, -1)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDiagJumpReqs (int cellIDToCheck, int diffSign, int posDiffAddition, int negDiffAddition) {
        int cellIDBehindOpponent;
        if (!cells.get(cellIDToCheck - 1).getPlayer().equals("player0")) {
            if (diffSign > 0) {
                cellIDBehindOpponent = cellIDToCheck + posDiffAddition;
                if (isOppOnEdgeOfBoard(cellIDBehindOpponent, posDiffAddition)) {
                    return true;
                }
            } else {
                cellIDBehindOpponent = cellIDToCheck + negDiffAddition;
                if (isOppOnEdgeOfBoard(cellIDBehindOpponent, negDiffAddition)) {
                    return true;
                }
            }
            if (isWall(cellIDBehindOpponent)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOppOnEdgeOfBoard (int cellIDBehindOpponent, int addition) {
        if (Math.abs(addition) > 1) {
            return isOutOfBoard(cellIDBehindOpponent);
        } else {
            return isOutOfBoard(cellIDBehindOpponent) || isHorizWall(cellIDBehindOpponent);
        }
    }

    private boolean isWall (int cellID) {
        return cells.get(cellID - 1).getWallType().equals("solid");
    }

    private boolean isOutOfBoard (int cellID) {
        return cellID < 1 || cellID > 289;
    }

    private boolean isHorizWall (int cellID) {
        return cells.get(cellID - 1).getDirection().equals("horizontal");
    }

    public void whoHasWon() {
        for (int i = 272; i <= 288; i += 2) {
            if (cells.get(i).getPlayer().equals("player1")){
                 this.winner="player1";
            }
        }
        for (int i = 0; i <= 16; i += 2) {
            if (cells.get(i).getPlayer().equals("player2")){
                this.winner = "player2";
            }
        }
    }
}

