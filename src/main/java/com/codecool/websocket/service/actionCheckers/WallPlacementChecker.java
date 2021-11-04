package com.codecool.websocket.service.actionCheckers;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.GameState;

public class WallPlacementChecker {

    private GameState gameState;

    public WallPlacementChecker(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveOutcomeType checkWallPlacement(int cellID) {
        Cell cell = gameState.getCells().get(cellID - 1);

        if (cell.getWallType().equals("solid")){
            return MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }

        if (gameState.getNextPlayer().equals("player1")
                && gameState.getAvailableWallsPlayer1() == 0){
            return MoveOutcomeType.NO_WALLS_LEFT;
        }

        if (gameState.getNextPlayer().equals("player2")
                && gameState.getAvailableWallsPlayer2() == 0){
            return MoveOutcomeType.NO_WALLS_LEFT;
        }

        Cell adjacentCorner = null;
        Cell adjacentWall = null;
        if (cell.getDirection().equals("vertical")){
            adjacentCorner = gameState.getCells().get(cellID + 16);
            adjacentWall = gameState.getCells().get(cellID + 33);
        } else if (cell.getDirection().equals("horizontal")){
            adjacentCorner = gameState.getCells().get(cellID);
            adjacentWall = gameState.getCells().get(cellID + 1);
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

        if (gameState.getNextPlayer().equals("player1")){
            gameState.setAvailableWallsPlayer1(gameState.getAvailableWallsPlayer1() - 1);
            gameState.getGameData().setAvailableWallsPlayer1(gameState.getAvailableWallsPlayer1());
        }

        if (gameState.getNextPlayer().equals("player2")){
            gameState.setAvailableWallsPlayer2(gameState.getAvailableWallsPlayer2() - 1);
            gameState.getGameData().setAvailableWallsPlayer2(gameState.getAvailableWallsPlayer2());
        }

        return MoveOutcomeType.SUCCESS;
    }

}
