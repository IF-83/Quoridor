package com.codecool.websocket.controller;

import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.GameLogic;
import com.google.gson.reflect.TypeToken;
import com.codecool.websocket.models.Request;
import com.codecool.websocket.models.Response;
import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.Game;
import com.codecool.websocket.models.repository.GameRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
        Optional<Game> optionalGame = gamerep.findById(Long.valueOf(gameId));
        if(optionalGame.isPresent()){
            Game game = optionalGame.get();
            String cellIDString = HtmlUtils.htmlEscape(request.getCellId());
            String player = HtmlUtils.htmlEscape(request.getPlayer());
            GameLogic gameLogic = new GameLogic(game, Integer.parseInt(cellIDString));
                if (game.getNextPlayer().equals(player)) {
                    gameLogic.tryMove();
                    if(gameLogic.getMoveOutcomeType() == MoveOutcomeType.SUCCESS) {
                        gameLogic.whoHasWon();
                        String winner = gameLogic.getWinner();
                        if (winner != null) {
                            simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/" + "player1", Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                            simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/" + "player2", Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                        }
                        game.setNextPlayer(player.equals("player1") ? "player2" : "player1");
                        game.setJsonFromCells(gameLogic.getCells());
                        gamerep.save(game);
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",new Response(cellIDString,player));
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",new Response(cellIDString,player));
                    }else{
                        simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/"+ player,Response.builder().invalidMove(true).errorMsg(gameLogic.getMoveOutcomeType().getErrorMsg()).build());
                    }

                } else {
                    simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/"+ player,Response.builder().invalidMove(true).errorMsg("Not your turn!").build());
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
        String file = null;
        try {
            file = Files.readString(Paths.get("./src/main/resources/initBoard.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Cell> cells = gson.fromJson(file,new TypeToken<List<Cell>>() {}.getType());
        game.setJsonFromCells(cells);
        Long gameId = gamerep.save(game).getGameId();
        Response resp = Response.builder()
                .gameId(gameId)
                .player(player)
                .build();
        return resp;
    }
}


