package com.soquickproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.soquickproject.controller.viewobject.UserVO;
import com.soquickproject.error.BusinessException;
import com.soquickproject.error.EmBusinessError;
import com.soquickproject.response.CommonReturnType;
import com.soquickproject.service.UserService;
import com.soquickproject.service.model.UserModel;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws  BusinessException{
        UserModel userModel=userService.getUserById(id);
        //用户信息不存在
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO= convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }
    public UserVO convertFromModel(UserModel userModel){
        if(userModel==null)
            return null;
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
    //用户获取otp短信接口
    @RequestMapping(value="soquick/getotp",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(value="telphone",required = false)String telphone){
        System.out.println("接收到请求了吗");
        //按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);
        //将OTP验证码同用户手机号关联，使用httpsession的方式绑定手机号与OTPCODE
        httpServletRequest.getSession().setAttribute(telphone, otpCode);
        String infomation=(String) this.httpServletRequest.getSession().getAttribute(telphone);
        System.out.println(infomation);
        //将OTP验证码通过短信发送给用户
        System.out.println(telphone+" "+otpCode);
        return CommonReturnType.create(null);
    }
    @RequestMapping(value="soquick/register",method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType register(@RequestParam(value = "telphone") String telphone,
                                     @RequestParam(value = "otpCode") String otpCode,
                                     @RequestParam(value = "name") String name,
                                     @RequestParam(value = "gender") String gender,
                                     @RequestParam(value = "age") String age,
                                     @RequestParam(value = "password") String password)
            throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.println("到达这一步了吗");
        System.out.println(telphone+" "+otpCode+" "+name+" "+gender+" "+age+" "+password);
        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        System.out.println(inSessionOtpCode);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");

        //密码加密
        userModel.setEncrptPassword(DigestUtils.md5Hex(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }
    @RequestMapping(value = "soquick/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，用来校验用户登录是否合法
        //用户加密后的密码
        UserModel userModel = userService.validateLogin(telphone, DigestUtils.md5Hex(password));

        //将登陆凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);

    }

}
