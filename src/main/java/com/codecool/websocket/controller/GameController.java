package com.codecool.websocket.controller;


import com.codecool.websocket.models.Request;
import com.codecool.websocket.models.Response;
import com.codecool.websocket.storage.Game;
import com.codecool.websocket.storage.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;

@RestController
@Controller
public class GameController {
    @Autowired
    private GameRepository gamerep;
    @Autowired
    private SimpMessagingTemplate simpleMessagingTemplate;

    @MessageMapping("/game/{gameId}")
        //@SendTo("/topic/greetings/{gameId}")
        public void greeting(@DestinationVariable String gameId, Request request) throws Exception {
        System.out.println("Meghívtam az endpointot ");
        Optional<Game> game = gamerep.findById(Long.valueOf(gameId));
        if(game.isPresent()){
            System.out.println("I hate Java");
        } else {
            System.out.println("I do hate React too");
        }

    }
//        String cellId = HtmlUtils.htmlEscape(request.getCellId());
//        String player = HtmlUtils.htmlEscape(request.getPlayer());
//        System.out.println(player);
//                if (game.getNextPlayer().equals(player)) {
//                    game.setNextPlayer("player1");
//                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",new Response(cellId,player));
//                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",new Response(cellId,player));
//                } else if(game.getNextPlayer().equals(player)) {
//                    game.setNextPlayer("player2");
//                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",new Response(cellId,player));
//                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",new Response(cellId,player));
//                }else {
//                    throw new IllegalArgumentException("Meghaltál");
//                }
//            }


    @GetMapping("/fetchNextGame")
    @CrossOrigin
    public Response fetchGame(){
        Game game = new Game();
        String player = "player1";
        game.setNextPlayer(player);
        Long gameId = gamerep.save(game).getGameId();
        Response resp = Response.builder()
                .gameId(gameId)
                .player(player)
                .build();
        return resp;
    }
}


