package com.codecool.websocket.service.actionCheckers;

import com.codecool.websocket.service.BoardState;

public class DiagJumpVerifier {

    private BoardState boardState;

    public DiagJumpVerifier(BoardState boardState) {
        this.boardState = boardState;
    }

    //TODO: check for wall too behind opponent, not just edge of board
    public boolean hasDiagJumpReqs (int cellIDToCheck, int diffSign, int posDiffAddition, int negDiffAddition) {
        int cellIDBehindOpponent;
        if (boardState.isOccupied(cellIDToCheck)) {
            if (diffSign > 0) {
                cellIDBehindOpponent = cellIDToCheck + posDiffAddition;
                if (boardState.isOppOnEdgeOfBoard(cellIDBehindOpponent, posDiffAddition)) {
                    return true;
                }
            } else {
                cellIDBehindOpponent = cellIDToCheck + negDiffAddition;
                if (boardState.isOppOnEdgeOfBoard(cellIDBehindOpponent, negDiffAddition)) {
                    return true;
                }
            }
        }
        return false;
    }

}
