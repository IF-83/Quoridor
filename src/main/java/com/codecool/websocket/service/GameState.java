package com.codecool.websocket.service;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.GameData;
import com.codecool.websocket.models.MoveOutcomeType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.util.List;

@Data
public class GameState {
    public GameData getGameData() {
        return gameData;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public String getWinner() {
        return winner;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public int getAvailableWallsPlayer1() {
        return availableWallsPlayer1;
    }

    public int getAvailableWallsPlayer2() {
        return availableWallsPlayer2;
    }

    public MoveOutcomeType getMoveOutcomeType() {
        return moveOutcomeType;
    }

    public int getTargetCellID() {
        return targetCellID;
    }

    public int getCurrentCellID() {
        return currentCellID;
    }

    public void setNextPlayer(String nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public void setAvailableWallsPlayer1(int availableWallsPlayer1) {
        this.availableWallsPlayer1 = availableWallsPlayer1;
    }

    public void setAvailableWallsPlayer2(int availableWallsPlayer2) {
        this.availableWallsPlayer2 = availableWallsPlayer2;
    }

    public void setMoveOutcomeType(MoveOutcomeType moveOutcomeType) {
        this.moveOutcomeType = moveOutcomeType;
    }

    private GameData gameData;
    private List<Cell> cells;
    private String winner;
    private String nextPlayer;
    private int availableWallsPlayer1;
    private int availableWallsPlayer2;
    private MoveOutcomeType moveOutcomeType;
    private int targetCellID;
    private int currentCellID;

    public GameState(GameData gameData, int targetCellID) {
        this.targetCellID = targetCellID;
        this.gameData = gameData;
        this.winner = gameData.getWinner();
        this.nextPlayer = gameData.getNextPlayer();
        this.availableWallsPlayer1= gameData.getAvailableWallsPlayer1();
        this.availableWallsPlayer2= gameData.getAvailableWallsPlayer2();
        this.cells = new Gson().fromJson(gameData.getCellsJson(),new TypeToken<List<Cell>>() {}.getType());
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

