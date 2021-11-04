package com.codecool.websocket.controller;

import com.codecool.websocket.models.MoveOutcomeType;
import com.codecool.websocket.service.ActionValidator;
import com.codecool.websocket.service.BoardState;
import com.codecool.websocket.service.GameState;
import com.codecool.websocket.service.actionCheckers.DiagJumpValidator;
import com.codecool.websocket.service.actionCheckers.DiagJumpVerifier;
import com.codecool.websocket.service.actionCheckers.MovementChecker;
import com.codecool.websocket.service.actionCheckers.WallPlacementChecker;
import com.google.gson.reflect.TypeToken;
import com.codecool.websocket.models.Request;
import com.codecool.websocket.models.Response;
import com.codecool.websocket.models.Cell;
import com.codecool.websocket.models.GameData;
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
        Optional<GameData> optionalGameData = gamerep.findById(Long.valueOf(gameId));
        if(optionalGameData.isPresent()){
            GameData gameData = optionalGameData.get();
            int targetCellID = Integer.parseInt(HtmlUtils.htmlEscape(request.getCellId()));
            String player = HtmlUtils.htmlEscape(request.getPlayer());
            GameState gameState = new GameState(gameData, targetCellID);
                if (gameData.getNextPlayer().equals(player)) {

                    BoardState boardState = new BoardState(gameState.getCells(), gameState.getNextPlayer());
                    DiagJumpVerifier diagJumpVerifier = new DiagJumpVerifier(boardState);
                    DiagJumpValidator diagJumpValidator = new DiagJumpValidator(diagJumpVerifier, boardState, targetCellID);
                    MovementChecker movementChecker = new MovementChecker(targetCellID, boardState, diagJumpValidator);
                    WallPlacementChecker wallPlacementChecker = new WallPlacementChecker(gameState);
                    ActionValidator actionValidator = new ActionValidator(movementChecker, wallPlacementChecker, boardState, targetCellID);
                    gameState.setMoveOutcomeType(actionValidator.tryPlayerAction());

                    if(gameState.getMoveOutcomeType() == MoveOutcomeType.SUCCESS) {
                        gameState.whoHasWon();
                        String winner = gameState.getWinner();
                        if (winner != null) {
                            simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/" + "player1", Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                            simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/" + "player2", Response.builder().winner("PLAYER " + winner + " HAS WON THE GAME").build());
                        }
                        gameData.setNextPlayer(player.equals("player1") ? "player2" : "player1");
                        gameData.setJsonFromCells(gameState.getCells());
                        gamerep.save(gameData);
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player1",new Response(String.valueOf(targetCellID),player));
                        simpleMessagingTemplate.convertAndSend("/runninggame/"+ gameId + "/" + "player2",new Response(String.valueOf(targetCellID),player));
                    }else{
                        simpleMessagingTemplate.convertAndSend("/runninggame/" + gameId + "/"+ player,Response.builder().invalidMove(true).errorMsg(gameState.getMoveOutcomeType().getErrorMsg()).build());
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
        GameData gameData = new GameData();
        String player = "player1";
        gameData.setNextPlayer(player);
        String file = null;
        try {
            file = Files.readString(Paths.get("./src/main/resources/initBoard.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Cell> cells = gson.fromJson(file,new TypeToken<List<Cell>>() {}.getType());
        gameData.setJsonFromCells(cells);
        Long gameId = gamerep.save(gameData).getGameId();
        Response resp = Response.builder()
                .gameId(gameId)
                .player(player)
                .build();
        return resp;
    }
}


