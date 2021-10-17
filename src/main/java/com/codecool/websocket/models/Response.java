package com.codecool.websocket.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Response {

    private String cellId;
    private String player;
    private Long gameId;
    public Response (Long gameId){
        this.gameId = gameId;
    }

    public Response (String cellId, String player){
        this.cellId =cellId;
        this.player =player;
    }
}
