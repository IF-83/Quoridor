package com.codecool.websocket.service.actionCheckers;

import com.codecool.websocket.service.BoardState;

public class DiagJumpValidator {

    private DiagJumpVerifier diagJumpVerifier;
    private BoardState boardState;
    private int absDifference;
    private int difference;
    private int currentCellID;
    private int targetCellID;

    public DiagJumpValidator(DiagJumpVerifier diagJumpVerifier, BoardState boardState, int targetCellID) {
        this.diagJumpVerifier = diagJumpVerifier;
        this.boardState = boardState;
        this.currentCellID = boardState.getCurrentCellID();
        this.targetCellID = targetCellID;
        this.difference = targetCellID - currentCellID;
        this.absDifference = Math.abs(difference);

    }

    public boolean isValidDiagonalJump() {
        // +-2 is to check the adjacent stepFields in a row
        // +-17 is to check adjacent cells in a column
        // +-1 is to check adjacent cells in a row
        int diffSign = (int) Math.signum(difference);
        // /-jump
        if (absDifference == 32) {
            int cellIDTopLeft = Math.min(currentCellID, targetCellID) - 2;
            int cellIDBottomRight = Math.max(currentCellID, targetCellID) + 2;
            if (diagJumpVerifier.hasDiagJumpReqs(cellIDTopLeft, diffSign, -1, -17)) {
                return true;
            }
            if (diagJumpVerifier.hasDiagJumpReqs(cellIDBottomRight, diffSign, +17, +1)) {
                return true;
            }
            // \-jump
        } else if (absDifference == 36) {
            int cellIDTopRight = Math.min(currentCellID, targetCellID) + 2;
            int cellIBottomLeft = Math.max(currentCellID, targetCellID) - 2;
            if (diagJumpVerifier.hasDiagJumpReqs(cellIDTopRight, diffSign, +1, -17)) {
                return true;
            }
            if (diagJumpVerifier.hasDiagJumpReqs(cellIBottomLeft, diffSign, +17, -1)) {
                return true;
            }
        }
        return false;
    }

}
