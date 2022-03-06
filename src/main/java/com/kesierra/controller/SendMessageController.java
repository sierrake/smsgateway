package com.kesierra.controller;

import com.kesierra.entity.Template;
import com.kesierra.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMessageController {
    @Autowired
    private MessageService messageService;
    @PostMapping(value = {"/directmessage"}, produces = {"application/json"})
    public String directmessage(@RequestParam String tels,@RequestParam String qos,
                                 @RequestParam String userName,@RequestParam String sessionId,
                                 @RequestBody Template titleContent){
        return messageService.directMessage(tels,qos,userName,sessionId,titleContent);
    }
    @PostMapping(value = {"/directmessage1"}, produces = {"application/json"})
    public String directmessage1(@RequestParam String tels,@RequestParam String qos,
                                @RequestParam String userName,@RequestParam String sessionId,
                                @RequestBody Template titleContent){
        return messageService.directMessagebak(tels,qos,userName,sessionId,titleContent);
    }
}
