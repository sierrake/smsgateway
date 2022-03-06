package com.kesierra.client;

import com.alibaba.fastjson.JSONObject;
import com.kesierra.entity.SmsRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class SmsServiceClient {
    @Autowired
    private RestTemplate restTemplate;
    private static String mokeHost = "mock-sms-server";
    private static String mokePort = "8080";

    @PostConstruct
    void init() {
        //${MOKE_SERVER_HOST}:${MOKE_SERVER_PORT}
        String tempHost = System.getenv("MOKE_SERVER_HOST");
        System.out.println("tempHost"+tempHost);
        String tempPort = System.getenv("MOKE_SERVER_PORT");

        if (!StringUtils.isEmpty(tempHost)){
            mokeHost = tempHost;
        }
        if (!StringUtils.isEmpty(tempPort)){
            mokePort = tempPort;
        }

    }
    public JSONObject smsSend(SmsRequestBody requestBody){
        //
        String url = "http://"+mokeHost+":"+mokePort+"/v2/emp/templateSms/sendSms";
        JSONObject json = restTemplate.postForEntity(url, requestBody, JSONObject.class).getBody();
        return json;
    }



}
