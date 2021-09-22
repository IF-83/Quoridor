package com.codecool.websocket.controller;


import com.codecool.websocket.models.Greeting;
import com.codecool.websocket.models.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.bind.annotation.CrossOrigin;


@Controller
public class GreetingController {

        @MessageMapping("/hello")
        @SendTo("/topic/greetings")
        public Greeting greeting(Message message) throws Exception {
            Thread.sleep(1000); // simulated delay
            return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
        }

}


