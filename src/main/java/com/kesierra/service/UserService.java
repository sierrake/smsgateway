package com.kesierra.service;

import com.kesierra.entity.Session;
import com.kesierra.entity.User;
import com.kesierra.vo.Reponse;
import com.kesierra.vo.UserLoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public Reponse register(User user);
    public UserLoginResponse login(User user);
    public Reponse logout(Session session );
}
