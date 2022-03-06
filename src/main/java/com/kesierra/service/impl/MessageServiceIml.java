package com.kesierra.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kesierra.client.SmsServiceClient;
import com.kesierra.entity.SmsRequestBody;
import com.kesierra.entity.Template;
import com.kesierra.enumP.ResponseCode;
import com.kesierra.handler.MessageLimitHandler;
import com.kesierra.service.MessageService;
import com.kesierra.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageServiceIml implements MessageService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SmsServiceClient smsServiceClient;
    @Autowired
    private MessageLimitHandler messageLimitHandler;

    @Override
    public String directMessage(String tels, String qos, String userName,
                                 String sessionId, Template message) {
        Reponse response = new Reponse();
        if (!checkParam(tels,qos,userName,sessionId,message)){
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
            return JSONObject.toJSONString(response);
        }
        if (!checkSession(userName,sessionId)){
            response.setCode(ResponseCode.notAuth.getCode());
            response.setMessage(ResponseCode.notAuth.getMessage());
            return JSONObject.toJSONString(response);
        }

        //send message
        SmsRequestBody requestBoby = new SmsRequestBody();
        requestBoby.setQos(qos);
        requestBoby.setAcceptor_tel(tels);
        requestBoby.setTemplate_param(message);
        return JSONObject.toJSONString(messageLimitHandler.sendMessage(requestBoby));
        //return smsServiceClient.smsSend(requestBoby).toJSONString();
    }
    public String directMessagebak(String tels, String qos, String userName,
                                String sessionId, Template message) {
        Reponse response = new Reponse();
        if (!checkParam(tels,qos,userName,sessionId,message)){
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
            return JSONObject.toJSONString(response);
        }
        if (!checkSession(userName,sessionId)){
            response.setCode(ResponseCode.notAuth.getCode());
            response.setMessage(ResponseCode.notAuth.getMessage());
            return JSONObject.toJSONString(response);
        }

        //send message
        SmsRequestBody requestBoby = new SmsRequestBody();
        requestBoby.setQos(qos);
        requestBoby.setAcceptor_tel(tels);
        requestBoby.setTemplate_param(message);
        return smsServiceClient.smsSend(requestBoby).toJSONString();
    }

    private boolean checkParam(String tels, String qos, String userName,
                               String sessionId, Template message){

        if (StringUtils.isEmpty(tels) || StringUtils.isEmpty(qos)
                || StringUtils.isEmpty(userName) || StringUtils.isEmpty(sessionId) || message == null){
            return false;
        }
        if (!isPhone(tels)){
            return false;
        }
        if (!checkQos(qos)){
            return false;
        }

        return checkMess(message);
    }
    private boolean checkSession(String userName,String sessionId){
        ValueOperations strOps = redisTemplate.opsForValue();
        Object isExist = strOps.get(sessionId + ":" + userName);
        if (isExist != null){
            return true;
        }
        return false;
    }
    private boolean checkQos(String qos){
        Pattern p = Pattern.compile("[1-3]");
        Matcher m = p.matcher(qos);
        return m.matches();
    }
    private  boolean isPhone(String tels) {
//        String phone = tels;
//        //check china phone
//        if (tels.startsWith("+86")){
//            phone = tels.substring(3);
//            //china phone
//            String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
//            if (phone.length() != 11) {
//                return false;
//            } else {
//                Pattern p = Pattern.compile(regex);
//                Matcher m = p.matcher(phone);
//                return m.matches();
//            }
                Pattern p = Pattern.compile("[0-9+]*");
                Matcher m = p.matcher(tels);
                return m.matches();
     //   }

        //return true;
    }
    private boolean checkMess(Template message){
        String title = message.getTitle();
        String content = message.getContent();
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(content)){
            return false;
        }
        int titleLen = title.length();
        if (titleLen < 1 || titleLen > 64){
            return false;
        }
        return true;
    }



}
