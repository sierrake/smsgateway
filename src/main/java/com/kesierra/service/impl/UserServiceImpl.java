package com.kesierra.service.impl;

import com.kesierra.entity.Session;
import com.kesierra.entity.User;
import com.kesierra.enumP.ResponseCode;
import com.kesierra.service.UserService;
import com.kesierra.vo.Reponse;
import com.kesierra.vo.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Reponse register(User user) {
        Reponse response = new Reponse();
        //check param
        if (!checkUerRegisterInfo(user)){
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
            return response;
        }
        //save register
        ValueOperations strOps = redisTemplate.opsForValue();
        boolean ifAbsent = strOps.setIfAbsent("user-"+user.getUserName(), user.getPassword());
        if (ifAbsent){
            response.setCode(ResponseCode.sucess.getCode());
            response.setMessage(ResponseCode.sucess.getMessage());
        }else{
            response.setCode(ResponseCode.Intenalrror.getCode());
            response.setMessage(ResponseCode.Intenalrror.getMessage());
        }

        return response;
    }

    @Override
    public UserLoginResponse login(User user) {
        UserLoginResponse response = new UserLoginResponse();
        //check user param
        if (!checkUerRegisterInfo(user)){
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
            return response;
        }

        //check user hasregister
        if (!hasRegister(user)){
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
            return response;
        }

        //generator session
        String uuid = UUID.randomUUID().toString();
        ValueOperations strOps = redisTemplate.opsForValue();
        strOps.set(uuid+":"+user.getUserName(),'1');

        response.setSessionId(uuid);
        response.setCode(ResponseCode.sucess.getCode());
        response.setMessage(ResponseCode.sucess.getMessage());

        return response;
    }

    @Override
    public Reponse logout(Session session) {
        Reponse response = new Reponse();
        if (checkLogoutInfo(session)){
            redisTemplate.delete(session.getSessionId() + ":" + session.getUserName());
            response.setCode(ResponseCode.sucess.getCode());
            response.setMessage(ResponseCode.sucess.getMessage());
        }else{
            response.setCode(ResponseCode.paramError.getCode());
            response.setMessage(ResponseCode.paramError.getMessage());
        }

        return response;
    }

    private boolean checkLogoutInfo(Session session){
        if (session == null){
            return false;
        }
        String userName = session.getUserName();
        String sessionId = session.getSessionId();
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(sessionId)){
            return false;
        }
        ValueOperations strOps = redisTemplate.opsForValue();
        Object isExist = strOps.get(sessionId + ":" + userName);
        if (isExist != null){
            return true;
        }
        return false;

    }

    private boolean hasRegister(User user){
        ValueOperations strOps = redisTemplate.opsForValue();
        Object password = strOps.get("user-" + user.getUserName());
        if (password == null){
            return false;
        }
        if (((String)password).equals(user.getPassword())){
            return true;
        }
        return false;
    }
    /***
     * check user info
     * @param user
     * @return
     */
    private boolean checkUerRegisterInfo(User user){
        if (user == null){
            return false;
        }
        String userName = user.getUserName();
        String password = user.getPassword();

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            return false;
        }

        //check username
        Pattern userPattern = Pattern.compile("[a-zA-Z0-9]{3,32}");
        Matcher matcher = userPattern.matcher(userName);
        if (!matcher.matches()){
            return false;
        }

        //check password
        Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9]{8,64}");
        matcher = userPattern.matcher(userName);
        if (!matcher.matches()){
            return false;
        }

        return true;
    }
}
