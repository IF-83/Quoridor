package com.codecool.websocket.service;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.actionCheckers.MovementChecker;
import com.codecool.websocket.service.actionCheckers.WallPlacementChecker;

public class ActionValidator {

    private final MovementChecker movementChecker;
    private final BoardState boardState;
    private final int targetCellID;
    private final WallPlacementChecker wallPlacementChecker;

    public ActionValidator(MovementChecker movementChecker, WallPlacementChecker wallPlacementChecker, BoardState boardState, int targetCellID) {
        this.movementChecker = movementChecker;
        this.boardState = boardState;
        this.targetCellID = targetCellID;
        this.wallPlacementChecker = wallPlacementChecker;
    }

    public MoveOutcomeType tryPlayerAction() {
        Cell cell = boardState.getCell(targetCellID);
        if (cell.getType().equals("stepField")) {
            MoveOutcomeType moverResult = movementChecker.checkMovement();
            if (moverResult == MoveOutcomeType.SUCCESS) {
                boardState.executeStepOrJump(targetCellID);
            }
            return moverResult;
        } else if (!cell.getType().equals("corner")){
            return wallPlacementChecker.checkWallPlacement(targetCellID);
        }else if (boardState.isPlayerBlocked()){
            return MoveOutcomeType.PLAYER_SURROUNDED;
        } else {
            return MoveOutcomeType.INVALID_WALL_PLACEMENT;
        }
    }


}
