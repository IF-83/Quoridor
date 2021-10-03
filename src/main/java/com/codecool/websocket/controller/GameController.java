package com.codecool.websocket.controller;


import com.codecool.websocket.models.SendResponse;
import com.codecool.websocket.models.Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;


@Controller
public class GameController {

        @MessageMapping("/hello")
        @SendTo("/topic/greetings")
        public SendResponse greeting(Response message) throws Exception {
            Thread.sleep(1000); // simulated delay
            //String data = HtmlUtils.htmlEscape(message.getName());
            return new SendResponse("[{\"type\":\"stepField\"}]");
        }

}


