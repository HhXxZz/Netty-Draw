package com.hxz.example.common;

public enum ResultEnum {
    SUCCESS(0,"",""),
    SESSIONID_INVALID(10001,"SESSIONID_INVALID","SESSION失效"),
    REQUEST_PARAMETER_ERROR(10002,"PARAMETER_ERROR",""),
    REQUEST_METHOD_ERROR(10003,"METHOD_ERROR","请求方法错误"),
    SYSTEM_ERROR(500,"SYSTEM_ERROR","系统错误")
    ;

    private Integer code;
    private String msg;
    private String desc;

    ResultEnum(Integer code, String msg,String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDesc() {
        return desc;
    }
}
