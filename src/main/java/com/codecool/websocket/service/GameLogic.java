package com.codecool.websocket.service;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.Game;
import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.moveCheckers.CheckUtility;
import com.codecool.websocket.service.moveCheckers.StepChecker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.util.List;

@Data
public class GameLogic {
    private Game game;
    private List<Cell> cells;
    private String winner;
    private String nextPlayer;
    private int availableWallsPlayer1;
    private int availableWallsPlayer2;
    private MoveOutcomeType moveOutcomeType;

    public GameLogic(Game game) {
        this.game = game;
        this.winner = game.getWinner();
        this.nextPlayer = game.getNextPlayer();
        this.availableWallsPlayer1=game.getAvailableWallsPlayer1();
        this.availableWallsPlayer2= game.getAvailableWallsPlayer2();
        this.cells = new Gson().fromJson(game.getCellsJson(),new TypeToken<List<Cell>>() {}.getType());
    }

    public void tryMove (int cellID) {
        CheckUtility checkUtility = new CheckUtility(cells, nextPlayer);
        Cell cell = cells.get(cellID - 1);
        if (cell.getType().equals("stepField")) {
            this.moveOutcomeType = new StepChecker().checkStep(cellID, checkUtility);
        } else if (!cell.getType().equals("corner")){
            this.moveOutcomeType = checkWallPlacement(cellID);
        }else if (isPlayerBlocked(cellID)){
            this.moveOutcomeType = MoveOutcomeType.PLAYER_SURROUNDED;
        } else {
            this.moveOutcomeType = MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }
    }

//Sets enum to "PLAYER_SURROUNDED
    private boolean isPlayerBlocked(int cellID) {
        return false;
    }

    private MoveOutcomeType checkWallPlacement(int cellID) {
        Cell cell = cells.get(cellID - 1);

        if (cell.getWallType().equals("solid")){
            return MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }

        if (nextPlayer.equals("player1")
                && availableWallsPlayer1 == 0){
            return MoveOutcomeType.NO_WALLS_LEFT;
        }

        if (nextPlayer.equals("player2")
                && availableWallsPlayer2 == 0){
            return MoveOutcomeType.NO_WALLS_LEFT;
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
            return MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }

        if (adjacentCorner.getWallType().equals("solid")
                || adjacentWall.getWallType().equals("solid")){
            return MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }

        cell.setWallType("solid");
        adjacentCorner.setWallType("solid");
        adjacentWall.setWallType("solid");

        if (nextPlayer.equals("player1")){
            this.availableWallsPlayer1--;
            this.game.setAvailableWallsPlayer1(this.availableWallsPlayer1);
        }

        if (nextPlayer.equals("player2")){
            this.availableWallsPlayer2--;
            this.game.setAvailableWallsPlayer2(this.availableWallsPlayer2);
        }

        return MoveOutcomeType.SUCCESS;
    }

    private MoveOutcomeType executeStepOrJump (int currentCellID, int targetCellID) {//maybe try-catch
        cells.get(currentCellID - 1).setPlayer("player0"); // -1 to transform ID into index
        cells.get(targetCellID - 1).setPlayer(nextPlayer); // -1 to transform ID into index
        return MoveOutcomeType.SUCCESS;
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

