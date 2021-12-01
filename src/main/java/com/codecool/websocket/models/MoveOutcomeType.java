package com.codecool.websocket.models;

public enum MoveOutcomeType {
    NO_WALLS_LEFT ("You do not have any walls left!"),
    INVALID_WALL_PLACEMENT ("You can not place a wall there!"),
    INVALID_STEP ("That's not a valid move!"),
    SUCCESS(""),
    PLAYER_SURROUNDED("You can not capture players in between walls!"),
    STUB_RESPONSE("");
    String errorMsg;

    MoveOutcomeType(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg (){
        return this.errorMsg;
    }
}
