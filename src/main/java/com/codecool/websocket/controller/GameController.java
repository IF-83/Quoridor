package com.codecool.websocket.controller;


import com.codecool.websocket.models.Request;
import com.codecool.websocket.models.Response;
import com.codecool.websocket.storage.GameData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@Controller
public class GameController {

    @Autowired
    private GameData gameData;

    @Autowired
    private SimpMessagingTemplate simpleMessagingTemplate;

    @MessageMapping("/hello/{gameId}")
        //@SendTo("/topic/greetings/{gameId}")
        public void greeting(@DestinationVariable String gameId, Request request) throws Exception {
        String cellId = HtmlUtils.htmlEscape(request.getCellId());
        String player = HtmlUtils.htmlEscape(request.getPlayer());

        System.out.println(player);
            if (Integer.valueOf(gameId) == 1 ) {
                if (player.equals("player2")){
                    simpleMessagingTemplate.convertAndSend("/topic/greetings/"+ gameId,new Response(cellId,"notYourTurn"));
                }
                simpleMessagingTemplate.convertAndSend("/topic/greetings/"+ gameId,new Response(cellId,player));
                //simpleMessagingTemplate.convertAndSend("/topic/greetings/"+ gameId,"[{\"cellId\":\""+ cellId + "\",\"player\": \"player2\",\"gameId\":\""+ gameId + "\"}]");
                }
            }

    @GetMapping("/fetchNextGame")
    @CrossOrigin
    public Response fetchGame(){
        Integer boardId = gameData.createGame();
        //Response resp = new Response(String.valueOf(boardId));
        Response resp = Response.builder()
                .boardId(String.valueOf(boardId))
                .build();
        return resp;
    }
}


