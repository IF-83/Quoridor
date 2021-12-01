package com.codecool.websocket;

import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.GameData;
import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.ActionValidator;
import com.codecool.websocket.service.BoardState;
import com.codecool.websocket.service.GameState;
import com.codecool.websocket.service.actionCheckers.MovementChecker;
import com.codecool.websocket.service.actionCheckers.WallPlacementChecker;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StepJumpCheckTest {
    String cellsJson = "[\n" +
            "    {\n" +
            "        \"type\": \"stepField\",\n" +
            "        \"player\": \"player0\",\n" +
            "        \"direction\":\"none\",\n" +
            "        \"wallType\":\"none\",\n" +
            "        \"id\":1\n" +
            "    },\n" +
            "]";

    @Mock WallPlacementChecker wallPlacementChecker;
    @Mock MovementChecker movementChecker;
    @Mock BoardState boardState;
    @Mock Cell cell;

    @Test
    void tryPlayerActionChecksMovementWhenCellIsStepField () {
        int targetCellID = 1;
        MoveOutcomeType expectedOutcome = MoveOutcomeType.STUB_RESPONSE;
        when(boardState.getCell(targetCellID)).thenReturn(cell);
        when(cell.getType()).thenReturn("stepField");
        when(movementChecker.checkMovement()).thenReturn(MoveOutcomeType.STUB_RESPONSE);
        ActionValidator actionValidator = new ActionValidator(movementChecker, wallPlacementChecker, boardState, targetCellID);
        MoveOutcomeType actualOutcome = actionValidator.tryPlayerAction();
        assertEquals(expectedOutcome, actualOutcome);
    }

//    @Test
//    void tryPlayerActionExecutesMovementWhenMovementSuccessful() {
//
//    }

}
