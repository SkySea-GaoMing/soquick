package com.soquickproject.response;

import lombok.Data;

@Data
public class CommonReturnType {
    //表名对应请求的处理返回结果，success或fail
    private String status;
    //如果是success则data返回前端需要的数据
    //如果是fail则data使用通用的错误码格式
    private Object data;
    //定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }
    public static CommonReturnType create(Object result,String status){
        CommonReturnType type=new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

}
