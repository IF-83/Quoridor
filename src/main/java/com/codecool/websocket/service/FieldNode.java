package com.codecool.websocket.service;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FieldNode {

    private int winType = 0; // 0 = neutral, 1 = winning for player1, 2 = winning for player2

    private FieldNode left = null;
    private FieldNode right = null;
    private FieldNode up = null;
    private FieldNode down = null;

    public FieldNode(int winType) {
        this.winType = winType;
    }

}
