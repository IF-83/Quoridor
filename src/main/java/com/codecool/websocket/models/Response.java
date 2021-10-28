package com.codecool.websocket.models;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Response {

    private String cellId;
    private String player;
    private Long gameId;
    private boolean invalidMove = false;
    private String errorMsg;
    private String winner;

    public Response (Long gameId){
        this.gameId = gameId;
    }

    public Response (String cellId, String player){
        this.cellId =cellId;
        this.player =player;
    }
}
