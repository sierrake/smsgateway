package com.kesierra.enumP;

public enum ResponseCode {
    sucess(200,"success"),notAuth(403,"禁止未经授权访问"),paramError(400,"请求参数错误"),Intenalrror(500,"内部服务器错误");
    private int code;
    private String message;

    ResponseCode(int code,String message){
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
