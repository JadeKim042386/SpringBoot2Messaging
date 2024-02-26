package joo.example.messagestompwebsocket.controller;

import joo.example.messagestompwebsocket.dto.Greeting;
import joo.example.messagestompwebsocket.dto.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello") //publish
    @SendTo("/topic/greetings") //subscribe
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay(1s)
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.name()) + "!");
    }
}
