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
            System.out.println("Game FOUND");
            String cellId = HtmlUtils.htmlEscape(request.getCellId());
            String player = HtmlUtils.htmlEscape(request.getPlayer());
            System.out.println(player);
                if (game.get().getNextPlayer().equals(player)) {
                    game.get().setNextPlayer(player.equals("player1") ? "player2" : "player1");
                    gamerep.save(game.get());
                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",new Response(cellId,player));
                    simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",new Response(cellId,player));
                } else  {
                    simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/"+ player,Response.builder().invalidMove(true).build());
                    System.out.println(Response.builder().invalidMove(true).build());
                }
            }else {
            System.out.println("GAME NOT FOUND");
        }
    }


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


