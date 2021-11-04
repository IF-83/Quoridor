package com.codecool.websocket.service.actionCheckers;

import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.BoardState;

public class MovementChecker {

    private int targetCellID;
    private BoardState boardState;
    private DiagJumpValidator diagJumpValidator;

    public MovementChecker(int targetCellID, BoardState boardState, DiagJumpValidator diagJumpValidator) {
        this.targetCellID = targetCellID;
        this.boardState = boardState;
        this.diagJumpValidator = diagJumpValidator;
    }

    public MoveOutcomeType checkMovement() {
        int currentCellID = boardState.findNextPlayerCellID();
        int difference = targetCellID - currentCellID;
        int absDifference = Math.abs(difference);
        boolean caseStep = absDifference == 2 || absDifference == 34; // 2 and 34 are the ID differences of horizontal and vertical steps
        boolean caseJump = absDifference == 4 || absDifference == 68; // 4 and 68 are the ID differences of horizontal and vertical jumps
        boolean caseDiagJump = absDifference == 36 || absDifference == 32; // 36 and 32 are the ID differences of diagonal jumps
        WallBetweenChecker wallBetweenChecker = new WallBetweenChecker(boardState);
        if (caseStep) {
            if (boardState.isWallBetween(targetCellID) || boardState.isOccupied(targetCellID)) {
                return MoveOutcomeType.INVALID_STEP;
            }
            boardState.executeStepOrJump(targetCellID);
            return MoveOutcomeType.SUCCESS;
        } else if (caseJump) {
            if (boardState.isWallBetween(targetCellID) || !boardState.isPlayerBetween(targetCellID)) {
                return MoveOutcomeType.INVALID_STEP;
            }
            boardState.executeStepOrJump(targetCellID);
            return MoveOutcomeType.SUCCESS;
        } else if (caseDiagJump) {
            if (!diagJumpValidator.isValidDiagonalJump()) {
                return MoveOutcomeType.INVALID_STEP;
            }
            boardState.executeStepOrJump(targetCellID);
            return MoveOutcomeType.SUCCESS;
        }
        return MoveOutcomeType.INVALID_STEP;
    }
}
