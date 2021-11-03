package com.codecool.websocket.service.moveCheckers;

import com.codecool.websocket.models.MoveOutcomeType;

public class StepChecker {

    public MoveOutcomeType checkStep (int targetCellID, CheckUtility checkUtility) {
        int currentCellID = checkUtility.findNextPlayerCellID();
        int difference = targetCellID - currentCellID;
        int absDifference = Math.abs(difference);
        boolean caseStep = absDifference == 2 || absDifference == 34; // 2 and 34 are the ID differences of horizontal and vertical steps
        boolean caseJump = absDifference == 4 || absDifference == 68; // 4 and 68 are the ID differences of horizontal and vertical jumps
        boolean caseDiagJump = absDifference == 36 || absDifference == 32; // 36 and 32 are the ID differences of diagonal jumps
        WallBetweenChecker wallBetweenChecker = new WallBetweenChecker(checkUtility);
        if (caseStep) {
            if (wallBetweenChecker.isWallBetween(currentCellID, targetCellID) || isOccupied(targetCellID)) {
                return MoveOutcomeType.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        } else if (caseJump) {
            if (wallBetweenChecker.isWallBetween(currentCellID, targetCellID) || !isPlayerBetween(currentCellID, targetCellID)) {
                return MoveOutcomeType.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        } else if (caseDiagJump) {
            if (!isValidDiagonalJump(absDifference, difference, currentCellID, targetCellID)) {
                return MoveOutcomeType.INVALID_STEP;
            }
            return executeStepOrJump(currentCellID, targetCellID);
        }
        return MoveOutcomeType.INVALID_STEP;
    }
}
