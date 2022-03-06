package com.kesierra.service;

import com.kesierra.entity.Message;
import com.kesierra.entity.Template;
import com.kesierra.vo.Reponse;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
    public String directMessage(String tels, String qos, String userName, String sessionId, Template message);
    public String directMessagebak(String tels, String qos, String userName, String sessionId, Template message);
}
