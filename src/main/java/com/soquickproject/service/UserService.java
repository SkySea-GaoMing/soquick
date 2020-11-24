package com.soquickproject.service;

import com.soquickproject.error.BusinessException;
import com.soquickproject.service.model.UserModel;

public interface UserService {
    //通过对象ID获取用户
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel validateLogin(String telphone,String encrptPassword) throws BusinessException;
}
