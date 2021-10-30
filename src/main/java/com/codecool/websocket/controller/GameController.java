package com.codecool.websocket.controller;

import com.codecool.websocket.models.MoveOutcomeType;
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
        System.out.println("Meghívtam az endpointot ");
        Optional<Game> optionalGame = gamerep.findById(Long.valueOf(gameId));
        if(optionalGame.isPresent()){
            Game game = optionalGame.get();
            game.setCellsFromJson();
            System.out.println("Game FOUND");
            String cellId = HtmlUtils.htmlEscape(request.getCellId());
            String player = HtmlUtils.htmlEscape(request.getPlayer());
            System.out.println(player);
                if (game.getNextPlayer().equals(player)
                        && game.tryMove(Integer.valueOf(cellId)) == MoveOutcomeType.SUCCESS) {
                    game.whoHasWon();
                    String winner = game.getWinner();
                    if (winner != null) {
                        System.out.println("winner is : " + winner);
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                    }
//                    game.executeStep(Integer.valueOf(cellId));
                    game.setNextPlayer(player.equals("player1") ? "player2" : "player1");
                    game.setJsonFromCells();
                    gamerep.save(game);
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
        String file = null;
        try {
            file = Files.readString(Paths.get("./src/main/resources/initBoard.json"));
            System.out.println(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Cell> cells = gson.fromJson(file,new TypeToken<List<Cell>>() {}.getType());
        //cells.forEach(x -> x.setGame(game));
        System.out.println(cells);
        game.setCells(cells);
        game.setJsonFromCells();
        System.out.println(game);
        Long gameId = gamerep.save(game).getGameId();
        //Optional<Game> game2 = gamerep.findById(Long.valueOf(gameId));
        //if (game2.isPresent()){
        //    System.out.println(game2.get().getCellsJson());
        //}
        Response resp = Response.builder()
                .gameId(gameId)
                .player(player)
                .build();
        return resp;
    }
}


